package com.cyberself.vpn.ui.splash

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.android.billingclient.api.Purchase
import com.cyberself.vpn.R
import com.cyberself.vpn.common.base.BaseFragment
import com.cyberself.vpn.data.source.local.SharedPreferencesDataSource
import com.cyberself.vpn.domain.billing.BillingClientWrapper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import javax.inject.Inject

@AndroidEntryPoint
class SplashFragment : BaseFragment(R.layout.fragment_splash),
    BillingClientWrapper.OnQueryActivePurchasesListener {

    override val viewModel: SplashViewModel by viewModels()

    lateinit var prefs: SharedPreferencesDataSource

    @Inject
    lateinit var billingClientWrapper: BillingClientWrapper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefs = SharedPreferencesDataSource(view.context)
        lifecycleScope.launchWhenResumed {
            delay(3000L)
            checkFirstLaunch()
        }
        checkSubscription()
    }

    private fun checkFirstLaunch() {
        if (prefs.getFirstLaunch()) {
            navigate(R.id.action_splash_fragment_to_privacy_fragment)
            prefs.setFirstLaunch(false)
        } else {
            navigate(R.id.action_splash_fragment_to_main_fragment)
        }
    }

    private fun checkSubscription() {
        prefs.setIsPremium(false)
        billingClientWrapper.queryActivePurchases(this)
    }

    override fun onSuccess(activePurchases: List<Purchase>) {
        if (activePurchases.isNotEmpty()){
            prefs.setIsPremium(true)
        }
        Log.d("SubscriptionFragment", "active purchases = $activePurchases")
    }

    override fun onFailure(error: BillingClientWrapper.Error) {
        Log.e("SubscriptionFragment", "message = ${error.debugMessage}")
    }

}
