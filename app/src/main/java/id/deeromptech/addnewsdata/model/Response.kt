package id.deeromptech.addnewsdata.model

import com.google.gson.annotations.SerializedName

data class Response(
    @SerializedName("status") var status: Boolean? = null,
    @SerializedName("message") var message: String? = null
)