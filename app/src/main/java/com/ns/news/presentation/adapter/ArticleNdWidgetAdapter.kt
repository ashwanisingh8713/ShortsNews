package com.ns.news.presentation.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.load
import com.ns.news.R
import com.ns.news.data.api.model.AWDataItem
import com.ns.news.data.db.Cell
import com.ns.news.databinding.ArticlePhotosBinding
import com.ns.news.databinding.ArticleStandardBinding
import com.ns.news.databinding.ArticleVideoBinding
import com.ns.news.databinding.NotDefinedBinding
import com.ns.news.databinding.WidgetVtAllTopicsWidgetBinding
import com.ns.news.databinding.WidgetVtForYouWidgetBinding
import com.ns.news.databinding.WidgetVtHeroPlainWidgetBinding
import com.ns.news.databinding.WidgetVtPlainWithCorousalBinding
import com.ns.news.databinding.WidgetVtStackCardWithCorousalBinding
import com.ns.news.databinding.WidgetVtTopNewsCorousalBinding
import com.ns.news.databinding.WidgetWebviewBinding
import com.ns.news.domain.model.ViewType
import com.ns.news.utils.showToast

/**
 * Adapter for the [RecyclerView] in [ArticleNdWidgetFragment].
 * https://guides.codepath.com/android/Paging-Library-Guide#add-a-data-source
 * https://stackoverflow.com/questions/63019699/using-recyclerview-with-diffutil-for-multiple-data-types
 * https://blog.devgenius.io/diffutil-in-multiple-columns-list-with-different-items-ae52a8134f0a
 * https://serjantarbuz.medium.com/recyclerview-with-different-items-and-multiple-columns-grid-724618d5d4e4
 * https://github.com/SerjantArbuz/AdapterExample
 *
 * *******Demystifying DiffUtil.ItemCallback Class -> https://jermainedilao.medium.com/demystifying-diffutil-itemcallback-class-8c0201cc69b1
 */

class ArticleNdWidgetAdapter(private val imageLoader: ImageLoader) : PagingDataAdapter<Cell, RecyclerView.ViewHolder>(REPO_COMPARATOR) {

    companion object {
        private val CATEGORY_AND_TIME_DELIMETER = "##"
        private val REPO_COMPARATOR = object : DiffUtil.ItemCallback<Cell>() {
            override fun areContentsTheSame(oldItem: Cell, newItem: Cell): Boolean  {
                return if((newItem.type == newItem.type) && oldItem.data.isNotEmpty() && newItem.data.isNotEmpty()) {
//                    Log.i("Ashwani", "areContentsTheSame IF :: true")
                    oldItem.data[0].id == newItem.data[0].id
                } else if((newItem.type == newItem.type) && oldItem.data.isNotEmpty() && newItem.data.isNotEmpty()) {
//                    Log.i("Ashwani", "areContentsTheSame ELSE IF :: true")
                    true
                } else {
                    val isSame = (oldItem.type == newItem.type) && oldItem.data.isEmpty() && newItem.data.isEmpty()
                    Log.i("Ashwani", "areContentsTheSame ELSE :: $isSame")
                    isSame
                }
            }

            override fun areItemsTheSame(oldItem: Cell, newItem: Cell): Boolean {
                val isSame = oldItem == newItem
                Log.i("Ashwani", "areItemsTheSame :: $isSame")
                return isSame
            }

        }

    }


    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)?.viewType) {
//            ViewType.ARTICLE_VT_NOT_VALID -> R.layout.listing_missing_vh
//            ViewType.WIDGET_VT_NOT_VALID -> R.layout.listing_missing_vh
            ViewType.VT_NOT_DEFINED -> R.layout.not_defined // DONE
            ViewType.ARTICLE_VT_STANDARD -> R.layout.article_standard // DONE
            ViewType.ARTICLE_VT_PHOTOS -> R.layout.article_photos // DONE
            ViewType.ARTICLE_VT_VIDEO -> R.layout.article_video // DONE
            ViewType.WIDGET_VT_WEB_WIDGET -> R.layout.widget_webview // NOT DONE
            ViewType.WIDGET_VT_HERO_PLAIN_WIDGET -> R.layout.widget_vt_hero_plain_widget // DONE
            ViewType.WIDGET_VT_TOP_NEWS_COROUSAL -> R.layout.widget_vt_top_news_corousal // DONE
            ViewType.WIDGET_VT_PLAIN_WITH_COROUSAL -> R.layout.widget_vt_plain_with_corousal // DONE
            ViewType.WIDGET_VT_STACK_CARD_WITH_COROUSAL -> R.layout.widget_vt_stack_card_with_corousal // DONE
            ViewType.WIDGET_VT_ALL_TOPICS_WIDGET -> R.layout.widget_vt_all_topics_widget // DONE
            ViewType.WIDGET_VT_FOR_YOU_WIDGET -> R.layout.widget_vt_for_you_widget // NOT DONE
            else -> {
                R.layout.not_defined
            }
        }
    }

    fun inflater(parent: ViewGroup): LayoutInflater{
        return LayoutInflater.from(parent.context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.not_defined -> NotDefinedVH(NotDefinedBinding.inflate(inflater(parent), parent, false))
            R.layout.article_standard -> ArticleStandardVH(ArticleStandardBinding.inflate(inflater(parent), parent, false))
            R.layout.article_photos -> ArticlePhotosVH(ArticlePhotosBinding.inflate(inflater(parent), parent, false))
            R.layout.article_video -> ArticleVideoVH(ArticleVideoBinding.inflate(inflater(parent), parent, false))
            R.layout.widget_webview -> WidgetWebViewVH(WidgetWebviewBinding.inflate(inflater(parent), parent, false))
            R.layout.widget_vt_hero_plain_widget -> WidgetHeroPlainWidgetVH(WidgetVtHeroPlainWidgetBinding.inflate(inflater(parent), parent, false))
            R.layout.widget_vt_top_news_corousal -> WidgetTopNewsCorousalVH(WidgetVtTopNewsCorousalBinding.inflate(inflater(parent), parent, false))
            R.layout.widget_vt_plain_with_corousal -> WidgetPlainWithCorousalVH(WidgetVtPlainWithCorousalBinding.inflate(inflater(parent), parent, false))
            R.layout.widget_vt_stack_card_with_corousal -> WidgetStackCardWithCorousalVH(WidgetVtStackCardWithCorousalBinding.inflate(inflater(parent), parent, false))
            R.layout.widget_vt_all_topics_widget -> WidgetAllTopicsVH(WidgetVtAllTopicsWidgetBinding.inflate(inflater(parent), parent, false))
            R.layout.widget_vt_for_you_widget -> WidgetForYouVH(WidgetVtForYouWidgetBinding.inflate(inflater(parent), parent, false))
            else ->  NotDefinedVH(NotDefinedBinding.inflate(inflater(parent), parent, false))
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val cell = getItem(position)
        when (cell?.viewType) {
            ViewType.VT_NOT_DEFINED -> (holder as NotDefinedVH).bind()
            ViewType.ARTICLE_VT_STANDARD -> (holder as ArticleStandardVH).bind(cell, position)
            ViewType.ARTICLE_VT_PHOTOS -> (holder as ArticlePhotosVH).bind(cell, position)
            ViewType.ARTICLE_VT_VIDEO -> (holder as ArticleVideoVH).bind(cell, position)
            ViewType.WIDGET_VT_WEB_WIDGET -> (holder as WidgetWebViewVH).bind(cell)
            ViewType.WIDGET_VT_HERO_PLAIN_WIDGET -> (holder as WidgetHeroPlainWidgetVH).bind(cell, position)
            ViewType.WIDGET_VT_TOP_NEWS_COROUSAL -> (holder as WidgetTopNewsCorousalVH).bind(cell, position, cell.viewType)
            ViewType.WIDGET_VT_PLAIN_WITH_COROUSAL -> (holder as WidgetPlainWithCorousalVH).bind(cell, position, cell.viewType)
            ViewType.WIDGET_VT_STACK_CARD_WITH_COROUSAL -> (holder as WidgetStackCardWithCorousalVH).bind(cell, position, cell.viewType)
            ViewType.WIDGET_VT_ALL_TOPICS_WIDGET -> (holder as WidgetAllTopicsVH).bind(cell.data, position)
            ViewType.WIDGET_VT_FOR_YOU_WIDGET -> (holder as WidgetForYouVH).bind(cell.data, position)
            else -> (holder as NotDefinedVH).bind()
        }
    }

    class NotDefinedVH(
        private val binding: NotDefinedBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {

        }
    }

    inner class ArticleStandardVH(
        private val binding: ArticleStandardBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Cell, position: Int) {
            val item = item.data[0]
            binding.apply {
                titleTv.text = "${item.title}"
                categoryTv.text = item.categoryName
                timeTv.text = item.modifiedGmt
                bannerImg.load(item.media[0].images.mediumLarge, imageLoader)
            }
        }
    }

    class ArticlePhotosVH(
        private val binding: ArticlePhotosBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Cell, position: Int) {
            binding.apply {
                titleTv.text = " :: $position :: ${item.data[0].title}"
            }
        }
    }

    class ArticleVideoVH(
        private val binding: ArticleVideoBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Cell, position: Int) {
            binding.apply {
                titleTv.text = " :: $position :: ${item.data[0].title}"
            }
        }
    }

    class WidgetWebViewVH(
        private val binding: WidgetWebviewBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cell: Cell) {
            binding.apply {
                webView.loadUrl(cell.link)
            }
        }
    }

    inner class WidgetHeroPlainWidgetVH(
        private val binding: WidgetVtHeroPlainWidgetBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cell: Cell, position: Int) {
            val item = cell.data[0]
            binding.apply {
                if(cell.title.isNotEmpty()) {
                    cellTitleTv.text = cell.title
                    cellActionBtn.setOnClickListener {
                        it.context.showToast(cell.link)
                    }
                    cellTitleTv.visibility = View.VISIBLE
                    cellActionBtn.visibility = View.VISIBLE
                }
                bannerImg.load(item.media[0].images.large, imageLoader)
                titleTv.text = " :: $position :: ${item.title}"
                categoryTv.text = item.categoryName
                timeTv.text = item.modifiedGmt
                recyclerView.adapter = HighlightsAdapter(item.highlights)
            }
        }
    }


    class WidgetTopNewsCorousalVH(
        private val binding: WidgetVtTopNewsCorousalBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cell: Cell, position: Int, viewType: ViewType) {
            val adapter = ItemRecyclerAdapter(cell, viewType)
            binding.pager.adapter = adapter
            binding.cellTitle.text = cell.title
        }
    }

    class WidgetPlainWithCorousalVH(
        private val binding: WidgetVtPlainWithCorousalBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Cell, position: Int, viewType: ViewType) {
            val adapter = ItemRecyclerAdapter(item, viewType)
            binding.pager.adapter = adapter
        }
    }
    class WidgetStackCardWithCorousalVH(
        private val binding: WidgetVtStackCardWithCorousalBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Cell, position: Int, viewType: ViewType) {
            val adapter = ItemRecyclerAdapter(item, viewType)
            binding.pager.adapter = adapter
        }
    }

    class WidgetAllTopicsVH(
        private val binding: WidgetVtAllTopicsWidgetBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(contents: List<AWDataItem>, position: Int) {

        }
    }

    class WidgetForYouVH(
        private val binding: WidgetVtForYouWidgetBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(contents: List<AWDataItem>, position: Int) {

        }
    }


}