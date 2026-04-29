package singhsarae.badshah.stripem2.creepySteam

import android.app.Activity
import singhsarae.badshah.stripem2.customModels.stripe.payments.CancelPaymentProcessCallBacks
import singhsarae.badshah.stripem2.customModels.stripe.payments.PaymentCallBacks
import singhsarae.badshah.stripem2.interfaces.StripeCallbacks

internal interface StreamLineConnector {
    fun connect(
        mActivity: Activity,
        apiKey: String?,
        locationId: String,
        customURL: String?,
        optionalHeaderAuthorization: String?,
        optionalHeaderAccept: String?,
        bodyMetaDataHashMap: HashMap<String, Any>?,
        enableSimulation: Boolean,
        callback: StripeCallbacks,
    )

    fun collectPayment(
        clientSecret:String?,
        amountInCents: Float,
        metaData: HashMap<String, String>?,
        currency:String?,
        callBacks: PaymentCallBacks
    )

    fun cancelPaymentProcess(
        callback: CancelPaymentProcessCallBacks
    )

    fun getReaderConnectionStatus():String

}