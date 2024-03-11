package id.deeromptech.addnewsdata.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import id.deeromptech.addnewsdata.model.News
import id.deeromptech.addnewsdata.R
import id.deeromptech.addnewsdata.databinding.ItemNewsBinding
import id.deeromptech.addnewsdata.utils.Constant
import id.deeromptech.addnewsdata.view.DetailNewsActivity

class ArticlesSettingListAdapter (private val activity: FragmentActivity) : ListAdapter<News, ArticlesSettingListAdapter.Holder>(MyDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding =
            ItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: Holder, position: Int) {
        val data = getItem(position)

        with(holder.binding) {
//            GlideApp.with(activity)
//                .load(data.image)
//                .apply(glideRequestOption(activity, R.drawable.image_empty))
//                .into(imageNews)

            if (data.image == null){
                imageNews.visibility = View.GONE
            }

            val content: Spanned
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                content = Html.fromHtml(data.content, HtmlCompat.FROM_HTML_MODE_LEGACY)
            } else {
                @Suppress("DEPRECATION")
                content = Html.fromHtml(data.content)
            }

            textNewsContext.text = content
            textNewsName.text = data.title ?: "-"

            cardNews.setOnClickListener {
                val bundle = Bundle()
                bundle.putString(Constant.ARTICLE_ID, data.id.toString())
                bundle.putString(Constant.NEWS_TITLE, data.title.toString())
                bundle.putString(Constant.NEWS_CONTENT, data.content.toString())
                bundle.putString(Constant.NEWS_IMAGE, data.image.toString())

                val intent = Intent(activity, DetailNewsActivity::class.java)
                intent.putExtras(bundle)
                activity.startActivity(intent)
            }

            layoutSettingArticles.visibility = View.VISIBLE

            editNew.setOnClickListener {
                onEditClick?.invoke(data)
            }

            deleteNews.setOnClickListener {
                onDeleteClick?.invoke(data)
            }
        }
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    class MyDiffCallback : DiffUtil.ItemCallback<News>() {
        override fun areItemsTheSame(oldItem: News, newItem: News): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: News, newItem: News): Boolean {
            return oldItem == newItem
        }
    }

    var onEditClick: ((News) -> Unit)? = null
    var onDeleteClick: ((News) -> Unit)? = null

    class Holder(val binding: ItemNewsBinding) : RecyclerView.ViewHolder(binding.root)
}