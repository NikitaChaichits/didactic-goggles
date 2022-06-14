package com.example.vpn.ui.main

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.VpnService
import android.os.Bundle
import android.os.RemoteException
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.vpn.R
import com.example.vpn.common.base.BaseFragment
import com.example.vpn.data.source.local.SharedPreferencesDataSource
import com.example.vpn.data.vpn.interfaces.ChangeServer
import com.example.vpn.data.vpn.util.CheckInternetConnection
import com.example.vpn.databinding.FragmentMainBinding
import com.example.vpn.domain.model.Country
import com.example.vpn.ui.main.Status.*
import com.example.vpn.ui.connection.alert.adapter.IssuesResultAdapter
import com.example.vpn.ui.main.adapter.BottomSheetAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import de.blinkt.openvpn.OpenVpnApi
import de.blinkt.openvpn.core.OpenVPNService
import de.blinkt.openvpn.core.OpenVPNThread
import kotlinx.coroutines.launch
import java.io.*

@AndroidEntryPoint
class MainFragment : BaseFragment(R.layout.fragment_main), ChangeServer {

    private val binding by viewBinding(FragmentMainBinding::bind)
    override val viewModel by activityViewModels<MainFragmentViewModel>()
    private val adapter by lazy { BottomSheetAdapter(::chooseCountry) }

    private lateinit var prefs : SharedPreferencesDataSource
    private lateinit var behavior: BottomSheetBehavior<ConstraintLayout>
    private val countryList: MutableList<Country> = mutableListOf()

    private var connection: CheckInternetConnection? = null
    private var vpnStart = false
    private var country: String = ""
    private var config: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAll()
        initListeners()
        observeViewModels()
    }

    private fun initAll() {
        prefs = SharedPreferencesDataSource(requireContext())
        viewModel.getListFromApi(prefs.getCountryName())
        connection = CheckInternetConnection()
    }

    private fun initListeners() {
        binding.ivSettings.setOnClickListener {
            navigate(R.id.action_main_fragment_to_settings_fragment)
        }
        binding.btnStartStop.setOnClickListener {
            if (behavior.state == BottomSheetBehavior.STATE_EXPANDED)
                behavior.state = BottomSheetBehavior.STATE_COLLAPSED

            if (vpnStart) {
                confirmDisconnect()
            } else {
                viewModel.setStatus(CONNECTING.value)
                prepareVpn()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeViewModels() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.serverConfig.collect {
                config = it
                binding.btnStartStop.isClickable = it != ""
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.country.collect {
                country = it
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.vpnStart.collect {
                vpnStart = it
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.status.collect { status ->
                when (status) {
                    CONNECT.value -> {
                        binding.btnStartStop.isEnabled = true
                        binding.ivSettings.isEnabled = true
                        binding.tvBtnName.text = resources.getString(R.string.fr_main_btn_start)
                        binding.btnStartStop.setColorFilter(
                            resources.getColor(R.color.appBackground))
                        binding.tvStatus.text = resources.getString(R.string.fr_main_press_to_connect)
                    }
                    CONNECTING.value -> {
                        binding.btnStartStop.isEnabled = true
                        binding.ivSettings.isEnabled = false
                        binding.tvBtnName.text = resources.getString(R.string.fr_main_btn_stop)
                        binding.btnStartStop.setColorFilter(
                            resources.getColor(R.color.appBackground))
                        binding.tvStatus.text  = resources.getString(R.string.fr_main_connecting)
                    }
                    CONNECTED.value -> {
                        binding.btnStartStop.isEnabled = true
                        binding.ivSettings.isEnabled = true
                        binding.tvBtnName.text = resources.getString(R.string.fr_main_btn_stop)
                        binding.btnStartStop.setColorFilter(
                            resources.getColor(R.color.shareButton_background))
                        binding.tvStatus.text = resources.getString(R.string.fr_main_press_to_disconnect)
                    }
                    RECONNECTING.value -> {
                        binding.btnStartStop.isEnabled = false
                        binding.ivSettings.isEnabled = false
                        binding.tvBtnName.text = resources.getString(R.string.fr_main_btn_stop)
                        binding.btnStartStop.setColorFilter(
                            resources.getColor(R.color.appBackground))
                        binding.tvStatus.text  = resources.getString(R.string.fr_main_reconnecting)
                    }
                    TRY_DIFFERENT_SERVER.value -> {}
                    LOADING.value -> {}
                    INVALID_DEVICE.value -> {}
                    AUTHENTICATION_CHECK.value -> {}
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.countryList.collect { list ->
                countryList.clear()
                countryList.addAll(list)
                adapter.notifyDataSetChanged()

                if (!list.isNullOrEmpty())
                    setCountry(list, prefs.getCountryName())
            }
        }
    }

    // Bottom Sheet

    override fun onStart() {
        super.onStart()
        setupBottomBehavior()
        setupRecyclerView()
    }

    private fun setupBottomBehavior() {
        behavior = BottomSheetBehavior.from(binding.bottomSheet)

        behavior.state = BottomSheetBehavior.STATE_COLLAPSED
        behavior.isHideable = false

        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {}

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                with(binding) {
                    if (slideOffset > 0) {
                        layoutCollapsed.alpha = 1 - 2 * slideOffset
                        layoutExpanded.alpha = slideOffset * slideOffset

                        if (slideOffset > 0.5) {
                            layoutCollapsed.visibility = View.GONE
                            layoutExpanded.visibility = View.VISIBLE
                        }

                        if (slideOffset < 0.5 && binding.layoutExpanded.visibility == View.VISIBLE) {
                            layoutCollapsed.visibility = View.VISIBLE
                            layoutExpanded.visibility = View.INVISIBLE
                        }
                    }
                }
            }
        })

        binding.imgClose.setOnClickListener {
            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private fun setupRecyclerView() {
        binding.rvCountries.adapter = adapter
        binding.rvCountries.layoutManager = LinearLayoutManager(requireContext())

        adapter.submitList(countryList)
    }


    private fun setCountry(countryList: List<Country>, countryName: String) {
        val country = countryList.find { it.shortName == countryName }

        setSingleItem(country)
    }

    private fun chooseCountry(itemPosition: Int) {
        val country = adapter.currentList[itemPosition]
        prefs.setCountryName(country.shortName)
        viewModel.setNewCountryList(country)
        setSingleItem(country)
        if (this::behavior.isInitialized)
            behavior.state = BottomSheetBehavior.STATE_COLLAPSED

        // reconnect
        try {
            if (vpnStart){
                stopVpn()
                viewModel.setStatus(RECONNECTING.value)
                startVpn()
            }
        }catch (e: Exception){
            Log.e("MainFragment","reconnecting error = " + e.message.toString())
        }

    }

    private fun setSingleItem(country: Country?) {
        binding.singleItem.visibility = View.VISIBLE
        binding.progressBar.visibility = View.INVISIBLE

        if (country != null){
            binding.include.ivFlag.setImageResource(country.flag)
            binding.include.tvCountry.text = country.fullName

            if (country.shortName == "US")
                binding.include.tvBestChoice.visibility = View.VISIBLE
            else
                binding.include.tvBestChoice.visibility = View.GONE
        } else{
            binding.include.ivFlag.setImageResource(R.drawable.ic_placeholder)
            binding.include.tvCountry.text ="Unknown country"
        }
    }


    // Work with VPN

    /**
     * Prepare for vpn connect with required permission
     */
    private fun prepareVpn() {
        if (!vpnStart) {
            if (getInternetStatus()) {
                // Checking permission for network monitor
                val intent = VpnService.prepare(context)
                if (intent != null) {
                    startActivityForResult(intent, 1)
                } else {
                    //have already permission
                    // Update confection status
                    startVpn()
                }
            } else {
                showToast("you have no internet connection !!")
            }
        } else if (stopVpn()) {
            showToast("Disconnect Successfully")
        }
    }

    private fun startVpn() {
        try {
            // .ovpn file
            val conf: InputStream = StringBufferInputStream(config)
            val isr = InputStreamReader(conf)
            val br = BufferedReader(isr)
            var config = ""
            var line: String?
            while (true) {
                line = br.readLine()
                if (line == null) break
                config += """
                $line

                """.trimIndent()
            }
            br.readLine()

            OpenVpnApi.startVpn(
                context,
                config,
                country,
                "vpn",
                "vpn"
            )

            viewModel.setVpnStart(true)
        } catch (e: IOException) {
            e.printStackTrace()
            showToast("Error! ${e.message}")
            viewModel.setStatus(CONNECT.value)
        } catch (e: RemoteException) {
            e.printStackTrace()
            showToast("Error! ${e.message}")
            viewModel.setStatus(CONNECT.value)
        }
    }

    /**
     * Receive broadcast message
     */
    private var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            try {
                setStatus(intent.getStringExtra("state"))
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            try {
                var duration = intent.getStringExtra("duration")
                var lastPacketReceive = intent.getStringExtra("lastPacketReceive")
                var byteIn = intent.getStringExtra("byteIn")
                var byteOut = intent.getStringExtra("byteOut")
                if (duration == null) duration = "00:00:00"
                if (lastPacketReceive == null) lastPacketReceive = "0"
                if (byteIn == null) byteIn = " "
                if (byteOut == null) byteOut = " "
                Log.d("Main Fragment",
                    "duration = $duration, lastPacketReceive = $lastPacketReceive, " +
                            "byteIn = $byteIn, byteOut = $byteOut")
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Status change with corresponding vpn connection status
     * @param connectionState
     */
    fun setStatus(connectionState: String?) {
        if (connectionState != null) when (connectionState) {
            "DISCONNECTED" -> {
                //TODO handle this status
                viewModel.setStatus(CONNECT.value)
                viewModel.setVpnStart(false)
                OpenVPNService.setDefaultStatus()
            }
            "CONNECTED" -> {
                viewModel.setStatus(CONNECTED.value)
                viewModel.setVpnStart(true) // it will use after restart this activity
            }
//            "WAIT" -> binding.tvStatus.text = "waiting for server connection!!"
//            "AUTH" -> binding.tvStatus.text = "server authenticating!!"
//            "RECONNECTING" -> binding.tvStatus.text = "Reconnecting..."
//            "NONETWORK" -> binding.tvStatus.text = "No network connection"
        }
    }

    /**
     * Show show disconnect confirm dialog
     */
    private fun confirmDisconnect() {
        val builder = AlertDialog.Builder(
            requireActivity()
        )
        builder.setMessage(requireActivity().getString(R.string.connection_close_confirm))
        builder.setPositiveButton(
            requireActivity().getString(R.string.yes)
        ) { dialog, id -> stopVpn() }
        builder.setNegativeButton(
            requireActivity().getString(R.string.no)
        ) { dialog, id ->
            // User cancelled the dialog
        }

        val dialog = builder.create()
        dialog.show()
    }

    /**
     * Stop vpn
     * @return boolean: VPN status
     */
    private fun stopVpn(): Boolean {
        try {
            OpenVPNThread.stop()
            viewModel.setStatus(CONNECT.value)
            viewModel.setVpnStart(false)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    /**
     * Internet connection status.
     */
    private fun getInternetStatus(): Boolean {
        return connection!!.netCheck(requireContext())
    }

    /**
     * Show toast message
     * @param message: toast message
     */
    private fun showToast(message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    /**
     * Change server when user select new server
     * @param server ovpn server details
     */
    override fun newServer() {
        if (vpnStart) { stopVpn() }
        prepareVpn()
    }

    override fun onResume() {
        LocalBroadcastManager.getInstance(requireActivity())
            .registerReceiver(broadcastReceiver, IntentFilter("connectionState"))
        super.onResume()
    }

    override fun onPause() {
        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(broadcastReceiver)
        super.onPause()
    }

}
