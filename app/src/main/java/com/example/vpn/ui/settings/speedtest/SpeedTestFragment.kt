package com.example.vpn.ui.settings.speedtest

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.vpn.R
import com.example.vpn.common.base.BaseFragment
import com.example.vpn.core.Speedtest
import com.example.vpn.core.Speedtest.ServerSelectedHandler
import com.example.vpn.core.Speedtest.SpeedtestHandler
import com.example.vpn.core.config.SpeedtestConfig
import com.example.vpn.core.config.TelemetryConfig
import com.example.vpn.core.serverSelector.TestPoint
import com.example.vpn.databinding.FragmentSpeedTestBinding
import com.example.vpn.ui.settings.speedtest.State.*
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.EOFException
import java.io.InputStreamReader
import kotlin.math.roundToInt


class SpeedTestFragment : BaseFragment(R.layout.fragment_speed_test) {

    private val binding by viewBinding(FragmentSpeedTestBinding::bind)
    override val viewModel: SpeedTestFragmentViewModel by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
        initListeners()
        initScreen()
    }

    @SuppressLint("SetTextI18n")
    private fun initObservers() {
        viewModel.run {
            viewLifecycleOwner.lifecycleScope.launch {
                state.collect { state ->
                    when (state) {
                        INITIAL.value -> {
                            binding.tvStatus.visibility = View.INVISIBLE
                            binding.pbCalculating.visibility = View.INVISIBLE
                            binding.btnStartStop.visibility = View.INVISIBLE
                            binding.tvBtnName.visibility = View.INVISIBLE
                            binding.ivStop.visibility = View.INVISIBLE
                            binding.ivBackgroundResult.visibility = View.INVISIBLE
                            binding.tvServerName.visibility = View.INVISIBLE
                            binding.llResult.visibility = View.INVISIBLE
                            binding.llSelect.visibility = View.INVISIBLE
                            binding.llInitial.visibility = View.VISIBLE
                        }
                        SELECT.value -> {
                            binding.tvStatus.visibility = View.VISIBLE
                            binding.tvStatus.setText(R.string.fr_speed_test_press_to_check)
                            binding.pbCalculating.visibility = View.INVISIBLE
                            binding.btnStartStop.visibility = View.VISIBLE
                            binding.tvBtnName.visibility = View.VISIBLE
                            binding.tvBtnName.setText(com.example.vpn.R.string.fr_speed_test_start)
                            binding.ivStop.visibility = View.INVISIBLE
                            binding.ivBackgroundResult.visibility = View.VISIBLE
                            binding.tvServerName.visibility = View.VISIBLE
                            binding.llResult.visibility = View.INVISIBLE
                            binding.llSelect.visibility = View.VISIBLE
                            binding.llInitial.visibility = View.INVISIBLE

                        }
                        CALCULATING.value -> {
                            binding.tvStatus.visibility = View.VISIBLE
                            binding.tvStatus.setText(R.string.fr_speed_test_calculation)
                            binding.pbCalculating.visibility = View.VISIBLE
                            binding.btnStartStop.visibility = View.INVISIBLE
                            binding.tvBtnName.visibility = View.INVISIBLE
                            binding.ivStop.visibility = View.VISIBLE
                            binding.ivBackgroundResult.visibility = View.VISIBLE
                            binding.tvServerName.visibility = View.VISIBLE
                            binding.llResult.visibility = View.INVISIBLE
                            binding.llSelect.visibility = View.INVISIBLE
                            binding.llInitial.visibility = View.INVISIBLE
                        }
                        DONE.value -> {
                            binding.tvStatus.visibility = View.VISIBLE
                            binding.tvStatus.setText(R.string.fr_speed_test_press_to_check)
                            binding.pbCalculating.visibility = View.INVISIBLE
                            binding.btnStartStop.visibility = View.VISIBLE
                            binding.tvBtnName.visibility = View.VISIBLE
                            binding.tvBtnName.setText(R.string.fr_speed_test_restart)
                            binding.ivStop.visibility = View.INVISIBLE
                            binding.ivBackgroundResult.visibility = View.VISIBLE
                            binding.tvServerName.visibility = View.VISIBLE
                            binding.llResult.visibility = View.VISIBLE
                            binding.llSelect.visibility = View.INVISIBLE
                            binding.llInitial.visibility = View.INVISIBLE
                            binding.btnStartStop.setOnClickListener {
                                initScreen()
                            }
                        }
                        ERROR.value -> {
                            binding.tvStatus.visibility = View.VISIBLE
                            binding.tvStatus.setText(R.string.fr_speed_test_error)
                            binding.pbCalculating.visibility = View.INVISIBLE
                            binding.btnStartStop.visibility = View.VISIBLE
                            binding.tvBtnName.visibility = View.VISIBLE
                            binding.tvBtnName.setText(R.string.fr_speed_test_restart)
                            binding.ivStop.visibility = View.INVISIBLE
                            binding.ivBackgroundResult.visibility = View.VISIBLE
                            binding.tvServerName.visibility = View.VISIBLE
                            binding.tvDownloadValue.text =
                                "0 ${resources.getString(R.string.fr_speed_test_mbps)}"
                            binding.tvUploadValue.text =
                                "0 ${resources.getString(R.string.fr_speed_test_mbps)}"
                            binding.tvPingValue.text =
                                "0 ${resources.getString(R.string.fr_speed_test_ms)}"
                            binding.llResult.visibility = View.VISIBLE
                            binding.llSelect.visibility = View.INVISIBLE
                            binding.llInitial.visibility = View.INVISIBLE
                            binding.btnStartStop.setOnClickListener {
                                initScreen()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun initListeners() {
        binding.btnBack.setOnClickListener {
            navigateBack()
        }
    }


    // speed test

    private var st: Speedtest? = null
    private var reInitOnResume = false

    private fun initScreen() {
        object : Thread() {
            override fun run() {
                activity?.runOnUiThread{ viewModel.setState(INITIAL.value) }
                var config: SpeedtestConfig? = null
                var telemetryConfig: TelemetryConfig? = null
                var servers: Array<TestPoint>? = null
                try {
                    var c: String = readFileFromAssets("SpeedtestConfig.json")!!
                    var o = JSONObject(c)
                    config = SpeedtestConfig(o)
                    c = readFileFromAssets("TelemetryConfig.json")!!
                    o = JSONObject(c)
                    telemetryConfig = TelemetryConfig(o)
                    if (st != null) {
                        try {
                            st?.abort()
                        } catch (e: Throwable) {
                            Log.e("SpeedTestFragment", e.message.toString())
                        }
                    }
                    st = Speedtest()
                    st?.setSpeedtestConfig(config)
                    st?.setTelemetryConfig(telemetryConfig)
                    c = readFileFromAssets("ServerList.json")!!
                    if (c.startsWith("\"") || c.startsWith("'")) { //fetch server list from URL
                        if (!st!!.loadServerList(
                                c.subSequence(1, c.length - 1).toString()
                            )
                        ) {
                            throw Exception("Failed to load server list")
                        }
                    } else { //use provided server list
                        val a = JSONArray(c)
                        if (a.length() == 0) throw Exception("No test points")
                        val s = ArrayList<TestPoint>()
                        for (i in 0 until a.length()) s.add(TestPoint(a.getJSONObject(i)))
                        servers = s.toTypedArray()
                        st?.addTestPoints(servers)
                    }
                } catch (e: Throwable) {
                    System.err.println(e)
                    st = null
                    activity?.runOnUiThread{ viewModel.setState(ERROR.value) }

                    Log.e("SpeedTestFragment",
                        "${getString(R.string.initFail_configError)} + \": \" + ${e.message}")

                    return
                }
                st?.selectServer(object : ServerSelectedHandler() {
                    override fun onServerSelected(server: TestPoint?) {
                        if (server == null) {
                            activity?.runOnUiThread{ viewModel.setState(ERROR.value) }
                            Log.e("SpeedTestFragment", getString(R.string.initFail_noServers))
                        } else {
                            selectServerScreen(server, st!!.testPoints)
                        }
                    }
                })
            }
        }.start()
    }

    private fun selectServerScreen(selected: TestPoint, servers: Array<TestPoint>) {
        activity?.runOnUiThread {
            viewModel.setState(SELECT.value)
            reInitOnResume = true
            val availableServers = ArrayList<TestPoint>()
            for (t in servers) {
                if (t.ping != -1f) availableServers.add(t)
            }
            val selectedId = availableServers.indexOf(selected)
            val spinner = binding.spSelectServer
            val options = ArrayList<String>()
            for (t in availableServers) {
                options.add(t.name)
            }
            val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
                requireContext(),
                R.layout.spinner_item,
                options.toTypedArray()
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
            spinner.setSelection(selectedId)

            binding.btnStartStop.setOnClickListener {
                reInitOnResume = false
                calculationScreen(availableServers[spinner.selectedItemPosition])
                binding.btnStartStop.setOnClickListener(null)
            }
        }
    }

    private fun calculationScreen(selected: TestPoint) {
        activity?.runOnUiThread{ viewModel.setState(CALCULATING.value) }
        st?.setSelectedServer(selected)
        binding.tvServerName.text = selected.name //remove

        binding.ivStop.setOnClickListener {
            st?.abort()
            activity?.runOnUiThread{ viewModel.setState(ERROR.value) }
        }

        val circleProgressBar = binding.pbCalculating
        circleProgressBar.setProgress(0f)
        circleProgressBar.setStartPositionInDegrees(-90)

        st?.start(object : SpeedtestHandler() {

            @SuppressLint("SetTextI18n")
            override fun onPingJitterUpdate(ping: Double, jitter: Double, progress: Double) {
                activity?.runOnUiThread(Runnable {
                    circleProgressBar.setProgress((100 * progress).toFloat()/5)
                    if(progress == 1.0){
                        binding.tvPingValue.text =
                            "${format(ping)} ${resources.getString(R.string.fr_speed_test_ms)}"
                    }
                })
            }

            @SuppressLint("SetTextI18n")
            override fun onDownloadUpdate(dl: Double, progress: Double) {
                activity?.runOnUiThread(Runnable {
                    circleProgressBar.setProgress(20f + (100 * progress).toFloat()/2.5f)
                    if(progress == 1.0){
                        binding.tvDownloadValue.text =
                            "${format(dl)} ${resources.getString(R.string.fr_speed_test_mbps)}"
                    }
                })
            }

            @SuppressLint("SetTextI18n")
            override fun onUploadUpdate(ul: Double, progress: Double) {
                activity?.runOnUiThread {
                    circleProgressBar.setProgress(60f + (100 * progress).toFloat()/2.5f)
                    if (progress == 1.0){
                        binding.tvUploadValue.text =
                            "${format(ul)} ${resources.getString(R.string.fr_speed_test_mbps)}"
                        activity?.runOnUiThread{ viewModel.setState(DONE.value) }

                    }
                }
            }

            override fun onIPInfoUpdate(ipInfo: String) {}
            override fun onTestIDReceived(id: String, shareURL: String) {}
            override fun onEnd() {}

            override fun onCriticalFailure(err: String) {
                activity?.runOnUiThread{ viewModel.setState(ERROR.value) }
                Log.e("SpeedTestFragment", getString(R.string.testFail_err) + "error: $err")
            }
        })
    }

    private fun format(d: Double): String {
        val l = resources.configuration.locales[0]
        if (d < 10) return String.format(l, "%.2f", d)
        return if (d < 100) String.format(l, "%.1f", d) else "" + d.roundToInt()
    }

    @Throws(Exception::class)
    private fun readFileFromAssets(name: String): String? {
        val b = BufferedReader(InputStreamReader(activity?.assets?.open(name)))
        var ret: String? = ""
        try {
            while (true) {
                val s = b.readLine() ?: break
                ret += s
            }
        } catch (e: EOFException) {
        }
        return ret
    }
}
