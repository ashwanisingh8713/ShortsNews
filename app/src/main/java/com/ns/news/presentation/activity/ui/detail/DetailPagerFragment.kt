package com.ns.news.presentation.activity.ui.detail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.ns.news.data.api.model.AWDataItem
import com.ns.news.data.db.NewsDb
import com.ns.news.databinding.FragmentDetailPagerBinding
import com.ns.news.presentation.activity.NewsSharedViewModel
import com.ns.news.presentation.activity.NewsSharedViewModelFactory
import com.ns.news.presentation.activity.SharedChannelEvent
import com.ns.news.presentation.activity.ui.bookmark.BookmarkViewModel
import com.ns.news.presentation.activity.ui.bookmark.BookmarkViewModelFactory
import com.ns.news.presentation.adapter.ArticleDetailViewpagerAdapter
import com.ns.news.presentation.adapter.BookmarkAdapter
import com.ns.news.utils.loadSvg
import kotlinx.coroutines.launch

class DetailPagerFragment : Fragment() {
    private lateinit var articleDetailPagerAdapter: ArticleDetailViewpagerAdapter
    private val viewModel: ArticleDetailViewModel by viewModels { ArticleDetailViewModelFactory }
    private val newsShareViewModel: NewsSharedViewModel by activityViewModels { NewsSharedViewModelFactory }
    private var _binding: FragmentDetailPagerBinding? = null
    private val binding get() = _binding!!
    private val args: DetailPagerFragmentArgs by navArgs()
    private var awDataItem: AWDataItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailPagerBinding.inflate(inflater, container, false)
        val root: View = binding.root
        loadToolbarAssets()
        loadArticles()
        return root
    }

    private fun loadToolbarAssets() {
        binding.backButtonToolbar.loadSvg("file:///android_asset/back_button.svg")
        binding.buttonShare.loadSvg("file:///android_asset/share.svg")
        binding.buttonComment.loadSvg("file:///android_asset/comment.svg")
        binding.buttonBookmark.loadSvg("file:///android_asset/bookmark.svg")
        binding.logoSize.loadSvg("file:///android_asset/text_size.svg")
        binding.buttonSpeak.loadSvg("file:///android_asset/speak.svg")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
            }
        })
        observeUpdates()
        topbarOptionsClick()

    }

    override fun onResume() {
        super.onResume()
        newsShareViewModel.disableDrawer()
    }

    override fun onPause() {
        super.onPause()

        if(args.sectionId.equals("Bookmark")) {
            newsShareViewModel.refreshBookmarkList()
        } else {
            newsShareViewModel.enableDrawer()
        }
    }

    /**
     * Listening all Cell's Articles of respective Section
     */
    private fun loadArticles() {
        if(args.sectionId.equals("Bookmark")) {
            lifecycleScope.launchWhenStarted {
                val viewModel = ViewModelProvider(this@DetailPagerFragment, BookmarkViewModelFactory(NewsDb.create(requireActivity()).bookmarkDao()))[BookmarkViewModel::class.java]
                viewModel.getAllBookmarks().collect{
                    val awList = mutableListOf<AWDataItem>()
                    it.reversed().map { awList.add(it.data) }
                    setupDetailViewPager(awList)
                    var awData = AWDataItem(articleId = args.articleId)
                    val index = awList.indexOf(awData)
                    if (index != -1) {
                        binding.viewPager.currentItem = index
                    }
                }
            }
        } else {
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.getArticle(
                    sectionId = args.sectionId,
                    cellType = args.cellType,
                    type = args.type
                ).collect {
                    val awList = mutableListOf<AWDataItem>()
                    it.map { awList.addAll(it.data) }
                    setupDetailViewPager(awList)
                    var awData = AWDataItem(articleId = args.articleId)
                    val index = awList.indexOf(awData)
                    if (index != -1) {
                        binding.viewPager.currentItem = index
                    }
                }
            }
        }
    }

    private fun observeUpdates() {
        // Collect current visible Article
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            newsShareViewModel.detailArticle.collect {
                awDataItem = it
                awDataItem?.articleId?.let {
                        it1 ->
                    run {
                        newsShareViewModel.readArticle(it1)
                        newsShareViewModel.checkArticleBookmarked(it1)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            newsShareViewModel.sharedChannelEvent.collect {
                updateBookmarkUI(it)
            }
        }
    }

    private fun setupDetailViewPager(contentList: List<AWDataItem>) {
        articleDetailPagerAdapter = ArticleDetailViewpagerAdapter(this, contentList)
        binding.viewPager.adapter = articleDetailPagerAdapter

    }

    private fun topbarOptionsClick() {
        binding.buttonBookmark.setOnClickListener {
            awDataItem?.let { it1 ->
                newsShareViewModel.addToBookmark(it1, args.cellType)
            }
        }

        binding.buttonRemoveBookmark.setOnClickListener {
            awDataItem?.let { it1 ->
                newsShareViewModel.removeFromBookmark(it1.articleId)
            }
        }
    }

    private fun updateBookmarkUI(bookmarkEvent:SharedChannelEvent) {
        when(bookmarkEvent){
            SharedChannelEvent.BOOKMARKED-> {
                binding.buttonBookmark.visibility = View.GONE
                binding.buttonRemoveBookmark.visibility = View.VISIBLE
            }
            SharedChannelEvent.UN_BOOKMARKED->{
                binding.buttonBookmark.visibility = View.VISIBLE
                binding.buttonRemoveBookmark.visibility = View.GONE
            }
            else->Log.i("Ashwani", "Something went wrong....In Bookmark Logic")
        }
    }


}