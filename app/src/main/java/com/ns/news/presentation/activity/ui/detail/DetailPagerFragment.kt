package com.ns.news.presentation.activity.ui.detail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.ns.news.data.api.model.AWDataItem
import com.ns.news.data.db.Cell
import com.ns.news.data.db.TableRead
import com.ns.news.databinding.FragmentDetailPagerBinding
import com.ns.news.presentation.activity.NewsSharedViewModel
import com.ns.news.presentation.activity.NewsSharedViewModelFactory
import com.ns.news.presentation.activity.SharedChannelEvent
import com.ns.news.presentation.adapter.ArticleDetailViewpagerAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailPagerFragment : Fragment() {
    private lateinit var articleDetailPagerAdapter: ArticleDetailViewpagerAdapter
    private val viewModel: ArticleDetailViewModel by viewModels { ArticleDetailViewModelFactory }
    private val newsShareViewModel: NewsSharedViewModel by activityViewModels { NewsSharedViewModelFactory }
    private var _binding: FragmentDetailPagerBinding? = null
    private val binding get() = _binding!!
    private val args: DetailPagerFragmentArgs by navArgs()
    private var awDataItem: AWDataItem? = null
    private var cell: Cell? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        cell = args.cell
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailPagerBinding.inflate(inflater, container, false)
        val root: View = binding.root
        observeArticles()
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
            }
        })

        topbarOptionsClick()
    }

    override fun onResume() {
        super.onResume()
        newsShareViewModel.disableDrawer()
    }

    override fun onPause() {
        super.onPause()
        newsShareViewModel.enableDrawer()
    }

    /**
     * Listening all Cell's Articles of respective Section
     */
    private fun observeArticles() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.getArticle(
                sectionId = cell!!.sectionId,
                cellType = cell!!.cellType,
                type = cell!!.type
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
                newsShareViewModel.addToBookmark(it1, cell!!.cellType)
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