package com.cyberself.vpn.ui.privacy

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.cyberself.vpn.R
import com.cyberself.vpn.common.base.BaseFragment
import com.cyberself.vpn.databinding.FragmentPrivacyBinding

class PrivacyFragment : BaseFragment(R.layout.fragment_privacy) {

    private val binding by viewBinding(FragmentPrivacyBinding::bind)
    override val viewModel: PrivacyViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
    }

    private fun initListeners() {
        binding.btnContinue.setOnClickListener {
            navigate(R.id.action_privacy_fragment_to_intro_fragment)
        }
    }

}