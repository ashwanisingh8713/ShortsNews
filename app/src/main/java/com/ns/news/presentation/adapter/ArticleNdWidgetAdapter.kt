package com.ns.news.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ns.news.R
import com.ns.news.databinding.ArticleItemBinding
import com.ns.news.data.db.Cell
import com.ns.news.domain.model.ViewType

/**
 * Adapter for the [RecyclerView] in [ArticleNdWidgetFragment].
 * https://guides.codepath.com/android/Paging-Library-Guide#add-a-data-source
 */

class ArticleNdWidgetAdapter : PagingDataAdapter<Cell, RecyclerView.ViewHolder>(REPO_COMPARATOR) {

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)?.viewType) {
            ViewType.ARTICLE_VT_NOT_VALID -> R.layout.article_item
            ViewType.ARTICLE_VT_STANDARD -> R.layout.article_item
            ViewType.WIDGET_VT_NOT_VALID -> R.layout.article_item
            ViewType.WIDGET_VT_TOP_NEWS_COROUSAL -> R.layout.article_item
            ViewType.WIDGET_VT_HERO_PLAIN_WIDGET -> R.layout.article_item
            ViewType.WIDGET_VT_WEB_WIDGET -> R.layout.article_item
            ViewType.WIDGET_VT_PLAIN_WITH_COROUSAL -> R.layout.article_item
            ViewType.WIDGET_VT_STACK_CARD_WITH_COROUSAL -> R.layout.article_item
            else -> {
                R.layout.article_item
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.article_item -> {
                ArticleViewHolder(ArticleItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent, false
                ))
            }

            else -> throw Exception()
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)!!
        when (item.viewType) {
            ViewType.ARTICLE_VT_NOT_VALID -> (holder as ArticleViewHolder).bind(item, position)
            else -> {
                (holder as ArticleViewHolder).bind(item, position)
            }
        }
    }

    class ArticleViewHolder(
        private val binding: ArticleItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Cell, position: Int) {
            binding.apply {
                titleTv.text = item.cellType+" :: $position"
            }
        }
    }

    companion object {
        private val REPO_COMPARATOR = object : DiffUtil.ItemCallback<Cell>() {
            override fun areContentsTheSame(oldItem: Cell, newItem: Cell): Boolean =
                oldItem.data == newItem.data

            override fun areItemsTheSame(oldItem: Cell, newItem: Cell): Boolean =
                oldItem.type == newItem.type

        }
    }
}