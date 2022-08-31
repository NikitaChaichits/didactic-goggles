package com.cyberself.vpn.ui.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.android.billingclient.api.Purchase
import com.cyberself.vpn.R
import com.cyberself.vpn.common.base.BaseFragment
import com.cyberself.vpn.data.source.local.SharedPreferencesDataSource
import com.cyberself.vpn.databinding.FragmentSettingsBinding
import com.cyberself.vpn.domain.billing.BillingClientWrapper
import com.cyberself.vpn.util.log
import com.cyberself.vpn.util.view.invisible
import com.cyberself.vpn.util.view.openWebView
import com.cyberself.vpn.util.view.visible
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


class SettingsFragment : BaseFragment(R.layout.fragment_settings),
    BillingClientWrapper.OnQueryActivePurchasesListener {

    @Inject
    lateinit var prefs: SharedPreferencesDataSource

    @Inject
    lateinit var billingClientWrapper: BillingClientWrapper

    private val binding by viewBinding(FragmentSettingsBinding::bind)
    override val viewModel: SettingsFragmentViewModel by viewModels()

    private var isWebViewVisible = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefs = SharedPreferencesDataSource(view.context)
        if (prefs.getIsPremium()) {
            binding.ivBanner.visibility = View.GONE
        }
        initListeners()
        onBackPressed()

        billingClientWrapper = BillingClientWrapper(view.context)
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
            btnBack.setOnClickListener { navigateBack() }
            ivBanner.setOnClickListener {
                navigate(
                    SettingsFragmentDirections.actionSettingsFragmentToSubscriptionFragment(
                        true
                    )
                )
            }
            btnCheckConnection.setOnClickListener {
                navigate(R.id.action_settings_fragment_to_speed_test_fragment)
            }
            btnRestorePurchase.setOnClickListener {
                checkSubscription()
            }
            btnContactSupport.setOnClickListener {
                navigate(R.id.action_settings_fragment_to_support_fragment)
            }
            btnRateApp.setOnClickListener {
                goToPlayMarket()
            }
            btnPrivacyPolicy.setOnClickListener {
                webView.openWebView("https://cyberself-vpn.com/policy.html")
                isWebViewVisible = true
            }
            btnTerms.setOnClickListener {
                webView.openWebView("https://cyberself-vpn.com/terms.html")
                isWebViewVisible = true
            }
            btnShare.setOnClickListener {
                shareApp()
            }
        }
    }

    private fun shareApp() {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name")
            val shareMessage = "Let me recommend you this application\n " +
                    "https://play.google.com/store/apps/details?id=" + activity?.packageName

            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
            startActivity(Intent.createChooser(shareIntent, "Choose where share"))
        } catch (e: Exception) {
            log("Can not share app")
        }
    }

    private fun goToPlayMarket() {
        val uri: Uri = Uri.parse("market://details?id=" + activity?.packageName)
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        goToMarket.addFlags(
            Intent.FLAG_ACTIVITY_NO_HISTORY or
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK
        )
        try {
            startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + activity?.packageName)
                )
            )
        }
    }

    private fun checkSubscription() {
        if (prefs.getIsPremium()) {
            toast(getString(R.string.subscription_exists))
        } else{
            billingClientWrapper.queryActivePurchases(this)
        }
    }

    override fun onSuccess(activePurchases: List<Purchase>) {
        // handle successful restore
        if (activePurchases.isNotEmpty()){
            prefs.setIsPremium(true)
            toast(getString(R.string.restore_successfully))
            lifecycleScope.launchWhenResumed {
                delay(1000L)
                navigateBack()
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
        toast(getString(R.string.error_restore))
        Log.e("SubscriptionFragment", "Error: ${error.debugMessage}")
    }
}
