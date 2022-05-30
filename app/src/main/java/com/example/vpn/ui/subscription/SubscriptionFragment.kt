package com.example.vpn.ui.subscription

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.vpn.R
import com.example.vpn.common.base.BaseFragment
import com.example.vpn.databinding.FragmentSubscriptionBinding
import com.example.vpn.util.view.Subscription
import com.example.vpn.util.view.visible

class SubscriptionFragment : BaseFragment(R.layout.fragment_subscription) {

    private val args: SubscriptionFragmentArgs by navArgs()

    private val binding by viewBinding(FragmentSubscriptionBinding::bind)
    override val viewModel: SubscriptionViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (args.isThreeTypesSubscription)
            binding.subscriptionWeek.visible()

        initListeners()
        binding.subscriptionWeek.setOnClickListener { onSubscriptionClick(it as Subscription) }
        binding.subscriptionMonthly.setOnClickListener { onSubscriptionClick(it as Subscription) }
        binding.subscriptionAnnual.setOnClickListener { onSubscriptionClick(it as Subscription) }
    }

    private fun initListeners() {
        binding.btnContinue.setOnClickListener {
            navigate(R.id.action_subscription_fragment_to_main_fragment)
        }
        binding.ivClose.setOnClickListener {
            navigate(R.id.action_subscription_fragment_to_main_fragment)
        }
    }

    private fun onSubscriptionClick(view: Subscription) {
        binding.subscriptionWeek.setCheckedStyle(false)
        binding.subscriptionMonthly.setCheckedStyle(false)
        binding.subscriptionAnnual.setCheckedStyle(false)
        view.setCheckedStyle(true)
    }

}
