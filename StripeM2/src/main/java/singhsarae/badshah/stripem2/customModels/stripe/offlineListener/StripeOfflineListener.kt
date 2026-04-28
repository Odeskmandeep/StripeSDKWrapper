package singhsarae.badshah.stripem2.customModels.stripe.offlineListener

import android.util.Log
import com.stripe.stripeterminal.external.callable.OfflineListener
import com.stripe.stripeterminal.external.models.OfflineStatus
import com.stripe.stripeterminal.external.models.PaymentIntent
import com.stripe.stripeterminal.external.models.TerminalException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat

class StripeOfflineListener : OfflineListener {

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    override fun onPaymentIntentForwarded(paymentIntent: PaymentIntent, e: TerminalException?) {

    }

    override fun onForwardingFailure(e: TerminalException) {
        // A non-specific error occurred while forwarding a PaymentIntent.
        // Check the error message and your integration implementation to
        // troubleshoot.
        Log.e("BADSHAH","StripeOfflineListener onForwardingFailure() Called")
    }

    override fun onOfflineStatusChange(offlineStatus: OfflineStatus) {
        Log.e("BADSHAH","StripeOfflineListener onOfflineStatusChange() offlineStatus:$offlineStatus")

    }

}