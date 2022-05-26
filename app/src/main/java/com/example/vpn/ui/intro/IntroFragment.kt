package com.example.vpn.ui.intro

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.vpn.R
import com.example.vpn.common.base.BaseFragment
import com.example.vpn.databinding.FragmentIntroBinding

class IntroFragment : BaseFragment(R.layout.fragment_intro) {

    private val binding by viewBinding(FragmentIntroBinding::bind)
    override val viewModel: IntroViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
    }


}
