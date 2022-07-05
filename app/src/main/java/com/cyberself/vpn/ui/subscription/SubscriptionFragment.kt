package com.cyberself.vpn.ui.subscription

import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import com.cyberself.vpn.R
import com.cyberself.vpn.common.base.BaseFragment
import com.cyberself.vpn.data.source.local.SharedPreferencesDataSource
import com.cyberself.vpn.databinding.FragmentSubscriptionBinding
import com.cyberself.vpn.domain.billing.BillingClientWrapper
import com.cyberself.vpn.util.view.Subscription
import com.cyberself.vpn.util.view.invisible
import com.cyberself.vpn.util.view.visible
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SubscriptionFragment : BaseFragment(R.layout.fragment_subscription),
    BillingClientWrapper.OnPurchaseListener {

    lateinit var prefs: SharedPreferencesDataSource

    @Inject
    lateinit var billingClientWrapper: BillingClientWrapper

    private val args: SubscriptionFragmentArgs by navArgs()

    private val binding by viewBinding(FragmentSubscriptionBinding::bind)
    override val viewModel: SubscriptionViewModel by viewModels()

    private var isWebViewVisible = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefs = SharedPreferencesDataSource(view.context)

        if (args.isThreeTypesSubscription)
            binding.subscriptionWeek.visible()

        initListeners()
        onBackPressed()

        billingClientWrapper.onPurchaseListener = this
        displayProducts()
    }

    private val purchaseButtonsMap: Map<String, Subscription> by lazy(LazyThreadSafetyMode.NONE) {
        mapOf(
            "premium_sub_annual" to binding.subscriptionAnnual,
            "premium_sub_month" to binding.subscriptionMonthly,
            "premium_sub_week" to binding.subscriptionWeek
        )
    }

    private fun displayProducts() {
        billingClientWrapper.queryProducts(object : BillingClientWrapper.OnQueryProductsListener {
            override fun onSuccess(products: List<SkuDetails>) {
                products.forEach { product ->
                    purchaseButtonsMap[product.sku]?.apply {
//                        text = "${product.description} for ${product.price}"
                        setOnClickListener {
                            billingClientWrapper.purchase(requireActivity(), product)
                            Log.e("SubscriptionFragment", "${product.description} for ${product.price}")
                        }
                    }
                }
            }

            override fun onFailure(error: BillingClientWrapper.Error) {
                //handle error
                Log.e("SubscriptionFragment", "Error: ${error.debugMessage}")
            }
        })
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

    override fun onPurchaseSuccess(purchase: Purchase?) {
        //handle successful purchase
        //show premium information
        prefs.setIsPremium(true)
        Log.i("SubscriptionFragment", "onPurchaseSuccess!")
    }

    override fun onPurchaseFailure(error: BillingClientWrapper.Error) {
        //handle error or user cancelation
        Log.e("SubscriptionFragment", "Error: ${error.debugMessage}")
    }

    override fun onResume() {
        super.onResume()

    //TODO check purchases
    }

}
