package singhsarae.badshah.stripem2.customModels.stripe.payments

interface PaymentCallBacks {
    fun onSuccess(status:PaymentSuccess)
    fun onFailure(status:PaymentProcessFailed)

    fun onPaymentStatusChange(status:String)
}

data class PaymentProcessFailed(
    val error:String?,
    val amountInCents: Float?,
    val cardLast4Digits:String?,
    val cardBrandName:String?,
    val chargeID:String?,
    val paymentIntentID:String?,
)

data class PaymentSuccess(
    val amountInCents:Float?,
    val cardLast4Digits:String?,
    val cardBrandName:String?,
    val chargeID:String?,
    val paymentIntentID:String?,
)