package com.example.vpn.ui.main

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
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.vpn.R
import com.example.vpn.common.base.BaseFragment
import com.example.vpn.data.vpn.interfaces.ChangeServer
import com.example.vpn.data.vpn.util.CheckInternetConnection
import com.example.vpn.databinding.FragmentMainBinding
import dagger.hilt.android.AndroidEntryPoint
import de.blinkt.openvpn.OpenVpnApi
import de.blinkt.openvpn.core.OpenVPNService
import de.blinkt.openvpn.core.OpenVPNThread
import kotlinx.coroutines.launch
import com.example.vpn.ui.main.Status.*
import java.io.*

@AndroidEntryPoint
class MainFragment : BaseFragment(R.layout.fragment_main), ChangeServer {

    private val binding by viewBinding(FragmentMainBinding::bind)
    override val viewModel by activityViewModels<MainFragmentViewModel>()

    private var connection: CheckInternetConnection? = null
    private var vpnStart = false
    private var country: String = ""
    private var config: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        connection = CheckInternetConnection()
        initListeners()
        observeViewModels()
        binding.btnBottomSheet.callOnClick()
    }

    private fun initListeners() {
        binding.ivSettings.setOnClickListener {
            navigate(R.id.action_main_fragment_to_settings_fragment)
        }
        binding.btnStartStop.setOnClickListener {
            if (vpnStart) {
                confirmDisconnect()
            } else {
                prepareVpn()
            }
        }
        binding.btnBottomSheet.setOnClickListener {
            BottomFragment().show(requireActivity().supportFragmentManager, "Bottom Sheet")
        }
    }

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
    }

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
                    status(CONNECTING.value)
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

            binding.tvStatus.text = "Connecting..."
            viewModel.setVpnStart(true)
        } catch (e: IOException) {
            e.printStackTrace()
            showToast("Error! ${e.message}")
            status(CONNECT.value)
        } catch (e: RemoteException) {
            e.printStackTrace()
            showToast("Error! ${e.message}")
            status(CONNECT.value)
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
                status(CONNECT.value)
                viewModel.setVpnStart(false)
                OpenVPNService.setDefaultStatus()
                binding.tvStatus.text = ""
            }
            "CONNECTED" -> {
                viewModel.setVpnStart(true) // it will use after restart this activity
                status(CONNECTED.value)
                binding.tvStatus.text = ""
            }
            "WAIT" -> binding.tvStatus.text = "waiting for server connection!!"
            "AUTH" -> binding.tvStatus.text = "server authenticating!!"
            "RECONNECTING" -> {
                status(CONNECTING.value)
                binding.tvStatus.text = "Reconnecting..."
            }
            "NONETWORK" -> binding.tvStatus.text = "No network connection"
        }
    }


    private fun status(status: String) {
        when (status) {
            CONNECT.value -> {
                binding.tvBtnName.text = resources.getString(R.string.fr_main_btn_start)
                binding.btnStartStop.setColorFilter(
                    resources.getColor(R.color.appBackground))
            }
            CONNECTING.value -> {
                binding.tvBtnName.text = resources.getString(R.string.fr_main_btn_stop)
                binding.btnStartStop.setColorFilter(
                    resources.getColor(R.color.appBackground))
            }
            CONNECTED.value -> {
                binding.tvBtnName.text = resources.getString(R.string.fr_main_btn_stop)
                binding.btnStartStop.setColorFilter(
                        resources.getColor(R.color.shareButton_background))
            }
            TRY_DIFFERENT_SERVER.value -> {}
            LOADING.value -> {}
            INVALID_DEVICE.value -> {}
            AUTHENTICATION_CHECK.value -> {}
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
            status(CONNECT.value)
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
