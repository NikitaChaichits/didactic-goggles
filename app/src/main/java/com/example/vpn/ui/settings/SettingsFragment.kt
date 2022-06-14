package com.example.vpn.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.vpn.R
import com.example.vpn.common.base.BaseFragment
import com.example.vpn.databinding.FragmentSettingsBinding
import com.example.vpn.ui.connection.AdActivity

class SettingsFragment : BaseFragment(R.layout.fragment_settings) {

    private val binding by viewBinding(FragmentSettingsBinding::bind)
    override val viewModel: SettingsFragmentViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
    }

    private fun initListeners() {
        binding.btnBack.setOnClickListener { navigateBack() }
        binding.ivBanner.setOnClickListener {
            navigate(SettingsFragmentDirections.actionSettingsFragmentToSubscriptionFragment(true))
        }
        binding.btnCheckConnection.setOnClickListener {
            navigate(R.id.action_settings_fragment_to_speed_test_fragment)
        }
        binding.btnContactSupport.setOnClickListener {
            navigate(R.id.action_settings_fragment_to_support_fragment)
        }
        binding.btnRateApp.setOnClickListener {
            val intent = Intent(requireContext(), AdActivity::class.java)
            activity?.startActivity(intent)
        }
    }

}
