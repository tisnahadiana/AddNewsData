package id.deeromptech.addnewsdata.utils

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.google.gson.Gson
import id.deeromptech.addnewsdata.R
import id.deeromptech.addnewsdata.model.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.net.SocketTimeoutException

object Constant {

    const val NEWS_IMAGE = "news_image"
    const val NEWS_TITLE = "news_title"
    const val ARTICLE_ID = "article_id"
    const val NEWS_CONTENT = "news_content"

    fun isTextEmpty(text: String?): Boolean {
        return TextUtils.isEmpty(text.toString()) || text == null
    }

    fun <T, VH : RecyclerView.ViewHolder> handleData(
        page: Int?,
        takeItem: Boolean,
        data: List<T>?,
        adapter: ListAdapter<T, VH>,
        recyclerView: RecyclerView?,
        textView: MaterialTextView?
    ) {
        if (page != null) {
            val oldList = adapter.currentList
            if (page == 1) {
                adapter.submitList(data)
            } else {
                val newList = oldList.toMutableList().apply {
                    addAll(data ?: mutableListOf())
                }
                adapter.submitList(newList) {
                    recyclerView?.smoothScrollToPosition(adapter.itemCount - 2)
                }
            }
        } else {
            adapter.submitList(if (takeItem) data?.take(4) else data)
        }
        val isEmpty = data?.isEmpty() ?: false
        recyclerView?.visibility = if (isEmpty) View.GONE else View.VISIBLE
        textView?.visibility = if (isEmpty) View.VISIBLE else View.GONE
    }

    fun handleErrorApi(context: FragmentActivity, it: Throwable) {
        if (ConnectivityStatus.isConnected(context)) {
            when (it) {
                is HttpException -> {
                    try {
                        val gson = Gson()
                        val response = gson.fromJson(
                            it.response()?.errorBody()?.charStream(),
                            Response::class.java
                        )
                        val message = response?.message.toString()
                        handleSessionAndException(it, context, message)
                    } catch (e: Exception) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.message_if_error),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                is SocketTimeoutException -> // connection errors
                    Toast.makeText(
                        context, "Connection Timeout!",
                        Toast.LENGTH_SHORT
                    ).show()

                else -> {
                    Toast.makeText(
                        context, it.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            Toast.makeText(
                context, context.getString(R.string.message_if_disconnect),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun handleSessionAndException(
        t: HttpException,
        context: FragmentActivity,
        message: String?
    ) {
        if (t.code().toString() == "401") {
            Toast.makeText(
                context,
                "Maaf session anda telah berakhir",
                Toast.LENGTH_SHORT
            ).show()

            context.lifecycleScope.launch {
                withContext(Dispatchers.IO) {
//                    MyApp.getInstance().clearDataStore(context)
                }
            }

//            context.startActivity(
//                Intent(
//                    context,
//                    AuthActivity::class.java
//                )
//            )
            context.finish()
        } else {
            if (message != "null") {
                Toast.makeText(
                    context,
                    message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun getFileName(context: Context?, file: Uri?): String? {
        var fileName: String? = null
        file.let { returnUri ->
            returnUri?.let { context?.contentResolver?.query(it, null, null, null, null) }
        }?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            fileName = cursor.getString(nameIndex)
        }

        return fileName
    }
}