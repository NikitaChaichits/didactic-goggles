package com.example.vpn.di

import android.content.Context
import com.example.vpn.domain.billing.BillingClientWrapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BillingModule {

    @Provides
    @Singleton
    fun provideBillingWrapper(
        @ApplicationContext context: Context
    ) : BillingClientWrapper = BillingClientWrapper(context)

}