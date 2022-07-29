package com.cyberself.vpn.domain.billing

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.*

class BillingClientWrapper(context: Context) : PurchasesUpdatedListener {

    interface OnQueryProductsListener {
        fun onSuccess(products: List<SkuDetails>)
        fun onFailure(error: Error)
    }

    interface OnPurchaseListener {
        fun onPurchaseSuccess(purchase: Purchase?)
        fun onPurchaseFailure(error: Error)
    }

    interface OnQueryActivePurchasesListener {
        fun onSuccess(activePurchases: List<Purchase>)
        fun onFailure(error: Error)
    }

    var onPurchaseListener: OnPurchaseListener? = null

    class Error(val responseCode: Int, val debugMessage: String)


    val billingClient = BillingClient
        .newBuilder(context)
        .enablePendingPurchases()
        .setListener(this)
        .build()

    fun purchase(activity: Activity, product: SkuDetails) {
        onConnected {
            activity.runOnUiThread {
                billingClient.launchBillingFlow(
                    activity,
                    BillingFlowParams.newBuilder().setSkuDetails(product).build()
                )
            }
        }
    }

    fun queryProducts(listener: OnQueryProductsListener) {
        val skusList = listOf("premium_sub_annual", "premium_sub_month", "premium_sub_week")

        queryProductsForType(
            skusList,
            BillingClient.SkuType.SUBS
        ) { billingResult, skuDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val products = skuDetailsList ?: mutableListOf()
                listener.onSuccess(products)
            } else {
                listener.onFailure(
                    Error(billingResult.responseCode, billingResult.debugMessage)
                )
            }
        }
    }

    private fun queryProductsForType(
        skusList: List<String>,
        @BillingClient.SkuType type: String,
        listener: SkuDetailsResponseListener
    ) {
        onConnected {
            billingClient.querySkuDetailsAsync(
                SkuDetailsParams.newBuilder().setSkusList(skusList).setType(type).build(),
                listener
            )
        }
    }

    private fun onConnected(block: () -> Unit) {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                block()
            }

            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                Log.e("BillingClientWrapper", "Billing service disconnected!")
            }
        })
    }

    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchaseList: MutableList<Purchase>?
    ) {
        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                if (purchaseList == null) {
                    onPurchaseListener?.onPurchaseSuccess(null)
                    return
                }

                purchaseList.forEach(::processPurchase)
            }
            else -> {
                //error occurred or user canceled
                onPurchaseListener?.onPurchaseFailure(
                    Error(
                        billingResult.responseCode,
                        billingResult.debugMessage
                    )
                )
            }
        }
    }

    private fun processPurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            onPurchaseListener?.onPurchaseSuccess(purchase)

                if (!purchase.isAcknowledged) {
                acknowledgePurchase(purchase) { billingResult ->
                    if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
                        //implement retry logic or try to acknowledge again in onResume()
                    }
                }
            }
        }
    }

    private fun acknowledgePurchase(
        purchase: Purchase,
        callback: AcknowledgePurchaseResponseListener
    ) {
        onConnected {
            billingClient.acknowledgePurchase(
                AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchase.purchaseToken)
                    .build(),
                callback::onAcknowledgePurchaseResponse
            )
        }
    }

    private fun queryActivePurchasesForType(
        params: QueryPurchasesParams,
        listener: PurchasesResponseListener
    ) {
        onConnected {
            billingClient.queryPurchasesAsync(params, listener)
        }
    }

    fun queryActivePurchases(listener: OnQueryActivePurchasesListener) {
        queryActivePurchasesForType(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.SkuType.SUBS)
                .build()
        ) { billingResult, activeSubsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                listener.onSuccess(activeSubsList)
            } else {
                listener.onFailure(
                    Error(billingResult.responseCode, billingResult.debugMessage)
                )
            }
        }
    }

}
