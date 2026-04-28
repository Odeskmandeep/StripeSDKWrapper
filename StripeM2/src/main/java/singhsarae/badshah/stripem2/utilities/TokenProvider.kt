package singhsarae.badshah.stripem2.utilities

import android.util.Log
import com.stripe.stripeterminal.external.callable.ConnectionTokenCallback
import com.stripe.stripeterminal.external.callable.ConnectionTokenProvider
import com.stripe.stripeterminal.external.models.ConnectionTokenException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import okhttp3.Credentials


class TokenProvider() : ConnectionTokenProvider {

    override fun fetchConnectionToken(callback: ConnectionTokenCallback) {
        val job = SupervisorJob()
        val scope = CoroutineScope(Dispatchers.IO + job)
        // Start the API call to get the connection token
        val tokenUrlPref = ExtensionFuns.getTokenUrlPrefData()
        if (tokenUrlPref != null){
            val urlToHit = if (!tokenUrlPref.apiKey.isNullOrEmpty()){
                tokenUrlPref.apiKey
                "https://api.stripe.com/v1/terminal/connection_tokens"
            }else{
                tokenUrlPref.customURL
            }
            val authHeader = if (!tokenUrlPref.apiKey.isNullOrEmpty()){
                Credentials.basic(tokenUrlPref.apiKey, "")
            }else{
                tokenUrlPref.optionalHeaderAuthorization.toString()
            }

            val reqBodyHash = HashMap<String, Any>()
            if (tokenUrlPref.bodyMetaDataHashMap != null){
                reqBodyHash["meta_data"] = tokenUrlPref.bodyMetaDataHashMap
            }
            scope.launch {
                val response = ApiClient.service.getStripeToken(
                    url = urlToHit.toString(),
                    acceptHeader = tokenUrlPref.optionalHeaderAccept.toString(),
                    authToken = authHeader,
                    hashMap = reqBodyHash
                )
                if (response.isSuccessful) {
                    Log.i("BADSHAH","response.body():${response.body()}")
                    Log.i("BADSHAH"," response.body()?.secret:${ response.body()?.secret}")
                    val secret = response.body()?.secret
//                    if (!secret.isNullOrEmpty()){
                        callback.onSuccess(secret.toString())
//                    }else{
//                        callback.onFailure(ConnectionTokenException("Failed to fetch connection token: No token available"))
//                    }
                }else{
                    Log.i("BADSHAH","Token Provider Api Fail..")
                }
            }
        }
    }
}
