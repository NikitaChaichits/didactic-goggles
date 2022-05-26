package com.example.vpn.ui.settings.support

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.vpn.R
import com.example.vpn.common.base.BaseFragment
import com.example.vpn.databinding.FragmentSupportBinding

class SupportFragment : BaseFragment(R.layout.fragment_support) {

    private val binding by viewBinding(FragmentSupportBinding::bind)
    override val viewModel: SupportFragmentViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
    }

}
