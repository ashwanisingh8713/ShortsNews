package com.ns.news.presentation.activity.ui.detail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.ns.news.data.api.model.AWDataItem
import com.ns.news.databinding.FragmentDetailBinding
import com.ns.news.presentation.adapter.ArticleDetailViewpagerAdapter

class DetailPagerFragment : Fragment() {
    private lateinit var articleDetailPagerAdapter: ArticleDetailViewpagerAdapter
    private val viewModel: ArticleDetailViewModel by viewModels { ArticleDetailViewModelFactory }
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private val args: DetailPagerFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
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
    }

    private fun setupDetailViewPager(contentList: List<AWDataItem>) {
        articleDetailPagerAdapter = ArticleDetailViewpagerAdapter(this, contentList)
        binding.viewPager.adapter = articleDetailPagerAdapter

    }

    /**
     * Listening all Cell's Articles of respective Section
     */
    private fun observeArticles() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.getArticle(sectionId = args.sectionId, cellType = args.cellType, type = args.type).collect {
                val awList = mutableListOf<AWDataItem>()
                it.map { awList.addAll(it.data) }
                setupDetailViewPager(awList)
                var awData = AWDataItem(articleId=args.articleId)
                val index = awList.indexOf(awData)
                if(index != -1) {
                    binding.viewPager.currentItem = index
                }
            }
        }
    }


}