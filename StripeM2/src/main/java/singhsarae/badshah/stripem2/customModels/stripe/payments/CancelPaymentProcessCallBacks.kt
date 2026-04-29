package singhsarae.badshah.stripem2.customModels.stripe.payments

interface CancelPaymentProcessCallBacks {
    fun onSuccess()
    fun onFailure(error:String)
}