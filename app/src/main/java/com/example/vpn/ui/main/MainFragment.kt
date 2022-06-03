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
import androidx.fragment.app.viewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.vpn.R
import com.example.vpn.common.base.BaseFragment
import com.example.vpn.databinding.FragmentMainBinding
import com.example.vpn.data.vpn.interfaces.ChangeServer
import com.example.vpn.domain.model.Server
import com.example.vpn.data.vpn.util.CheckInternetConnection
import com.example.vpn.data.vpn.util.SharedPreference
import dagger.hilt.android.AndroidEntryPoint
import de.blinkt.openvpn.OpenVpnApi
import de.blinkt.openvpn.core.OpenVPNService
import de.blinkt.openvpn.core.OpenVPNThread
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

@AndroidEntryPoint
class MainFragment : BaseFragment(R.layout.fragment_main), ChangeServer {

    private val binding by viewBinding(FragmentMainBinding::bind)
    override val viewModel: MainFragmentViewModel by viewModels()

    private var server: Server? = null
    private var connection: CheckInternetConnection? = null
    private var preference: SharedPreference? = null
    private var vpnStart = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
        initializeAll()
    }

    private fun initListeners() {
        binding.ivSettings.setOnClickListener {
            navigate(R.id.action_main_fragment_to_settings_fragment)
        }
        binding.btnStartStop.setOnClickListener {
            BottomFragment().show(requireActivity().supportFragmentManager, "Bottom Sheet")
        }

        binding.vpnBtn.setOnClickListener {
            // Vpn is running, user would like to disconnect current connection.
            if (vpnStart) {
                confirmDisconnect()
            } else {
                prepareVpn()
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
                    status("connecting")
                    startVpn()
                }

            } else {

                // No internet connection available
                showToast("you have no internet connection !!")
            }
        } else if (stopVpn()) {

            // VPN is stopped, show a Toast message.
            showToast("Disconnect Successfully")
        }
    }

    /**
     * Initialize all variable and object
     */
    private fun initializeAll() {
        preference = SharedPreference(requireContext())
        server = preference?.getServer()

        // Update current selected server icon
//        updateCurrentServerIcon(server?.flagUrl)
        connection = CheckInternetConnection()
    }

    /**
     * Start the VPN
     */
    private fun startVpn() {
        try {
            // .ovpn file
            val conf = requireActivity().assets.open(server?.ovpn!!)
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
            Log.d("MainFragment", "config = $config")
            br.readLine()
            OpenVpnApi.startVpn(
                context,
                config,
                server?.country,
                server?.ovpnUserName,
                server?.ovpnUserPassword
            )

            // Update log
            binding.logTv.text = "Connecting..."
            vpnStart = true
        } catch (e: IOException) {
            e.printStackTrace()
            showToast("Error! ${e.message}")
            status("connect")
        } catch (e: RemoteException) {
            e.printStackTrace()
            showToast("Error! ${e.message}")
            status("connect")
        }
    }

    /**
     * Update status UI
     * @param duration: running time
     * @param lastPacketReceive: last packet receive time
     * @param byteIn: incoming data
     * @param byteOut: outgoing data
     */
    fun updateConnectionStatus(
        duration: String,
        lastPacketReceive: String,
        byteIn: String,
        byteOut: String
    ) {
        binding.durationTv.text = "Duration: $duration"
        binding.lastPacketReceiveTv.text = "Packet Received: $lastPacketReceive second ago"
        binding.byteInTv.text = "Bytes In: $byteIn"
        binding.byteOutTv.text = "Bytes Out: $byteOut"
    }

    /**
     * Receive broadcast message
     */
    var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
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
                updateConnectionStatus(duration, lastPacketReceive, byteIn, byteOut)
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
                status("connect")
                vpnStart = false
                OpenVPNService.setDefaultStatus()
                binding.logTv.text = ""
            }
            "CONNECTED" -> {
                vpnStart = true // it will use after restart this activity
                status("connected")
                binding.logTv.text = ""
            }
            "WAIT" -> binding.logTv.text = "waiting for server connection!!"
            "AUTH" -> binding.logTv.text = "server authenticating!!"
            "RECONNECTING" -> {
                status("connecting")
                binding.logTv.text = "Reconnecting..."
            }
            "NONETWORK" -> binding.logTv.text = "No network connection"
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

        // Create the AlertDialog
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
            status("connect")
            vpnStart = false
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
     * Change button background color and text
     * @param status: VPN current status
     */
    private fun status(status: String) {
        when (status) {
            "connect" -> {
                binding.vpnBtn.text = requireContext().getString(R.string.connect)
            }
            "connecting" -> {
                binding.vpnBtn.text = requireContext().getString(R.string.connecting)
            }
            "connected" -> {
                binding.vpnBtn.text = requireContext().getString(R.string.disconnect)
            }
            "tryDifferentServer" -> {
                binding.vpnBtn.setBackgroundResource(R.drawable.button_connected)
                binding.vpnBtn.text = "Try Different\nServer"
            }
            "loading" -> {
                binding.vpnBtn.setBackgroundResource(R.drawable.button)
                binding.vpnBtn.text = "Loading Server.."
            }
            "invalidDevice" -> {
                binding.vpnBtn.setBackgroundResource(R.drawable.button_connected)
                binding.vpnBtn.text = "Invalid Device"
            }
            "authenticationCheck" -> {
                binding.vpnBtn.setBackgroundResource(R.drawable.button_connecting)
                binding.vpnBtn.text = "Authentication \n Checking..."
            }
        }
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
    override fun newServer(server: Server?) {
//        updateCurrentServerIcon(server?.flagUrl)

        // Stop previous connection
        if (vpnStart) {
            stopVpn()
        }
        prepareVpn()
    }


    override fun onResume() {
        LocalBroadcastManager.getInstance(requireActivity())
            .registerReceiver(broadcastReceiver, IntentFilter("connectionState"))
        if (server == null) {
            server = preference?.getServer()
        }
        super.onResume()
    }

    override fun onPause() {
        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(broadcastReceiver)
        super.onPause()
    }

    /**
     * Save current selected server on local shared preference
     */
    override fun onStop() {
        if (server != null) {
            preference?.saveServer(server!!)
        }
        super.onStop()
    }
}
