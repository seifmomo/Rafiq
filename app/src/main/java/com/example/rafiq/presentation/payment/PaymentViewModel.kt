package com.example.rafiq.presentation.payment

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.ViewModel
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.common.api.Status
import com.google.android.gms.wallet.PaymentData
import com.google.android.gms.wallet.PaymentDataRequest
import com.google.android.gms.wallet.PaymentsClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

sealed interface PaymentState {
    data object Idle : PaymentState
    data object Processing : PaymentState
    data class NeedsResolution(val status: Status) : PaymentState
    data class Success(val token: String?) : PaymentState
    data object Cancelled : PaymentState
    data class Error(val message: String) : PaymentState
}

/**
 * Google Pay sandbox flow. Uses WalletConstants.ENVIRONMENT_TEST so nothing is
 * really charged. Tokenization is PAYMENT_GATEWAY + Stripe test params — swap in
 * a real test merchant + publishable key and you will get a usable token.
 * Must run on a real device with Google Play services (no emulator support).
 */
@HiltViewModel
class PaymentViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow<PaymentState>(PaymentState.Idle)
    val state: StateFlow<PaymentState> = _state.asStateFlow()

    val amountLabel: String = "USD 12.50"

    fun pay(paymentsClient: PaymentsClient) {
        if (_state.value is PaymentState.Processing) return
        _state.value = PaymentState.Processing
        try {
            val request = PaymentDataRequest.fromJson(PAYMENT_DATA_JSON)
            paymentsClient.loadPaymentData(request)
                .addOnSuccessListener { paymentData ->
                    _state.value = PaymentState.Success(paymentData.paymentMethodToken?.token)
                }
                .addOnFailureListener { error ->
                    when (error) {
                        is ResolvableApiException -> {
                            val status = error.status
                            if (status.hasResolution()) {
                                _state.value = PaymentState.NeedsResolution(status)
                            } else {
                                _state.value = PaymentState.Error("Google Pay couldn't start on this device")
                            }
                        }
                        is ApiException -> _state.value = PaymentState.Error(
                            error.status.statusMessage ?: "Payment failed (status ${error.statusCode})"
                        )
                        else -> _state.value = PaymentState.Error(error.message ?: "Payment failed")
                    }
                }
        } catch (e: Exception) {
            _state.value = PaymentState.Error("Payment request could not be built: ${e.localizedMessage}")
        }
    }

    fun onResult(resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            val paymentData = data?.let { PaymentData.getFromIntent(it) }
            val token = paymentData?.paymentMethodToken?.token
            _state.value = if (token != null) PaymentState.Success(token) else PaymentState.Cancelled
        } else {
            _state.value = PaymentState.Cancelled
        }
    }

    fun reset() {
        _state.value = PaymentState.Idle
    }

    fun onUnavailable() {
        _state.value = PaymentState.Error("Google Pay needs an activity context — unavailable here")
    }

    companion object {
        private const val GATEWAY = "stripe"
        private const val GATEWAY_MERCHANT_ID = "exampleMerchantId"
        private const val STRIPE_PUBLISHABLE_KEY = "pk_test_REPLACE_ME"
        private const val CURRENCY_CODE = "USD"
        private const val TOTAL_PRICE = "12.50"

        internal val PAYMENT_DATA_JSON = """
            {
              "apiVersion": 2,
              "apiVersionMinor": 0,
              "allowedPaymentMethods": [
                {
                  "type": "CARD",
                  "parameters": {
                    "allowedAuthMethods": ["PAN_ONLY", "CRYPTOGRAM_3DS"],
                    "allowedCardNetworks": ["AMEX", "DISCOVER", "INTERAC", "JCB", "MASTERCARD", "VISA"]
                  },
                  "tokenizationSpecification": {
                    "type": "PAYMENT_GATEWAY",
                    "parameters": {
                      "gateway": "$GATEWAY",
                      "gatewayMerchantId": "$GATEWAY_MERCHANT_ID",
                      "stripe:version": "2023-10-16",
                      "stripe:publishableKey": "$STRIPE_PUBLISHABLE_KEY"
                    }
                  }
                }
              ],
              "merchantInfo": {
                "merchantName": "RAFIQ"
              },
              "transactionInfo": {
                "totalPriceStatus": "FINAL",
                "totalPrice": "$TOTAL_PRICE",
                "currencyCode": "$CURRENCY_CODE"
              }
            }
        """.trimIndent()
    }
}