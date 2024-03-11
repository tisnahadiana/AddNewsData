package id.deeromptech.addnewsdata.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class News(
    val id: String? = null,
    val title: String? = null,
    val content: String? = null,
    var image: String? = null,
) : Parcelable
