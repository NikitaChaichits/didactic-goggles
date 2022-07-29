package com.cyberself.vpn.ui.settings.speedtest

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.cyberself.vpn.R
import com.cyberself.vpn.common.base.BaseFragment
import com.cyberself.speedtest.core.Speedtest
import com.cyberself.speedtest.core.Speedtest.ServerSelectedHandler
import com.cyberself.speedtest.core.Speedtest.SpeedtestHandler
import com.cyberself.speedtest.core.serverSelector.TestPoint
import com.cyberself.vpn.databinding.FragmentSpeedTestBinding
import com.cyberself.vpn.ui.settings.speedtest.State.*
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


class   SpeedTestFragment : BaseFragment(R.layout.fragment_speed_test) {

    private val binding by viewBinding(FragmentSpeedTestBinding::bind)
    override val viewModel: SpeedTestViewModel by viewModels()


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
                        INITIAL.name -> {
                            binding.tvStatus.visibility = View.INVISIBLE
                            binding.pbCalculating.visibility = View.INVISIBLE
                            binding.btnStartStop.visibility = View.INVISIBLE
                            binding.tvBtnName.visibility = View.INVISIBLE
                            binding.ivStop.visibility = View.INVISIBLE
                            binding.ivBackgroundResult.visibility = View.INVISIBLE
                            binding.llResult.visibility = View.INVISIBLE
                            binding.llInitial.visibility = View.VISIBLE
                        }
                        READY.name -> {
                            binding.tvStatus.visibility = View.VISIBLE
                            binding.tvStatus.setText(R.string.fr_speed_test_press_to_check)
                            binding.pbCalculating.visibility = View.INVISIBLE
                            binding.btnStartStop.visibility = View.VISIBLE
                            binding.tvBtnName.visibility = View.VISIBLE
                            binding.tvBtnName.setText(R.string.fr_speed_test_start)
                            binding.ivStop.visibility = View.INVISIBLE
                            binding.ivBackgroundResult.visibility = View.INVISIBLE
                            binding.llResult.visibility = View.INVISIBLE
                            binding.llInitial.visibility = View.INVISIBLE
                        }
                        CALCULATING.name -> {
                            binding.tvStatus.visibility = View.VISIBLE
                            binding.tvStatus.setText(R.string.fr_speed_test_calculation)
                            binding.pbCalculating.visibility = View.VISIBLE
                            binding.btnStartStop.visibility = View.INVISIBLE
                            binding.tvBtnName.visibility = View.INVISIBLE
                            binding.ivStop.visibility = View.VISIBLE
                            binding.ivBackgroundResult.visibility = View.VISIBLE
                            binding.llResult.visibility = View.INVISIBLE
                            binding.llInitial.visibility = View.INVISIBLE
                        }
                        DONE.name -> {
                            binding.tvStatus.visibility = View.VISIBLE
                            binding.tvStatus.setText(R.string.fr_speed_test_press_to_check)
                            binding.pbCalculating.visibility = View.INVISIBLE
                            binding.btnStartStop.visibility = View.VISIBLE
                            binding.tvBtnName.visibility = View.VISIBLE
                            binding.tvBtnName.setText(R.string.fr_speed_test_restart)
                            binding.ivStop.visibility = View.INVISIBLE
                            binding.ivBackgroundResult.visibility = View.VISIBLE
                            binding.llResult.visibility = View.VISIBLE
                            binding.llInitial.visibility = View.INVISIBLE
                            binding.btnStartStop.setOnClickListener {
                                initScreen()
                            }
                        }
                        ERROR.name -> {
                            binding.tvStatus.visibility = View.VISIBLE
                            binding.tvStatus.setText(R.string.fr_speed_test_error)
                            binding.pbCalculating.visibility = View.INVISIBLE
                            binding.btnStartStop.visibility = View.VISIBLE
                            binding.tvBtnName.visibility = View.VISIBLE
                            binding.tvBtnName.setText(R.string.fr_speed_test_restart)
                            binding.ivStop.visibility = View.INVISIBLE
                            binding.ivBackgroundResult.visibility = View.VISIBLE
                            binding.tvDownloadValue.text =
                                "0 ${resources.getString(R.string.fr_speed_test_mbps)}"
                            binding.tvUploadValue.text =
                                "0 ${resources.getString(R.string.fr_speed_test_mbps)}"
                            binding.tvPingValue.text =
                                "0 ${resources.getString(R.string.fr_speed_test_ms)}"
                            binding.llResult.visibility = View.VISIBLE
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
                activity?.runOnUiThread { viewModel.setState(INITIAL.name) }

                val servers: Array<TestPoint> = arrayOf(server)

                try {
                    if (st != null) {
                        try {
                            st?.abort()
                        } catch (e: Throwable) {
                            Log.e("SpeedTestFragment", e.message.toString())
                        }
                    }
                    st = Speedtest()
                    st?.addTestPoints(servers)
                } catch (e: Throwable) {
                    System.err.println(e)
                    st = null
                    activity?.runOnUiThread { viewModel.setState(ERROR.name) }

                    Log.e(
                        "SpeedTestFragment",
                        "${getString(R.string.initFail_configError)} + \": \" + ${e.message}"
                    )

                    return
                }
                st?.selectServer(object : ServerSelectedHandler() {
                    override fun onServerSelected(server: TestPoint?) {
                        if (server == null) {
                            activity?.runOnUiThread { viewModel.setState(ERROR.name) }
                            Log.e("SpeedTestFragment", getString(R.string.initFail_noServers))
                        } else {
                            selectServerScreen()
                        }
                    }
                })
            }
        }.start()
    }

    private fun selectServerScreen() {
        activity?.runOnUiThread {
            viewModel.setState(READY.name)
            reInitOnResume = true

            binding.btnStartStop.setOnClickListener {
                reInitOnResume = false
                calculationScreen()
                binding.btnStartStop.setOnClickListener(null)
            }
        }
    }

    private fun calculationScreen() {
        activity?.runOnUiThread { viewModel.setState(CALCULATING.name) }
        st?.setSelectedServer(server)

        binding.ivStop.setOnClickListener {
            st?.abort()
            activity?.runOnUiThread { viewModel.setState(ERROR.name) }
        }

        val circleProgressBar = binding.pbCalculating
        circleProgressBar.setProgress(0f)
        circleProgressBar.setStartPositionInDegrees(-90)

        st?.start(object : SpeedtestHandler() {

            @SuppressLint("SetTextI18n")
            override fun onPingJitterUpdate(ping: Double, jitter: Double, progress: Double) {
                activity?.runOnUiThread(Runnable {
                    circleProgressBar.setProgress((100 * progress).toFloat() / 5)
                    if (progress == 1.0) {
                        binding.tvPingValue.text =
                            "${format(ping)} ${resources.getString(R.string.fr_speed_test_ms)}"
                    }
                })
            }

            @SuppressLint("SetTextI18n")
            override fun onDownloadUpdate(dl: Double, progress: Double) {
                activity?.runOnUiThread(Runnable {
                    circleProgressBar.setProgress(20f + (100 * progress).toFloat() / 2.5f)
                    if (progress == 1.0) {
                        binding.tvDownloadValue.text =
                            "${format(dl)} ${resources.getString(R.string.fr_speed_test_mbps)}"
                    }
                })
            }

            @SuppressLint("SetTextI18n")
            override fun onUploadUpdate(ul: Double, progress: Double) {
                activity?.runOnUiThread {
                    circleProgressBar.setProgress(60f + (100 * progress).toFloat() / 2.5f)
                    if (progress == 1.0) {
                        binding.tvUploadValue.text =
                            "${format(ul)} ${resources.getString(R.string.fr_speed_test_mbps)}"
                        activity?.runOnUiThread { viewModel.setState(DONE.name) }

                    }
                }
            }

            override fun onIPInfoUpdate(ipInfo: String) {}
            override fun onTestIDReceived(id: String, shareURL: String) {}
            override fun onEnd() {}

            override fun onCriticalFailure(err: String) {
                activity?.runOnUiThread { viewModel.setState(ERROR.name) }
                Log.e("SpeedTestFragment", getString(R.string.testFail_err) + "error: $err")
            }
        })
    }

    private fun format(d: Double): String {
        val l = resources.configuration.locales[0]
        if (d < 10) return String.format(l, "%.2f", d)
        return if (d < 100) String.format(l, "%.1f", d) else "" + d.roundToInt()
    }

    private companion object {
        private val server =
            TestPoint(
                "Bari, Italy (GARR)",
                "https://st-be-ba1.infra.garr.it",
                "garbage.php",
                "empty.php",
                "empty.php",
                "getIP.php"
            )
    }
}
