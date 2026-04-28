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
import singhsarae.badshah.stripem2.interfaces.StripeCallbacks
import kotlin.String

object StripeManager {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val connector = StreamLineConnectorImpl()
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
        callback: StripeCallbacks,
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
            callback,
        )

    }

    fun readerConnectionStatus():String{
        val status = if (Terminal.isInitialized()) {
            Terminal.getInstance().connectionStatus.toString()
        }else{
            ConnectionStatus.NOT_CONNECTED.toString()
        }
        return status
    }


}