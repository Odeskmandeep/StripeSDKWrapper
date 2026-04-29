package singhsarae.badshah.stripem2.creepySteam

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.stripe.stripeterminal.Terminal
import com.stripe.stripeterminal.external.callable.Callback
import com.stripe.stripeterminal.external.callable.Cancelable
import com.stripe.stripeterminal.external.callable.PaymentIntentCallback
import com.stripe.stripeterminal.external.callable.TerminalListener
import com.stripe.stripeterminal.external.models.CaptureMethod
import com.stripe.stripeterminal.external.models.ConnectionStatus
import com.stripe.stripeterminal.external.models.PaymentIntent
import com.stripe.stripeterminal.external.models.PaymentIntentParameters
import com.stripe.stripeterminal.external.models.PaymentStatus
import com.stripe.stripeterminal.external.models.TerminalErrorCode
import com.stripe.stripeterminal.external.models.TerminalException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import singhsarae.badshah.stripem2.customModels.stripe.payments.CancelPaymentProcessCallBacks
import singhsarae.badshah.stripem2.customModels.stripe.payments.PaymentCallBacks
import singhsarae.badshah.stripem2.customModels.stripe.payments.PaymentProcessFailed
import singhsarae.badshah.stripem2.customModels.stripe.payments.PaymentSuccess
import singhsarae.badshah.stripem2.interfaces.StripeCallbacks
import singhsarae.badshah.stripem2.utilities.ExtensionFuns
import singhsarae.badshah.stripem2.utilities.ExtensionFuns.TokenUrlPref

internal class StreamLineConnectorImpl: StreamLineConnector {


    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun connect(
        mActivity: Activity,
        apiKey: String?,
        locationId: String,
        customURL: String?,
        optionalHeaderAuthorization: String?,
        optionalHeaderAccept: String?,
        bodyMetaDataHashMap: HashMap<String, Any>?,
        enableSimulation: Boolean,
        callback: StripeCallbacks,
    ) {

        if (ContextCompat.checkSelfPermission(
                mActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Log.i("BADSHAH", "connectStripeIfPossible() Permission GRANTED")
            ExtensionFuns.updateTokenUrlPrefData(
                TokenUrlPref(
                    apiKey = apiKey,
                    locationId = locationId,
                    customURL = customURL,
                    optionalHeaderAuthorization = optionalHeaderAuthorization,
                    optionalHeaderAccept = optionalHeaderAccept,
                    bodyMetaDataHashMap = bodyMetaDataHashMap,
                )
            )
            getStripeReaderDetails(mActivity,callback,enableSimulation)
        } else {
            callback.onError("Location permission not found")
        }
    }

    private fun getStripeReaderDetails(mActivity: Activity,callback: StripeCallbacks,enableSimulation: Boolean,) {
        if (ExtensionFuns.checkUSBReaderConnectedViaUSB(mActivity)) {
            if (Terminal.isInitialized()) {
                try {
                    if (Terminal.getInstance().connectionStatus == ConnectionStatus.CONNECTED) {
                        Terminal.getInstance().disconnectReader(object : Callback {
                            override fun onFailure(e: TerminalException) {}
                            override fun onSuccess() {}
                        })
                    }
                } catch (_: Exception) {}
            }
            scope.launch {
                delay(3000)
                ExtensionFuns.initializeStripeReaderCallbacks(
                    mActivity.applicationContext,
                    mActivity,
                    true,
                    enableSimulation,
                    callback
                )
            }
        }else{
            callback.onError("Reader not connected with USB.")
        }
    }

    /**
     * Collect Payment:
     */
    override fun collectPayment(
        clientSecret: String?,
        amountInCents: Float,
        metaData: HashMap<String, String>?,
        currency: String?,
        callBacks: PaymentCallBacks
    ) {
        if (readerConnectionStatus() != ConnectionStatus.CONNECTED.toString()){
            callBacks.onFailure(PaymentProcessFailed(
                error = "Reader not connected.",
                amountInCents = amountInCents,
                cardLast4Digits = null,
                cardBrandName = null,
                chargeID = null,
                paymentIntentID = null,
            ))
            return
        }

        if (clientSecret != null){
            retrievePaymentIntent(clientSecret,callBacks,amountInCents)
        }else{
            val customMetaData = metaData ?: HashMap<String, String>()
            if (currency.isNullOrEmpty()){
                callBacks.onFailure(PaymentProcessFailed(
                     error = "Currency is not valid.",
                     amountInCents = amountInCents,
                     cardLast4Digits = null,
                     cardBrandName = null,
                     chargeID = null,
                     paymentIntentID = null,
                ))
                return
            }
            val params = PaymentIntentParameters.Builder()
                .setAmount(amountInCents.toLong())
                .setCurrency(currency)
                .setCaptureMethod(CaptureMethod.Automatic)
                .setMetadata(customMetaData)
                .build()
            createPaymentIntent(params,callBacks,amountInCents)
        }
    }

    override fun cancelPaymentProcess(callback: CancelPaymentProcessCallBacks) {
        cancelable?.cancel(object : Callback {
            override fun onSuccess() {
                callback.onSuccess()
            }
            override fun onFailure(e: TerminalException) {
                callback.onFailure(e.toString())
            }
        })
    }


    private fun createPaymentIntent(params: PaymentIntentParameters,callBacks: PaymentCallBacks,amountInCents: Float){
        Terminal.getInstance()
            .createPaymentIntent(
                params,
                object : PaymentIntentCallback {
                    override fun onSuccess(paymentIntent: PaymentIntent) {
                        collectPaymentFromIntent(paymentIntent,callBacks,amountInCents)
                    }
                    override fun onFailure(e: TerminalException) {
                        callBacks.onFailure(PaymentProcessFailed(
                            error = "Unable to proceed with card. Exception: ${e.toString()}",
                            amountInCents = amountInCents,
                            cardLast4Digits = null,
                            cardBrandName = null,
                            chargeID = null,
                            paymentIntentID = null,
                        ))
                    }
                })
    }

    private fun retrievePaymentIntent(clientSecret:String,callBacks: PaymentCallBacks,amountInCents: Float){
        Terminal.getInstance().retrievePaymentIntent(
            clientSecret,
            object : PaymentIntentCallback {
                override fun onSuccess(paymentIntent: PaymentIntent) {
                    collectPaymentFromIntent(paymentIntent,callBacks,amountInCents)
                }

                override fun onFailure(e: TerminalException) {
                    callBacks.onFailure(PaymentProcessFailed(
                        error = "Unable to Retrieve Payment Intent. Exception: ${e.toString()}",
                        amountInCents = amountInCents,
                        cardLast4Digits = null,
                        cardBrandName = null,
                        chargeID = null,
                        paymentIntentID = null,
                    ))
                }
            })
    }

    private var cancelable: Cancelable? = null
    private fun collectPaymentFromIntent(paymentIntent: PaymentIntent,callBacks: PaymentCallBacks,amountInCents: Float){
        Terminal.getInstance().setTerminalListener(object : TerminalListener {
            override fun onPaymentStatusChange(status: PaymentStatus) {
                super.onPaymentStatusChange(status)
                callBacks.onPaymentStatusChange(status.toString())
            }
        })
        cancelable = Terminal.getInstance()
            .collectPaymentMethod(
                intent = paymentIntent,
                callback = object : PaymentIntentCallback {
                    override fun onSuccess(paymentIntent: PaymentIntent) {
                        confirmPaymentFromIntent(paymentIntent,callBacks,amountInCents)
                    }

                    override fun onFailure(e: TerminalException) {
                        callBacks.onFailure(PaymentProcessFailed(
                            error = "Unable to Collect Payment. Exception: ${e.toString()}",
                            amountInCents = amountInCents,
                            cardLast4Digits = null,
                            cardBrandName = null,
                            chargeID = null,
                            paymentIntentID = null,
                        ))
                    }
                }
            )
    }

    private fun confirmPaymentFromIntent(paymentIntent: PaymentIntent,callBacks: PaymentCallBacks,amountInCents: Float){
        Terminal.getInstance()
            .confirmPaymentIntent(paymentIntent, object : PaymentIntentCallback {
                override fun onSuccess(paymentIntent: PaymentIntent) {
                           val card4LastDigit =
                                paymentIntent.getCharges()[0].paymentMethodDetails?.cardPresentDetails?.last4.toString()
                           val cardBrandName =
                                paymentIntent.getCharges()[0].paymentMethodDetails?.cardPresentDetails?.brand.toString()
                            val chargeID = paymentIntent.getCharges()[0].id
                        callBacks.onSuccess(
                        PaymentSuccess(
                            amountInCents = amountInCents,
                            cardLast4Digits = card4LastDigit,
                            cardBrandName = cardBrandName,
                            chargeID = chargeID,
                            paymentIntentID = paymentIntent.id,
                        )
                    )
                }

                override fun onFailure(e: TerminalException) {
                    val card4LastDigit = e.paymentIntent?.getCharges()
                        ?.get(0)?.paymentMethodDetails?.cardPresentDetails?.last4.toString()
                    val cardBrandName = e.paymentIntent?.getCharges()
                        ?.get(0)?.paymentMethodDetails?.cardPresentDetails?.brand.toString()
                    val chargeID = e.paymentIntent?.getCharges()
                        ?.get(0)?.id.toString()

                    callBacks.onFailure(PaymentProcessFailed(
                        error = "Unable to Collect Payment. Exception: ${e.toString()}",
                        amountInCents = amountInCents,
                        cardLast4Digits = card4LastDigit,
                        cardBrandName = cardBrandName,
                        chargeID = chargeID,
                        paymentIntentID = paymentIntent.id,
                    ))
                }
            })
    }

    /**
     * Reader connection Status:
     */
    fun readerConnectionStatus():String{
        val status = if (Terminal.isInitialized()) {
            Terminal.getInstance().connectionStatus.toString()
        }else{
            ConnectionStatus.NOT_CONNECTED.toString()
        }
        return status
    }


}