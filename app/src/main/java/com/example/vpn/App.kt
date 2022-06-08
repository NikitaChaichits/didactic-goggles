package com.example.vpn

import android.app.Application
import com.example.vpn.billing.BillingDataSource
import com.example.vpn.billing.BillingRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.GlobalScope

@HiltAndroidApp
class App : Application() {
    lateinit var appContainer: AppContainer

    // Container of objects shared across the whole app
    inner class AppContainer {
        private val applicationScope = GlobalScope
        private val billingDataSource = BillingDataSource.getInstance(
            this@App,
            applicationScope,
            BillingRepository.SUBSCRIPTION_SKUS,
        )
        val billingRepository = BillingRepository(
            billingDataSource,
            applicationScope
        )
    }

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer()
    }
}
