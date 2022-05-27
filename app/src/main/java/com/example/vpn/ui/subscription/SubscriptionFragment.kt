package com.example.vpn.ui.subscription

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.vpn.R
import com.example.vpn.common.base.BaseFragment
import com.example.vpn.databinding.FragmentMainBinding
import com.example.vpn.databinding.FragmentSubscriptionBinding

class SubscriptionFragment : BaseFragment(R.layout.fragment_subscription) {

    private val binding by viewBinding(FragmentSubscriptionBinding::bind)
    override val viewModel: SubscriptionViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()

        initListeners()
    }

    private fun initListeners() {
        binding.btnContinue.setOnClickListener {
            findNavController().navigate(R.id.action_subscription_fragment_to_main_fragment)
        }
    }


}
