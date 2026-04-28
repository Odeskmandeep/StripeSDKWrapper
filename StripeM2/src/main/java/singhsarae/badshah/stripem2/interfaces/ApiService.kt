package singhsarae.badshah.stripem2.interfaces

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Url
import singhsarae.badshah.stripem2.customModels.SecretTokenModel

interface ApiService {

    @POST
    suspend fun getStripeToken(
        @Url url: String,
        @Header("Accept") acceptHeader: String,
        @Header("Authorization") authToken: String,
        @Body hashMap: HashMap<String, Any>
    ): Response<SecretTokenModel>

}