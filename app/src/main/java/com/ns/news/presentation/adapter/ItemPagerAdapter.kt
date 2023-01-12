package com.ns.news.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.imageLoader
import coil.load
import com.ns.news.R
import com.ns.news.data.api.model.AWDataItem
import com.ns.news.data.db.Cell
import com.ns.news.databinding.ItemSampleCardLayoutBinding
import com.ns.news.databinding.NotDefinedBinding
import com.ns.news.databinding.WidgetVtGlamorCorousalItemBinding
import com.ns.news.databinding.WidgetVtPagerGalleryCorousalItemBinding
import com.ns.news.domain.model.ViewType
import com.ns.news.presentation.activity.ArticleNdWidgetClickListener

class ItemPagerAdapter(val listener: ArticleNdWidgetClickListener, private val cell: Cell, private val viewType: ViewType) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val contents = cell.data

    override fun getItemViewType(position: Int): Int {
        return when (viewType) {
            ViewType.WIDGET_VT_GLAMOR_COROUSAL_WIDGET -> R.layout.widget_vt_glamor_corousal_item
//            ViewType.WIDGET_VT_GLAMOR_COROUSAL_WIDGET -> R.layout.item_sample_card_layout
            ViewType.WIDGET_VT_PAGER_GALLERY -> R.layout.widget_vt_pager_gallery_corousal_item
            ViewType.WIDGET_VT_STACK_CARD_WITH_COROUSAL -> R.layout.widget_vt_glamor_corousal_item
            else -> {
                R.layout.not_defined
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.item_sample_card_layout -> {
                ItemSampleCardVH(
                    ItemSampleCardLayoutBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            R.layout.widget_vt_glamor_corousal_item -> {
                WidgetGlamorCorousalItemVH(
                    WidgetVtGlamorCorousalItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent, false
                    )
                )
            }
            R.layout.widget_vt_pager_gallery_corousal_item -> {
                WidgetPagerGalleryItemVH(
                    WidgetVtPagerGalleryCorousalItemBinding.inflate(
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
            /*ViewType.WIDGET_VT_GLAMOR_COROUSAL_WIDGET -> (holder as ItemSampleCardVH).bind(
                content, position
            )*/
            ViewType.WIDGET_VT_GLAMOR_COROUSAL_WIDGET -> (holder as WidgetGlamorCorousalItemVH).bind(
                content, position
            )
            ViewType.WIDGET_VT_PAGER_GALLERY -> (holder as WidgetPagerGalleryItemVH).bind(
                content, position
            )
            ViewType.WIDGET_VT_STACK_CARD_WITH_COROUSAL -> (holder as WidgetGlamorCorousalItemVH).bind(
                content, position
            )
            else -> {
                (holder as ArticleNdWidgetAdapter.NotDefinedVH).bind()
            }
        }
    }

    override fun getItemCount(): Int {
        return contents.size
    }


    class ItemSampleCardVH(
        private val binding: ItemSampleCardLayoutBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: AWDataItem, index: Int) {
            binding.apply {
            }
        }
    }

    inner class WidgetGlamorCorousalItemVH(
        private val binding: WidgetVtGlamorCorousalItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: AWDataItem, index: Int) {
            binding.apply {
                bannerImg.load(
                    item.media[0].images.mediumLarge,
                    binding.bannerImg.context.imageLoader
                )
                titleTv.text = "${item.title}"
                root.setOnClickListener {
                    listener.onArticleClick(cell.cellType, cell.type, cell.sectionId, item.articleId)
                }
            }
        }
    }

    inner class WidgetPagerGalleryItemVH(
        private val binding: WidgetVtPagerGalleryCorousalItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: AWDataItem, index: Int) {
            binding.apply {
                bannerImg.load(item.media[0].images.large, binding.bannerImg.context.imageLoader)
                root.setOnClickListener {
                    listener.onArticleClick(cell.cellType, cell.type, cell.sectionId, item.articleId)
                }
            }
        }
    }

}