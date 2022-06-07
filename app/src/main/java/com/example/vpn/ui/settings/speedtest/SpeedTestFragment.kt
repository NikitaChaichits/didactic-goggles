package com.example.vpn.ui.settings.speedtest

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.vpn.R
import com.example.vpn.common.base.BaseFragment
import com.example.vpn.databinding.FragmentSpeedTestBinding

class SpeedTestFragment : BaseFragment(R.layout.fragment_speed_test) {

    private val binding by viewBinding(FragmentSpeedTestBinding::bind)
    override val viewModel: SpeedTestFragmentViewModel by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
        initListeners()
    }

    private fun initObservers() {
        viewModel.run {
            state.observe(viewLifecycleOwner) { state ->
                when(state){
                    State.INITIAL.value -> {
                        binding.tvStatus.setText(R.string.fr_speed_test_press_to_check)
                        binding.tvBtnName.visibility = View.VISIBLE
                        binding.tvBtnName.setText(R.string.fr_speed_test_start)
                        binding.ivStop.visibility = View.INVISIBLE
                        binding.ivBackgroundResult.visibility = View.INVISIBLE
                        binding.llResult.visibility = View.INVISIBLE
                    }
                    State.CHECKING.value -> {
                        binding.tvStatus.setText(R.string.fr_speed_test_calculation)
                        binding.tvBtnName.visibility = View.INVISIBLE
                        binding.ivStop.visibility = View.VISIBLE
                        binding.ivBackgroundResult.visibility = View.VISIBLE
                        binding.llResult.visibility = View.INVISIBLE
                    }
                    State.DONE.value -> {
                        binding.tvStatus.setText(R.string.fr_speed_test_press_to_check)
                        binding.tvBtnName.visibility = View.VISIBLE
                        binding.tvBtnName.setText(R.string.fr_speed_test_restart)
                        binding.ivStop.visibility = View.INVISIBLE
                        binding.ivBackgroundResult.visibility = View.VISIBLE
                        binding.llResult.visibility = View.VISIBLE
                        binding.tvDownloadValue.text = "156 ${resources.getString(R.string.fr_speed_test_mbps)}"
                        binding.tvUploadValue.text = "132 ${resources.getString(R.string.fr_speed_test_mbps)}"
                        binding.tvPingValue.text = "16 ${resources.getString(R.string.fr_speed_test_ms)}"
                    }
                    State.ERROR.value -> {
                        binding.tvStatus.setText(R.string.fr_speed_test_error)
                        binding.tvBtnName.visibility = View.VISIBLE
                        binding.tvBtnName.setText(R.string.fr_speed_test_restart)
                        binding.ivStop.visibility = View.INVISIBLE
                        binding.ivBackgroundResult.visibility = View.VISIBLE
                        binding.llResult.visibility = View.VISIBLE
                        binding.tvDownloadValue.text = "0 ${resources.getString(R.string.fr_speed_test_mbps)}"
                        binding.tvUploadValue.text = "0 ${resources.getString(R.string.fr_speed_test_mbps)}"
                        binding.tvPingValue.text = "0 ${resources.getString(R.string.fr_speed_test_ms)}"
                    }
                }
            }
        }
    }

    private fun initListeners() {
        binding.btnStartStop.setOnClickListener {
            when (viewModel.state.value){
                State.INITIAL.value -> viewModel.setState(State.CHECKING.value)
                State.CHECKING.value -> viewModel.setState(State.ERROR.value)
                State.DONE.value -> viewModel.setState(State.CHECKING.value)
                State.ERROR.value -> viewModel.setState(State.CHECKING.value)
            }
        }

        binding.btnBack.setOnClickListener {
            viewModel.setState(State.INITIAL.value)
            navigateBack()
        }
    }
}
