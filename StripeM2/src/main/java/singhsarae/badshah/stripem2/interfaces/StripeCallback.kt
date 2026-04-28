package singhsarae.badshah.stripem2.interfaces


import singhsarae.badshah.stripem2.customModels.stripe.reader.Reader

interface StripeCallbacks {
    fun onSuccess(
        reader: Reader
    )
    fun onError(error: String)
    //For Reader Firmware update:
    fun onFinishInstallingUpdate(
        finished: Boolean?,
        error: String?
    )
    fun onStartInstallingUpdate(
        started: Boolean
    )
    fun onReportReaderSoftwareUpdateProgress(progress: Float)
    fun onReaderReconnectFailed(reader: Reader)
    fun onReaderReconnectSucceeded(reader: Reader)
}