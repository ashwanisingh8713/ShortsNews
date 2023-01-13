package com.ns.news.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.imageLoader
import coil.load
import com.ns.news.R
import com.ns.news.data.api.model.AWDataItem
import com.ns.news.data.db.Cell
import com.ns.news.databinding.NotDefinedBinding
import com.ns.news.databinding.WidgetVtStackCorousalItemBinding
import com.ns.news.databinding.WidgetVtTopNewsCorousalItemBinding
import com.ns.news.domain.model.ViewType
import com.ns.news.presentation.activity.ArticleNdWidgetClickListener

class ItemRecyclerAdapter(val listener: ArticleNdWidgetClickListener, private val cell: Cell, private val viewType: ViewType) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val contents = cell.data

    override fun getItemViewType(position: Int): Int {
        return when (viewType) {
            ViewType.WIDGET_VT_TOP_NEWS_COROUSAL -> R.layout.widget_vt_top_news_corousal_item
            ViewType.WIDGET_VT_PLAIN_WITH_COROUSAL -> R.layout.widget_vt_top_news_corousal_item
            ViewType.WIDGET_VT_STACK_CARD_WITH_COROUSAL -> R.layout.widget_vt_stack_corousal_item
            else -> {
                R.layout.not_defined
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.widget_vt_top_news_corousal_item -> {
                WidgetTopNewsCorousalItemVH(
                    WidgetVtTopNewsCorousalItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent, false
                    )
                )
            }
            R.layout.widget_vt_stack_corousal_item -> {
                WidgetStackCorousalItemVH(
                    WidgetVtStackCorousalItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent, false
                    )
                )
            }

            else -> ArticleNdWidgetAdapter.NotDefinedVH(
                NotDefinedBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                )
            )

        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val content = contents[position]
        when (viewType) {
            ViewType.WIDGET_VT_TOP_NEWS_COROUSAL -> (holder as WidgetTopNewsCorousalItemVH).bind(
                content, position
            )
            ViewType.WIDGET_VT_PLAIN_WITH_COROUSAL -> (holder as WidgetTopNewsCorousalItemVH).bind(
                content, position
            )
            ViewType.WIDGET_VT_STACK_CARD_WITH_COROUSAL -> (holder as WidgetStackCorousalItemVH).bind(
                content, position
            )
            else-> {
                (holder as ArticleNdWidgetAdapter.NotDefinedVH).bind()
            }
        }
    }

    override fun getItemCount(): Int {
        return contents.size
    }

    inner class WidgetTopNewsCorousalItemVH(
        private val binding: WidgetVtTopNewsCorousalItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: AWDataItem, index: Int) {
            binding.apply {
                top9Title.text = item.title
                top9Index.text = "${index+1}"
                binding.root.setOnClickListener {
                    listener.onArticleClick(cell.cellType, cell.type, cell.sectionId, item.articleId)
                }
            }
        }
    }

    inner class WidgetStackCorousalItemVH(
        private val binding: WidgetVtStackCorousalItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(content: AWDataItem, index: Int) {
            binding.apply {
                titleWidget.text = content.title
                autherName.text = content.author[0].name
                if(content.author.isNotEmpty())  autherImage.load(content.author[0].avatarUrl, autherImage.context.imageLoader)
                binding.root.setOnClickListener {
                    listener.onArticleClick(cell, content.articleId)
                }
            }
        }
    }

}


