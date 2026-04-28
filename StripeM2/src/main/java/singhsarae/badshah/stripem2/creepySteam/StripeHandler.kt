package singhsarae.badshah.stripem2.creepySteam

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.stripe.stripeterminal.Terminal
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

abstract class StripeHandler {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun connectStripeIfPossible(
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


}