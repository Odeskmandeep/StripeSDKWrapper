package singhsarae.badshah.stripem2.creepySteam

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.stripe.stripeterminal.Terminal
import com.stripe.stripeterminal.TerminalApplicationDelegate
import com.stripe.stripeterminal.external.callable.Callback
import com.stripe.stripeterminal.external.models.ConnectionStatus
import com.stripe.stripeterminal.external.models.TerminalException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import singhsarae.badshah.stripem2.interfaces.StripeCallbacks
import singhsarae.badshah.stripem2.utilities.ExtensionFuns
import singhsarae.badshah.stripem2.utilities.ExtensionFuns.TokenUrlPref
import singhsarae.badshah.stripem2.utilities.PreferenceHelper
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
//            TerminalApplicationDelegate.onCreate(mApplication)
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

        connector.connectStripeIfPossible(
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