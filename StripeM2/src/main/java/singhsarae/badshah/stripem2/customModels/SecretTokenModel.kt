package singhsarae.badshah.stripem2.customModels

import com.google.gson.annotations.SerializedName

data class SecretTokenModel(
    @SerializedName("secret")
    val secret:String?
)
