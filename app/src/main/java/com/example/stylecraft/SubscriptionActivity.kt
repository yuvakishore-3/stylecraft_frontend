package com.example.stylecraft

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.billingclient.api.*
import com.google.android.material.button.MaterialButton
import com.example.stylecraft.MainActivity

class SubscriptionActivity : AppCompatActivity(), PurchasesUpdatedListener {

    private lateinit var btnSubscribe: MaterialButton
    private lateinit var btnSkipForNow: MaterialButton
    private lateinit var billingClient: BillingClient
    private var productDetails: ProductDetails? = null

    companion object {
        private const val TAG = "SubscriptionActivity"
        private const val REAL_SUBSCRIPTION_SKU = "myapp_premium_subscription"
        private const val TEST_SUBSCRIPTION_SKU = "android.test.purchased"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subscription)

        logDebugInfo()
        initializeViews()
        setupBillingClient()
        setupClickListeners()
    }

    /** ============================
     * Debug Info
     * ============================ */
    private fun logDebugInfo() {
        Log.d(TAG, "=== DEBUG INFO ===")
        Log.d(TAG, "Package: $packageName")
        try {
            val info = packageManager.getPackageInfo(packageName, 0)
            Log.d(TAG, "Version Code: ${info.longVersionCode}")
            Log.d(TAG, "Version Name: ${info.versionName}")
        } catch (e: Exception) {
            Log.w(TAG, "Package info error: ${e.message}")
        }
    }

    /** ============================
     * View Init
     * ============================ */
    private fun initializeViews() {
        btnSubscribe = findViewById(R.id.btnSubscribe)
        btnSkipForNow = findViewById(R.id.btnSkipForNow)
    }

    /** ============================
     * Billing Setup
     * ============================ */
    private fun setupBillingClient() {
        billingClient = BillingClient.newBuilder(this)
            .setListener(this)
            .enablePendingPurchases()
            .build()

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(result: BillingResult) {
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.d(TAG, "Billing setup completed")
                    querySubscriptionDetails()
                } else {
                    Log.e(TAG, "Billing setup failed: ${result.debugMessage}")
                }
            }

            override fun onBillingServiceDisconnected() {
                Log.w(TAG, "Billing service disconnected")
            }
        })
    }

    private fun querySubscriptionDetails() {
        queryProduct(REAL_SUBSCRIPTION_SKU, BillingClient.ProductType.SUBS) { success ->
            if (!success) {
                Log.w(TAG, "Real product not found, trying test product")
                queryProduct(TEST_SUBSCRIPTION_SKU, BillingClient.ProductType.INAPP) { test ->
                    if (!test) showNoProductsAvailable()
                }
            }
        }
    }

    private fun queryProduct(productId: String, type: String, callback: (Boolean) -> Unit) {
        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(productId)
                .setProductType(type)
                .build()
        )
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList).build()

        billingClient.queryProductDetailsAsync(params) { result, list ->
            if (result.responseCode == BillingClient.BillingResponseCode.OK && list.isNotEmpty()) {
                productDetails = list[0]
                logOfferDetails(productDetails)
                callback(true)
            } else {
                Log.e(TAG, "Failed to get product details: ${result.debugMessage}")
                callback(false)
            }
        }
    }

    private fun logOfferDetails(details: ProductDetails?) {
        details?.subscriptionOfferDetails?.forEachIndexed { index, offer ->
            Log.d(TAG, "Offer $index: ${offer.basePlanId}, ${offer.offerToken}")
        } ?: Log.d(TAG, "No offers found")
    }

    private fun showNoProductsAvailable() {
        runOnUiThread {
            Toast.makeText(this, "No products found in Play Console", Toast.LENGTH_LONG).show()
        }
    }

    /** ============================
     * Button Listeners
     * ============================ */
    private fun setupClickListeners() {
        btnSkipForNow.setOnClickListener {
            // Replaced page2 with MainActivity
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        btnSubscribe.setOnClickListener {
            launchSubscriptionFlow()
        }
    }

    /** ============================
     * Launch Subscription Flow
     * ============================ */
    private fun launchSubscriptionFlow() {
        val details = productDetails ?: run {
            Toast.makeText(this, "Product not loaded yet", Toast.LENGTH_SHORT).show()
            return
        }

        val paramsBuilder = BillingFlowParams.ProductDetailsParams.newBuilder().setProductDetails(details)

        // Subscription flow
        if (details.productType == BillingClient.ProductType.SUBS) {
            val offerToken = details.subscriptionOfferDetails?.firstOrNull()?.offerToken
            if (offerToken == null) {
                Toast.makeText(this, "No offer available", Toast.LENGTH_SHORT).show()
                return
            }
            paramsBuilder.setOfferToken(offerToken)
        }

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(listOf(paramsBuilder.build()))
            .build()

        billingClient.launchBillingFlow(this, billingFlowParams)
    }

    /** ============================
     * Purchase Result
     * ============================ */
    override fun onPurchasesUpdated(result: BillingResult, purchases: MutableList<Purchase>?) {
        if (result.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) handlePurchase(purchase)
        } else if (result.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            Toast.makeText(this, "Purchase canceled", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Purchase failed: ${result.debugMessage}", Toast.LENGTH_SHORT).show()
            Log.e(TAG, "Purchase failed: ${result.debugMessage}")
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        Log.d(TAG, "Purchase successful: ${purchase.products}")
        Toast.makeText(this, "Subscription Activated!", Toast.LENGTH_LONG).show()

        if (!purchase.isAcknowledged) {
            val params = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken).build()
            billingClient.acknowledgePurchase(params) { result ->
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.d(TAG, "Purchase acknowledged")
                }
            }
        }

        // ✅ Redirect to login after success
        // Replaced page2 with MainActivity
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
