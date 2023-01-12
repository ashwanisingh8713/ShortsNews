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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailPagerBinding.inflate(inflater, container, false)
        val root: View = binding.root

        /*Log.i("Ashwani", "${args.cellType}")
        Log.i("Ashwani", "${args.type}")
        Log.i("Ashwani", "${args.sectionId}")
        Log.i("Ashwani", "ArticleId :: ${args.articleId}")*/

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

    /**
     * Listening all Cell's Articles of respective Section
     */
    private fun observeArticles() {
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
                when(it){
                    SharedChannelEvent.BOOKMARKED-> Log.i("Ashwani", "IS BOOKMARKED")
                    SharedChannelEvent.UN_BOOKMARKED->Log.i("Ashwani", "NOT BOOKMARKED")
                    else->Log.i("Ashwani", "Something went wrong....In Bookmark Logic")
                }
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
                newsShareViewModel.addToBookmark(it1)
            }
        }

        binding.buttonComment.setOnClickListener {
            awDataItem?.let { it1 ->
                newsShareViewModel.removeFromBookmark(it1.articleId)
            }
        }
    }



}