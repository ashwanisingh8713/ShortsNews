package com.ns.news.presentation.adapter

import android.util.Log
import android.view.LayoutInflater
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
import com.ns.news.databinding.WidgetVtTopNewsCorousalItemBinding
import com.ns.news.databinding.WidgetWebviewBinding
import com.ns.news.domain.model.ViewType

/**
 * Adapter for the [RecyclerView] in [ArticleNdWidgetFragment].
 * https://guides.codepath.com/android/Paging-Library-Guide#add-a-data-source
 */

class ArticleNdWidgetAdapter(private val imageLoader: ImageLoader) : PagingDataAdapter<Cell, RecyclerView.ViewHolder>(REPO_COMPARATOR) {

    companion object {
        private val REPO_COMPARATOR = object : DiffUtil.ItemCallback<Cell>() {
            override fun areContentsTheSame(oldItem: Cell, newItem: Cell): Boolean =
                true//oldItem.data == newItem.data

            override fun areItemsTheSame(oldItem: Cell, newItem: Cell): Boolean =
                true//oldItem.type == newItem.type

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
                Log.i("Ashwani", "Else not defined")
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
            R.layout.widget_vt_hero_plain_widget -> WidgetHeroPlainVH(imageLoader, WidgetVtHeroPlainWidgetBinding.inflate(inflater(parent), parent, false))
            R.layout.widget_vt_top_news_corousal -> WidgetTopNewsCorousalVH(WidgetVtTopNewsCorousalBinding.inflate(inflater(parent), parent, false))
            R.layout.widget_vt_plain_with_corousal -> WidgetPlainWithCorousalVH(WidgetVtPlainWithCorousalBinding.inflate(inflater(parent), parent, false))
            R.layout.widget_vt_stack_card_with_corousal -> WidgetStackCardWithCorousalVH(WidgetVtStackCardWithCorousalBinding.inflate(inflater(parent), parent, false))
            R.layout.widget_vt_all_topics_widget -> WidgetAllTopicsVH(WidgetVtAllTopicsWidgetBinding.inflate(inflater(parent), parent, false))
            R.layout.widget_vt_for_you_widget -> WidgetForYouVH(WidgetVtForYouWidgetBinding.inflate(inflater(parent), parent, false))
            else ->  NotDefinedVH(NotDefinedBinding.inflate(inflater(parent), parent, false))
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (item?.viewType) {
            ViewType.VT_NOT_DEFINED -> (holder as NotDefinedVH).bind()
            ViewType.ARTICLE_VT_STANDARD -> (holder as ArticleStandardVH).bind(item, position)
            ViewType.ARTICLE_VT_PHOTOS -> (holder as ArticlePhotosVH).bind(item, position)
            ViewType.ARTICLE_VT_VIDEO -> (holder as ArticleVideoVH).bind(item, position)
            ViewType.WIDGET_VT_WEB_WIDGET -> (holder as WidgetWebViewVH).bind()
            ViewType.WIDGET_VT_HERO_PLAIN_WIDGET -> (holder as WidgetHeroPlainVH).bind(item.data, position)
            ViewType.WIDGET_VT_TOP_NEWS_COROUSAL -> (holder as WidgetTopNewsCorousalVH).bind(item, position, item.viewType)
            ViewType.WIDGET_VT_PLAIN_WITH_COROUSAL -> (holder as WidgetPlainWithCorousalVH).bind(item, position, item.viewType)
            ViewType.WIDGET_VT_STACK_CARD_WITH_COROUSAL -> (holder as WidgetStackCardWithCorousalVH).bind(item, position, item.viewType)
            ViewType.WIDGET_VT_ALL_TOPICS_WIDGET -> (holder as WidgetAllTopicsVH).bind(item.data, position)
            ViewType.WIDGET_VT_FOR_YOU_WIDGET -> (holder as WidgetForYouVH).bind(item.data, position)
            else -> (holder as NotDefinedVH).bind()
        }
    }

    class NotDefinedVH(
        private val binding: NotDefinedBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {

        }
    }

    class ArticleStandardVH(
        private val binding: ArticleStandardBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Cell, position: Int) {
            binding.apply {
                cellType.text = item.cellType + " :: $position"
            }
        }
    }

    class ArticlePhotosVH(
        private val binding: ArticlePhotosBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Cell, position: Int) {
            binding.apply {
                cellType.text = item.cellType + " :: $position"
            }
        }
    }

    class ArticleVideoVH(
        private val binding: ArticleVideoBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Cell, position: Int) {
            binding.apply {
                cellType.text = item.cellType + " :: $position"
            }
        }
    }

    class WidgetWebViewVH(
        private val binding: WidgetWebviewBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.apply {

            }
        }
    }

    class WidgetHeroPlainVH(
        private val imageLoader: ImageLoader, private val binding: WidgetVtHeroPlainWidgetBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(contents: List<AWDataItem>, position: Int) {
            binding.heroImg.load(contents[0].media[0].images.medium, imageLoader)
        }
    }



    class WidgetTopNewsCorousalVH(
        private val binding: WidgetVtTopNewsCorousalBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cell: Cell, position: Int, viewType: ViewType) {
            val adapter = HorizontalRecyclerAdapter(cell, viewType)
            binding.pager.adapter = adapter
            binding.cellTitle.text = cell.title
        }
    }

    class WidgetPlainWithCorousalVH(
        private val binding: WidgetVtPlainWithCorousalBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Cell, position: Int, viewType: ViewType) {
            val adapter = HorizontalRecyclerAdapter(item, viewType)
            binding.pager.adapter = adapter
        }
    }
    class WidgetStackCardWithCorousalVH(
        private val binding: WidgetVtStackCardWithCorousalBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Cell, position: Int, viewType: ViewType) {
            val adapter = HorizontalRecyclerAdapter(item, viewType)
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

    class WidgetTopNewsCorousalItemVH(
        private val binding: WidgetVtTopNewsCorousalItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(content: AWDataItem, index: Int) {
            binding.apply {
                top9Title.text = content.title
                top9Index.text = "${index+1}"
            }
        }
    }

    class HorizontalRecyclerAdapter(
        private val cell: Cell,
        private val viewType: ViewType
    ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private val contents = cell.data

        override fun getItemViewType(position: Int): Int {
            return when (viewType) {
                ViewType.WIDGET_VT_TOP_NEWS_COROUSAL -> R.layout.widget_vt_top_news_corousal_item
                ViewType.WIDGET_VT_PLAIN_WITH_COROUSAL -> R.layout.widget_vt_top_news_corousal_item
                ViewType.WIDGET_VT_STACK_CARD_WITH_COROUSAL -> R.layout.widget_vt_top_news_corousal_item
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

                else -> NotDefinedVH(NotDefinedBinding.inflate(LayoutInflater.from(parent.context), parent, false))

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
                ViewType.WIDGET_VT_STACK_CARD_WITH_COROUSAL -> (holder as WidgetTopNewsCorousalItemVH).bind(
                    content, position
                )
                else-> {
                    (holder as NotDefinedVH).bind()
                }
            }
        }

        override fun getItemCount(): Int {
            return contents.size
        }


    }


}