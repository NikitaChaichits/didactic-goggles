package com.example.vpn.ui.settings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.vpn.R
import com.example.vpn.common.base.BaseFragment
import com.example.vpn.databinding.FragmentMainBinding
import com.example.vpn.databinding.FragmentSettingsBinding

class SettingsFragment : BaseFragment(R.layout.fragment_settings) {

    private val binding by viewBinding(FragmentSettingsBinding::bind)
    override val viewModel: SettingsFragmentViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
    }

}
