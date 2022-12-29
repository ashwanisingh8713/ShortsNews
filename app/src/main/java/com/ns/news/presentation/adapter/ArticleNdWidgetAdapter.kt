

package com.ns.news.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ns.news.data.api.model.CellsItem
import com.ns.news.databinding.ArticleItemBinding

/**
 * Adapter for the [RecyclerView] in [ArticleNdWidgetFragment].
 */

class ArticleNdWidgetAdapter : PagingDataAdapter<CellsItem, ArticleNdWidgetAdapter.ArticleViewHolder>(REPO_COMPARATOR) {

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
        fun bind(item: CellsItem, position: Int) {
            binding.apply {
                titleTv.text = item.cellType+" :: $position"
            }
        }
    }

    companion object {
        private val REPO_COMPARATOR = object : DiffUtil.ItemCallback<CellsItem>() {
            override fun areItemsTheSame(oldItem: CellsItem, newItem: CellsItem): Boolean =
                oldItem.title == newItem.title

            override fun areContentsTheSame(oldItem: CellsItem, newItem: CellsItem): Boolean =
                oldItem == newItem
        }
    }
}