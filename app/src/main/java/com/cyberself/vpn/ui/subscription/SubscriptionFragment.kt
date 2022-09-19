package com.cyberself.vpn.ui.subscription

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.cyberself.vpn.R
import com.cyberself.vpn.common.base.BaseFragment
import com.cyberself.vpn.data.source.local.SharedPreferencesDataSource
import com.cyberself.vpn.databinding.FragmentSubscriptionBinding
import com.cyberself.vpn.domain.billing.BillingClientWrapper
import com.cyberself.vpn.util.view.Subscription
import com.cyberself.vpn.util.view.invisible
import com.cyberself.vpn.util.view.openWebView
import com.cyberself.vpn.util.view.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SubscriptionFragment : BaseFragment(R.layout.fragment_subscription),
    BillingClientWrapper.OnPurchaseListener, BillingClientWrapper.OnQueryActivePurchasesListener {

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
    }

    private val purchaseButtonsMap: Map<String, Subscription> by lazy(LazyThreadSafetyMode.NONE) {
        if (args.isThreeTypesSubscription) {
            mapOf(
                "premium_sub_annual" to binding.subscriptionAnnual,
                "premium_sub_month" to binding.subscriptionMonthly,
                "premium_sub_week" to binding.subscriptionWeek
            )
        } else {
            mapOf(
                "premium_sub_annual" to binding.subscriptionAnnual,
                "premium_sub_month" to binding.subscriptionMonthly
            )
        }
    }

    private fun displayProducts() {
        billingClientWrapper.queryProducts(object : BillingClientWrapper.OnQueryProductsListener {
            override fun onSuccess(products: List<ProductDetails>) {
                products.forEach { product ->
                    purchaseButtonsMap[product.productId]?.apply {
                        setOnClickListener {
                            billingClientWrapper.purchase(requireActivity(), product)
                            Log.d(
                                "SubscriptionFragment",
                                "${product.description} for ${product.oneTimePurchaseOfferDetails?.formattedPrice}"
                            )
                        }
                    }
                }
            }

            override fun onFailure(error: BillingClientWrapper.Error) {
                if (error.responseCode != BillingClient.BillingResponseCode.OK
                    && error.responseCode != BillingClient.BillingResponseCode.USER_CANCELED
                ) {
                    lifecycleScope.launch(Dispatchers.Main) {
                        toast("Error! ${error.debugMessage}")
                    }
                }

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

    @SuppressLint("ClickableViewAccessibility")
    private fun initListeners() {
        with(binding) {
            tvPurchased.setOnClickListener { checkSubscription() }
            btnContinue.setOnClickListener {
                navigate(R.id.action_subscription_fragment_to_main_fragment)
            }
            ivClose.setOnClickListener {
                navigate(R.id.action_subscription_fragment_to_main_fragment)
            }
            subscriptionWeek.setOnTouchListener { view, event ->
                if (event.action == MotionEvent.ACTION_DOWN ||
                    event.action == MotionEvent.ACTION_MOVE)
                    onSubscriptionClick(view as Subscription)
                return@setOnTouchListener true
            }
            subscriptionMonthly.setOnTouchListener { view, event ->
                if (event.action == MotionEvent.ACTION_DOWN ||
                    event.action == MotionEvent.ACTION_MOVE)
                    onSubscriptionClick(view as Subscription)
                return@setOnTouchListener true
            }
            subscriptionAnnual.setOnTouchListener { view, event ->
                if (event.action == MotionEvent.ACTION_DOWN ||
                    event.action == MotionEvent.ACTION_MOVE)
                    onSubscriptionClick(view as Subscription)
                return@setOnTouchListener true
            }
            tvTerms.setOnClickListener {
                webView.openWebView("https://cyberself-vpn.com/terms.html")
                isWebViewVisible = true
            }
        }
    }

    private fun onSubscriptionClick(view: Subscription) {
        binding.subscriptionWeek.setCheckedStyle(false)
        binding.subscriptionMonthly.setCheckedStyle(false)
        binding.subscriptionAnnual.setCheckedStyle(false)
        view.setCheckedStyle(true)
        displayProducts()
    }

    private fun checkSubscription() {
        if (prefs.getIsPremium()) {
            toast(getString(R.string.subscription_exists))
        } else {
            billingClientWrapper.queryActivePurchases(this)
        }
    }

    override fun onPurchaseSuccess(purchase: Purchase?) {
        // handle successful purchase
        prefs.setIsPremium(true)
        navigate(R.id.action_subscription_fragment_to_main_fragment)
        Log.i("SubscriptionFragment", "onPurchaseSuccess!")
    }

    override fun onPurchaseFailure(error: BillingClientWrapper.Error) {
        // handle error or user cancelation
        Log.e("SubscriptionFragment", "Error: ${error.debugMessage}")
    }

    override fun onSuccess(activePurchases: List<Purchase>) {
        // handle successful restore
        if (activePurchases.isNotEmpty()){
            prefs.setIsPremium(true)
            lifecycleScope.launch(Dispatchers.Main) {
                toast(getString(R.string.restore_successfully))
                delay(1000L)
                navigate(R.id.action_subscription_fragment_to_main_fragment)
            }
        } else {
            lifecycleScope.launch(Dispatchers.Main) {
                toast(getString(R.string.subscription_not_exists))
            }
        }

        Log.i("SubscriptionFragment", "onPurchaseSuccess!")
    }

    override fun onFailure(error: BillingClientWrapper.Error) {
        // handle error while restore
        lifecycleScope.launch(Dispatchers.Main) {
            toast(getString(R.string.error_restore))
        }
        Log.e("SubscriptionFragment", "Error: ${error.debugMessage}")
    }

}
