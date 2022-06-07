package com.cyberself.vpn.domain.billing

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.*

class BillingClientWrapper(context: Context) : PurchasesUpdatedListener {

    interface OnQueryProductsListener {
        fun onSuccess(products: List<ProductDetails>)
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

    fun purchase(activity: Activity, productDetails: ProductDetails, selectedOfferIndex: Int = 0) {

        val offerToken = productDetails.subscriptionOfferDetails?.get(selectedOfferIndex)?.offerToken

        val productDetailsParamsList =
            listOf(
                offerToken?.let {
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(productDetails)
                        .setOfferToken(it)
                        .build()
                }
            )

        val billingFlowParams =
            BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build()

        onConnected {
            activity.runOnUiThread {
                billingClient.launchBillingFlow(activity, billingFlowParams)
            }
        }
    }

    fun queryProducts(listener: OnQueryProductsListener) {
        val skusList = listOf("premium_sub_annual", "premium_sub_month", "premium_sub_week")

        queryProductsForType(
            skusList,
            BillingClient.ProductType.SUBS
        ) { billingResult, productDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val products = productDetailsList ?: mutableListOf()
                listener.onSuccess(products)
            } else {
                listener.onFailure(
                    Error(billingResult.responseCode, billingResult.debugMessage)
                )
            }
        }
    }

    private fun queryProductsForType(
        subsList: List<String>,
        @BillingClient.ProductType type: String,
        listener: ProductDetailsResponseListener
    ) {
        val productList = mutableListOf<QueryProductDetailsParams.Product>()

        for (i in subsList) {
            productList.add(
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(i)
                    .setProductType(type)
                    .build())
        }

        val params = QueryProductDetailsParams.newBuilder().setProductList(productList)

        onConnected {
            billingClient.queryProductDetailsAsync(
                params.build(),
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
                .setProductType(BillingClient.ProductType.SUBS)
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
