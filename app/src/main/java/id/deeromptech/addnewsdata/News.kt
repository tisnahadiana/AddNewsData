package id.deeromptech.addnewsdata

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class News(
    val title: String? = null,
    val content: String? = null,
    var image: String? = null,
) : Parcelable
