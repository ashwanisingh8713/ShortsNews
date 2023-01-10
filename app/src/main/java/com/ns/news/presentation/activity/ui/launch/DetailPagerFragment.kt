package com.ns.news.presentation.activity.ui.launch

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.ns.articledetail.data.ArticleData
import com.ns.news.databinding.FragmentDetailBinding
import com.ns.news.presentation.adapter.ArticleDetailViewpagerAdapter
import com.ns.news.presentation.activity.ui.detail.ArticleDetailViewModel

class DetailPagerFragment : Fragment() {
    lateinit var articleDetailPagerAdapter: ArticleDetailViewpagerAdapter
    lateinit var articleDetailViewModel: ArticleDetailViewModel
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

        Log.i("", "${args.type}")

        articleDetailViewModel = ViewModelProvider(this).get(ArticleDetailViewModel::class.java)
        activity?.let {
            articleDetailViewModel.articleDetailLiveData.observe(it) { articleList ->
                setupDetailViewPager(articleList)
            }
        }

        binding.viewPagerArticleDetail.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                articleDetailViewModel.getArticleValue(position)
            }
        })

        return root
    }

    private fun setupDetailViewPager( contentList:List<ArticleData> ) {
        articleDetailPagerAdapter = ArticleDetailViewpagerAdapter(this, contentList)
        binding.viewPagerArticleDetail.adapter = articleDetailPagerAdapter

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }


}