package singhsarae.badshah.stripem2.utilities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.content.edit
import com.google.gson.Gson
import com.stripe.stripeterminal.Terminal
import com.stripe.stripeterminal.external.callable.Callback
import com.stripe.stripeterminal.external.callable.Cancelable
import com.stripe.stripeterminal.external.callable.ConnectionTokenCallback
import com.stripe.stripeterminal.external.callable.DiscoveryListener
import com.stripe.stripeterminal.external.callable.MobileReaderListener
import com.stripe.stripeterminal.external.callable.ReaderCallback
import com.stripe.stripeterminal.external.callable.TerminalListener
import com.stripe.stripeterminal.external.models.ConnectionConfiguration
import com.stripe.stripeterminal.external.models.ConnectionStatus
import com.stripe.stripeterminal.external.models.ConnectionTokenException
import com.stripe.stripeterminal.external.models.DeviceType
import com.stripe.stripeterminal.external.models.DisconnectReason
import com.stripe.stripeterminal.external.models.DiscoveryConfiguration
import com.stripe.stripeterminal.external.models.PaymentStatus
import com.stripe.stripeterminal.external.models.Reader
import com.stripe.stripeterminal.external.models.ReaderDisplayMessage
import com.stripe.stripeterminal.external.models.ReaderSoftwareUpdate
import com.stripe.stripeterminal.external.models.TerminalException
import com.stripe.stripeterminal.log.LogLevel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import singhsarae.badshah.stripem2.creepySteam.StripeManager
import singhsarae.badshah.stripem2.customModels.stripe.offlineListener.StripeOfflineListener
import singhsarae.badshah.stripem2.interfaces.StripeCallbacks

object ExtensionFuns {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    /**
     * Stripe Token Pref Data:
     */
    data class TokenUrlPref(
        val apiKey:String?,
        val locationId:String,
        val customURL:String?,
        val optionalHeaderAuthorization:String?,
        val optionalHeaderAccept:String?,
        val bodyMetaDataHashMap:HashMap<String, Any>?,
    )
    fun updateTokenUrlPrefData(mTokenUrlPref: TokenUrlPref?) {
        val gsonToString = if (mTokenUrlPref != null) {
            Gson().toJson(mTokenUrlPref)
        } else {
            ""
        }
        val prefs = PreferenceHelper.defaultPrefs(StripeManager.context())
        prefs.edit {
            putString(PreferenceHelper.Key.TOKEN_URL_PREFS, gsonToString)
        }
    }

    fun getTokenUrlPrefData(): TokenUrlPref? {
        val prefs = PreferenceHelper.defaultPrefs(StripeManager.context())
        val gsonString =
            prefs.getString(PreferenceHelper.Key.TOKEN_URL_PREFS, "") ?: ""
        return if (gsonString.isNotEmpty()) {
            Gson().fromJson(gsonString, TokenUrlPref::class.java)
        } else {
            null
        }
    }

    fun checkUSBReaderConnectedViaUSB(mActivity: Activity): Boolean {
        val m = mActivity.applicationContext.getSystemService(Context.USB_SERVICE) as UsbManager
        val usbDevices = m.deviceList
        Log.e("BADSHAH", "USB Devices:::$usbDevices")
        val ite: Collection<UsbDevice> = usbDevices.values
        val usbs = ite.toTypedArray()
        var readerFound = false
        for (usb in usbs) {
            Log.d("Connected usb devices", "Connected usb devices are " + usb.productName)
            if (usb.productName?.contains(
                    "STRM2",
                    true
                ) == true || usb.productName?.contains("TRINKET", true) == true
            ) {
                readerFound = true
            }
        }

        return readerFound
    }

    /**
     * initialize StripeReaderCallbacks:
     */
    private var lockReaderDiscovery = false
    private var readerCallback: ReaderCallback? = null
    private var reConnectionReaderCallback: MobileReaderListener? = null
    private var fetchedReader: Reader? = null
    private var connectCount = 0
    fun initializeStripeReaderCallbacks(
        context: Context,
        mActivity: Activity,
        usbDiscovery: Boolean,
        enableSimulation: Boolean,
        callback: StripeCallbacks
    ) {

        readerCallback = object : ReaderCallback {
            override fun onFailure(e: TerminalException) {
                mActivity.runOnUiThread {
                    if (connectCount < 3) {
                        try {
                            startConnectingToReader(
                                context,
                                mActivity,
                                fetchedReader
                            )
                        } catch (_: Exception) {
                        }
                    }
                    if (connectCount == 3) {
                        callback.onError(e.message.toString())
                    }
                }
            }

            override fun onSuccess(reader: Reader) {
                connectCount = 0
                val customReaderData = singhsarae.badshah.stripem2.customModels.stripe.reader.Reader(
                    readerId = reader.id.toString(),
                    readerName = reader.deviceType.toString()
                )
                callback.onSuccess(customReaderData)
            }
        }
        reConnectionReaderCallback = object : MobileReaderListener {
            override fun onFinishInstallingUpdate(
                update: ReaderSoftwareUpdate?,
                e: TerminalException?
            ) {
                callback.onFinishInstallingUpdate(
                    true,
                    e?.message
                )
            }

            override fun onStartInstallingUpdate(
                update: ReaderSoftwareUpdate,
                cancelable: Cancelable?
            ) {
                callback.onStartInstallingUpdate(
                    true
                )
            }

            override fun onReportReaderSoftwareUpdateProgress(progress: Float)
            {
                super.onReportReaderSoftwareUpdateProgress(progress)
                callback.onReportReaderSoftwareUpdateProgress(progress)
            }

            override fun onReportAvailableUpdate(update: ReaderSoftwareUpdate) {
                super.onReportAvailableUpdate(update)
            }

            override fun onRequestReaderDisplayMessage(message: ReaderDisplayMessage) {
                super.onRequestReaderDisplayMessage(message)
            }

            override fun onReaderReconnectFailed(reader: Reader) {
                val customReaderData = singhsarae.badshah.stripem2.customModels.stripe.reader.Reader(
                    readerId = reader.id.toString(),
                    readerName = reader.deviceType.toString()
                )
                callback.onReaderReconnectFailed(customReaderData)
            }

            override fun onReaderReconnectStarted(
                reader: Reader, cancelReconnect: Cancelable, reason: DisconnectReason
            ) {}

            override fun onReaderReconnectSucceeded(reader: Reader) {
                val customReaderData = singhsarae.badshah.stripem2.customModels.stripe.reader.Reader(
                    readerId = reader.id.toString(),
                    readerName = reader.deviceType.toString()
                )
                callback.onReaderReconnectSucceeded(customReaderData)
            }
        }

        val listener = object : TerminalListener {
            override fun onConnectionStatusChange(status: ConnectionStatus) {}
            override fun onPaymentStatusChange(status: PaymentStatus) {}
        }
        val logLevel = LogLevel.VERBOSE

        try {
            if (!Terminal.isInitialized()) {
                Terminal.init(
                    context,
                    logLevel,
                    TokenProvider(),
                    listener,
                    StripeOfflineListener()
                )
                // Since the Terminal is a singleton, you can call getInstance whenever you need it
                lockReaderDiscovery = false
                startDiscovery(context, mActivity, usbDiscovery,enableSimulation,callback)
            } else {
                    fetchedReader = Terminal.getInstance().connectedReader
                    if (fetchedReader == null) {
                        TokenProvider().fetchConnectionToken(object : ConnectionTokenCallback {
                            override fun onFailure(e: ConnectionTokenException) {}
                            override fun onSuccess(token: String) {
                                lockReaderDiscovery = false
                                startDiscovery(context, mActivity, usbDiscovery,enableSimulation,callback)
                            }
                        })
                        return
                    } else {
                    }
            }
        } catch (e: Exception) { }

    }

    @SuppressLint("MissingPermission")
    fun startDiscovery(
        context: Context,
        mActivity: Activity,
        usbDiscovery: Boolean,
        enableSimulation: Boolean,
        callback: StripeCallbacks,
    ) {
        Log.e("BADSHAH", "startDiscovery Called..001")
        scope.launch {
            val discoveryConfig = if (usbDiscovery) {
                DiscoveryConfiguration.UsbDiscoveryConfiguration(
                    0,
                    enableSimulation
                )
            } else {
                DiscoveryConfiguration.BluetoothDiscoveryConfiguration(
                    0,
                    enableSimulation
                )
            }
            Terminal
                .getInstance()
                .discoverReaders(
                    config = discoveryConfig,
                    discoveryListener = object : DiscoveryListener {
                        override fun onUpdateDiscoveredReaders(readers: List<Reader>) {
                            // In your app, display the discovered reader(s) to the user.
                            // Call `connectUsbReader` after the user selects a reader to connect to.
                            Log.e(
                                "BADSHAH",
                                "startDiscovery() discoveryListener onUpdateDiscoveredReaders Called..002::: lockReaderDiscovery:$lockReaderDiscovery"
                            )
                            if (!lockReaderDiscovery) {
                                lockReaderDiscovery = true
                                startReaderDiscoveryWork(
                                    readers,
                                    context, mActivity,
                                    enableSimulation,callback
                                )
                            }
                        }
                    },
                    callback = object : Callback {
                        override fun onSuccess() {}

                        override fun onFailure(e: TerminalException) {
                            callback.onError("Reader Discovery Failed: ${e.message}")
                        }
                    })
        }
    }

    private fun startReaderDiscoveryWork(
        readers: List<Reader>, context: Context,
        mActivity: Activity,
        enableSimulation: Boolean,
        callback: StripeCallbacks,
    ) {
        if (readers.isNotEmpty()) {
            if (enableSimulation) {
                for (i in readers.indices) {
                    if (readers[i].deviceType == DeviceType.STRIPE_M2) {
                        Log.e(
                            "BADSHAH",
                            "reader's type:::${readers[i].deviceType}"
                        )
                        fetchedReader = readers[i]
                        break
                    }
                }
            } else {
                fetchedReader = readers[0]
            }
            try {
                connectCount = 0
                Log.e("BADSHAH", "Stripe Fetched Reader Detail:$fetchedReader")
                Handler(Looper.getMainLooper()).postDelayed({
                    startConnectingToReader(
                        context,
                        mActivity,
                        fetchedReader
                    )
                }, 3000)
            } catch (e: Exception) {
                callback.onError("Error while connecting to reader: $e")
            }
        } else {
            callback.onError("Reader not found!")
        }
    }

    private fun startConnectingToReader(
        context: Context,
        mActivity: Activity,
        fetchedReader: Reader?
    ) {
        Log.e("BADSHAH", "startConnectingToReader() fetchedReader:$fetchedReader")
        if (fetchedReader != null) {
            val tokenUrlPref = ExtensionFuns.getTokenUrlPrefData()
            Log.e("BADSHAH", "connectCount:$connectCount")
            if (connectCount < 3) {
                connectCount += 1
                Terminal.getInstance().connectReader(
                    reader = fetchedReader,
                    config = ConnectionConfiguration.UsbConnectionConfiguration(
                        tokenUrlPref?.locationId.toString(),
                        true,
                        reConnectionReaderCallback!!
                    ),
                    connectionCallback = readerCallback!!,
                )
            }
        }
    }

}