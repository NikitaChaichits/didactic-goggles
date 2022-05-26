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
        observeViewModel()
    }

}
