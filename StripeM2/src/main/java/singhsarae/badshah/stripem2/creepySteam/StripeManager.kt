package singhsarae.badshah.stripem2.creepySteam

import android.app.Activity
import android.app.Application
import android.content.Context
import android.util.Log
import com.stripe.stripeterminal.Terminal
import com.stripe.stripeterminal.TerminalApplicationDelegate
import com.stripe.stripeterminal.external.models.ConnectionStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import singhsarae.badshah.stripem2.customModels.stripe.payments.PaymentCallBacks
import singhsarae.badshah.stripem2.customModels.stripe.payments.PaymentProcessFailed
import singhsarae.badshah.stripem2.interfaces.StripeCallbacks
import kotlin.String

object StripeManager {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val connector: StreamLineConnector = StreamLineConnectorImpl()
    @Volatile
    private var appContext: Context? = null
    private var initialized = false

    fun context(): Context {
        return appContext
            ?: throw IllegalStateException("SDK not initialized. Call MySdk.init() first.")
    }

     fun init(mApplication: Application) {
        if (!initialized) {
            initialized = true
            Log.e("BADSHAH","StripeManager SDK init Called..")
            // Initialize the Stripe Terminal SDK
            TerminalApplicationDelegate.onCreate(mApplication)
            appContext = mApplication.applicationContext
        }
    }

    fun connectStripeIfPossible(
        mActivity: Activity,
        apiKey:String?,
        locationId:String,
        customURL:String?,
        optionalHeaderAuthorization:String?,
        optionalHeaderAccept:String?,
        bodyMetaDataHashMap:HashMap<String, Any>?,
        enableSimulation: Boolean,
        callbacks: StripeCallbacks,
    ) {

        connector.connect(
            mActivity,
            apiKey,
            locationId,
            customURL,
            optionalHeaderAuthorization,
            optionalHeaderAccept,
            bodyMetaDataHashMap,
            enableSimulation,
            callbacks,
        )
    }

    fun collectPayment(
        clientSecret:String?,
        amountInCents: Float,
        metaData: HashMap<String, String>?,
        currency:String?,
        callBacks: PaymentCallBacks
    ){
            connector.collectPayment(
                clientSecret,
                amountInCents,
                metaData,
                currency,
                callBacks
            )
    }

    fun getReaderConnectionStatus(): String {
        return  connector.getReaderConnectionStatus()
    }




}