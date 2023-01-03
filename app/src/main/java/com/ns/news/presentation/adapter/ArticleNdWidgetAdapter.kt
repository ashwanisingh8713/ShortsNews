package com.ns.news.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ns.news.databinding.ArticleItemBinding
import com.ns.news.data.db.Cell

/**
 * Adapter for the [RecyclerView] in [ArticleNdWidgetFragment].
 */

class ArticleNdWidgetAdapter : PagingDataAdapter<Cell, ArticleNdWidgetAdapter.ArticleViewHolder>(REPO_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        return ArticleViewHolder(
            ArticleItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val cellsItem = getItem(position)
        if (cellsItem != null) {
            holder.bind(cellsItem, position)
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