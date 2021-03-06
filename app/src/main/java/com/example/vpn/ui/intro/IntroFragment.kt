package com.example.vpn.ui.intro

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.vpn.R
import com.example.vpn.common.base.BaseFragment
import com.example.vpn.databinding.FragmentIntroBinding
import com.example.vpn.util.view.invisible
import com.example.vpn.util.view.visible

class IntroFragment : BaseFragment(R.layout.fragment_intro) {

    private val binding by viewBinding(FragmentIntroBinding::bind)
    override val viewModel: IntroViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListeners()
        initObservers()
    }

    private fun initListeners(){
        binding.btnContinue.setOnClickListener { viewModel.onBtnContinueClick() }
    }

    private fun initObservers(){
        viewModel.run {
            goToNextScreen.liveData.observe(viewLifecycleOwner){
                findNavController().navigate(R.id.action_intro_fragment_to_subscription_fragment)
            }
            showSecondIntroScreen.liveData.observe(viewLifecycleOwner){
                binding.groupFirstIntro.invisible()
                binding.groupSecondIntro.visible()
            }
        }
    }

}
