package com.example.vpn.ui.subscription

import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.vpn.R
import com.example.vpn.common.base.BaseFragment
import com.example.vpn.databinding.FragmentSubscriptionBinding
import com.example.vpn.util.view.Subscription
import com.example.vpn.util.view.invisible
import com.example.vpn.util.view.visible

class SubscriptionFragment : BaseFragment(R.layout.fragment_subscription) {

    private val args: SubscriptionFragmentArgs by navArgs()

    private val binding by viewBinding(FragmentSubscriptionBinding::bind)
    override val viewModel: SubscriptionViewModel by viewModels()

    private var isWebViewVisible = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (args.isThreeTypesSubscription)
            binding.subscriptionWeek.visible()

        initListeners()
        onBackPressed()
    }

    private fun onBackPressed() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (isWebViewVisible) {
                        binding.webView.invisible()
                        isWebViewVisible = false
                    }
                }
            })
    }

    private fun initListeners() {
        with(binding) {
            btnContinue.setOnClickListener {
                navigate(R.id.action_subscription_fragment_to_main_fragment)
            }
            ivClose.setOnClickListener {
                navigate(R.id.action_subscription_fragment_to_main_fragment)
            }
            subscriptionWeek.setOnClickListener { onSubscriptionClick(it as Subscription) }
            subscriptionMonthly.setOnClickListener { onSubscriptionClick(it as Subscription) }
            subscriptionAnnual.setOnClickListener { onSubscriptionClick(it as Subscription) }
            tvTerms.setOnClickListener { openWebView("https://cyberself-vpn.com/terms.html") }
        }
    }

    private fun onSubscriptionClick(view: Subscription) {
        binding.subscriptionWeek.setCheckedStyle(false)
        binding.subscriptionMonthly.setCheckedStyle(false)
        binding.subscriptionAnnual.setCheckedStyle(false)
        view.setCheckedStyle(true)
    }

    private fun openWebView(url: String) {
        binding.webView.run {
            webViewClient = WebViewClient()
            loadUrl(url)
            settings.javaScriptEnabled = true
            settings.setSupportZoom(true)
            visible()
            isWebViewVisible = true
        }
    }

}
