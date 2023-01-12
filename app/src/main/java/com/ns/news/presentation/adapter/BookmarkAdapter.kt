package com.ns.news.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.imageLoader
import coil.load
import com.ns.news.R
import com.ns.news.data.api.model.AWDataItem
import com.ns.news.data.db.Bookmark
import com.ns.news.data.db.Cell
import com.ns.news.databinding.*
import com.ns.news.domain.model.ViewType
import com.ns.news.presentation.activity.ArticleNdWidgetClickListener

class BookmarkAdapter(private val bookmarks: List<Bookmark>, private val callback: Callback) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ArticleBookmarkVH(ArticleStandardBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ArticleBookmarkVH).bind(bookmarks[position], position, callback)

    }

    override fun getItemCount(): Int {
        return bookmarks.size
    }

    inner class ArticleBookmarkVH(
        private val binding: ArticleStandardBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(bookmark: Bookmark, position: Int, callback: Callback) {
            val item = bookmark.data
            binding.apply {
                titleTv.text = item.title
                categoryTv.text = item.categoryName
                timeTv.text = item.modifiedGmt
                if(item.media.isNotEmpty())  bannerImg.load(item.media[0].images.mediumLarge, bannerImg.context.imageLoader)
                root.setOnClickListener {
                    //listener.onArticleClick(cell, item.articleId)
                }
            }
        }
    }


    interface Callback {
        fun onBookmarkClick(item: Bookmark)
    }

}