package com.ns.news.presentation.adapter

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import coil.imageLoader
import coil.load
import com.ns.news.R
import com.ns.news.data.api.model.CellBackground
import com.ns.news.data.db.Cell
import com.ns.news.databinding.ArticlePhotosBinding
import com.ns.news.databinding.ArticleStandardBinding
import com.ns.news.databinding.ArticleVideoBinding
import com.ns.news.databinding.NotDefinedBinding
import com.ns.news.databinding.WidgetVtAllTopicsWidgetBinding
import com.ns.news.databinding.WidgetVtForYouWidgetBinding
import com.ns.news.databinding.WidgetVtGalleryBinding
import com.ns.news.databinding.WidgetVtGlamourBinding
import com.ns.news.databinding.WidgetVtHeroPlainWidgetBinding
import com.ns.news.databinding.WidgetVtPlainWithCorousalBinding
import com.ns.news.databinding.WidgetVtStackCardWithCorousalBinding
import com.ns.news.databinding.WidgetVtTopNewsCorousalBinding
import com.ns.news.databinding.WidgetWebviewBinding
import com.ns.news.domain.model.ViewType
import com.ns.news.presentation.activity.ArticleNdWidgetClickListener
import com.ns.news.utils.SnapHelperOneByOne
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


class ArticleNdWidgetAdapter(private val listener: ArticleNdWidgetClickListener) : PagingDataAdapter<Cell, RecyclerView.ViewHolder>(REPO_COMPARATOR) {

    val isDayMode = true

    companion object {
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
            ViewType.WIDGET_VT_FOR_YOU_WIDGET -> R.layout.widget_vt_for_you_widget // DONE
            ViewType.WIDGET_VT_GLAMOR_COROUSAL_WIDGET  -> R.layout.widget_vt_glamour // DONE
            ViewType.WIDGET_VT_PAGER_GALLERY  -> R.layout.widget_vt_gallery // DONE
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
            R.layout.widget_vt_glamour -> WidgetGlamourVH(WidgetVtGlamourBinding.inflate(inflater(parent), parent, false))
            R.layout.widget_vt_gallery -> WidgetGalleryVH(WidgetVtGalleryBinding.inflate(inflater(parent), parent, false))
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
            ViewType.WIDGET_VT_ALL_TOPICS_WIDGET -> (holder as WidgetAllTopicsVH).bind(cell, position)
            ViewType.WIDGET_VT_FOR_YOU_WIDGET -> (holder as WidgetForYouVH).bind(cell, position)
            ViewType.WIDGET_VT_GLAMOR_COROUSAL_WIDGET -> (holder as WidgetGlamourVH).bind(cell, ViewType.WIDGET_VT_GLAMOR_COROUSAL_WIDGET)
            ViewType.WIDGET_VT_PAGER_GALLERY -> (holder as WidgetGalleryVH).bind(cell, ViewType.WIDGET_VT_PAGER_GALLERY)
            else -> (holder as NotDefinedVH).bind()
        }
    }

    private fun setCellBackground(root: View, cellBackground: CellBackground) {
        when(isDayMode) {
            true->root.setBackgroundColor(Color.parseColor(cellBackground.colorDay))
            false->root.setBackgroundColor(Color.parseColor(cellBackground.colorNight))
        }
    }

    private fun setCellHeader(cellTitle: String, cellAction: String, cellTitleTv: TextView, cellActionBtn: Button) {
        if(cellTitle.isNotEmpty()) {
            cellTitleTv.text = cellTitle
            cellActionBtn.setOnClickListener {
                it.context.showToast(cellAction)
            }
            cellActionBtn.text = cellAction
            cellTitleTv.visibility = View.VISIBLE
            cellActionBtn.visibility = View.VISIBLE
        } else {
            cellTitleTv.visibility = View.GONE
            cellActionBtn.visibility = View.GONE
        }
    }

    private fun setArticleTitle(type: String, title: String) = "$type :: $title"
    private fun setWidgetTitle(type: String, title: String) = "$type :: $title"


    class NotDefinedVH(
        private val binding: NotDefinedBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {

        }
    }



    inner class ArticleStandardVH(
        private val binding: ArticleStandardBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cell: Cell, position: Int) {
            val item = cell.data[0]
            binding.apply {
                setCellBackground(root,cell.cellBg)
                titleTv.text = setArticleTitle(cell.type, item.title)
                categoryTv.text = item.categoryName
                timeTv.text = item.modifiedGmt
                if(item.media.isNotEmpty())  bannerImg.load(item.media[0].images.mediumLarge, bannerImg.context.imageLoader)

            }
        }
    }

    inner class ArticlePhotosVH(
        private val binding: ArticlePhotosBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cell: Cell, position: Int) {
            val item= cell.data[0]
            binding.apply {
                setCellBackground(root,cell.cellBg)
                titleTv.text = setArticleTitle(cell.type, item.title)
            }
        }
    }

    inner class ArticleVideoVH(
        private val binding: ArticleVideoBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cell: Cell, position: Int) {
            val item= cell.data[0]
            binding.apply {
                setCellBackground(root,cell.cellBg)
                titleTv.text = setArticleTitle(cell.type, item.title)
            }
        }
    }

    inner class WidgetWebViewVH(
        private val binding: WidgetWebviewBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cell: Cell) {
            binding.apply {
                setCellBackground(root,cell.cellBg)
                webView.loadUrl(cell.link)
            }
        }
    }

    inner class WidgetHeroPlainWidgetVH(
        private val binding: WidgetVtHeroPlainWidgetBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cell: Cell, position: Int) {
            binding.apply {
                cell.apply {
                    setCellBackground(root, cellBg)
                    setCellHeader(cellTitle = setWidgetTitle(type, title), cellAction = link,
                        cellTitleTv= cellTitleTv, cellActionBtn= cellActionBtn)
                }
                cell.data[0].apply {
                    if(media.isNotEmpty())  bannerImg.load(media[0].images.mediumLarge, bannerImg.context.imageLoader)
                    titleTv.text = title
                    categoryTv.text = categoryName
                    timeTv.text = modifiedGmt
                    recyclerView.adapter = HighlightsAdapter(highlights)
                }

            }
        }
    }


    inner class WidgetTopNewsCorousalVH(
        private val binding: WidgetVtTopNewsCorousalBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cell: Cell, position: Int, viewType: ViewType) {
            binding.apply {
                setCellBackground(root,cell.cellBg)
                val adapter = ItemRecyclerAdapter(listener, cell, viewType)
                pager.adapter = adapter
                cellTitle.text = cell.title
            }

        }

    }

    inner class WidgetPlainWithCorousalVH(
        private val binding: WidgetVtPlainWithCorousalBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cell: Cell, position: Int, viewType: ViewType) {
            binding.apply {
                setCellBackground(root,cell.cellBg)
                val adapter = ItemRecyclerAdapter(listener, cell, viewType)
                pager.adapter = adapter
            }
        }
    }
    inner class WidgetStackCardWithCorousalVH(
        private val binding: WidgetVtStackCardWithCorousalBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cell: Cell, position: Int, viewType: ViewType) {
            binding.apply {
                setCellBackground(root,cell.cellBg)
                val adapter = ItemRecyclerAdapter(listener, cell, viewType)
                pager.adapter = adapter
            }

        }
    }

    inner class WidgetAllTopicsVH(
        private val binding: WidgetVtAllTopicsWidgetBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cell: Cell, position: Int) {
            binding.apply {
                setCellBackground(root, cell.cellBg)
            }
        }
    }

    inner class WidgetForYouVH(
        private val binding: WidgetVtForYouWidgetBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cell: Cell, position: Int) {
            binding.apply {
                setCellBackground(root, cell.cellBg)
            }
        }
    }



    inner class WidgetGlamourVH(
        private val binding: WidgetVtGlamourBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cell: Cell, viewType: ViewType) {
            binding.apply {
                cell.apply {
                    setCellBackground(root, cellBg)
                    setCellHeader(cellTitle = setWidgetTitle(type, title), cellAction = link,
                        cellTitleTv= cellTitleTv, cellActionBtn= cellActionBtn)
                }
                glamourRecyclerView.onFlingListener = null
                val linearSnapHelper: LinearSnapHelper = SnapHelperOneByOne()
                linearSnapHelper.attachToRecyclerView(glamourRecyclerView)
                glamourRecyclerView.adapter = ItemPagerAdapter(listener, cell, viewType)
            }
        }
    }

    inner class WidgetGalleryVH(
        private val binding: WidgetVtGalleryBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cell: Cell, viewType: ViewType) {
            binding.apply {
                cell.apply {
                    setCellBackground(root, cellBg)
                    setCellHeader(cellTitle = setWidgetTitle(type, title), cellAction = link,
                        cellTitleTv= cellTitleTv, cellActionBtn= cellActionBtn)
                }
                viewPager.adapter = ItemPagerAdapter(listener, cell, viewType)
                transformation1(viewPager)
                val currentItem = 1
                viewPager.currentItem = currentItem
                galleryTitleTv.text = cell.data[currentItem].title
                viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback(){
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        galleryTitleTv.text = cell.data[position].title
                    }
                })
            }
        }
    }

    private fun transformation1(vpLocations: ViewPager2) {
        vpLocations.clipToPadding = false
        vpLocations.clipChildren = false
        vpLocations.offscreenPageLimit = 3
        vpLocations.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        val compositeTransformer = CompositePageTransformer()
        compositeTransformer.addTransformer(MarginPageTransformer(40))
        compositeTransformer.addTransformer { page, position ->
            val r = 1 - kotlin.math.abs(position)
            page.scaleY = (0.90f + r * 0.10f)
        }
        vpLocations.setPageTransformer(compositeTransformer)
    }


}