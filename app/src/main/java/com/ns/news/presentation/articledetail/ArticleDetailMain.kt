package com.ns.articledetail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.ns.articledetail.adapter.ArticleDetailViewpagerAdapter
import com.ns.articledetail.data.ArticleData
import com.ns.articledetail.model.ArticleDetailViewModel
import com.ns.news.databinding.ActivityArticleDetailMainBinding

class ArticleDetailMain : AppCompatActivity() {
    lateinit var articleDetailPagerAdapter: ArticleDetailViewpagerAdapter
    lateinit var articleDetailViewModel: ArticleDetailViewModel
    lateinit var binding: ActivityArticleDetailMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArticleDetailMainBinding.inflate(layoutInflater)
        articleDetailViewModel = ViewModelProvider(this).get(ArticleDetailViewModel::class.java)
        articleDetailViewModel.articleDetailLiveData.observe(this) { articleList ->
            setupDetailViewPager(articleList)
        }

        binding.viewPagerArticleDetail.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                articleDetailViewModel.getArticleValue(position)
            }
        })
    }

    private fun setupDetailViewPager( contentList:List<ArticleData> ) {
        articleDetailPagerAdapter = ArticleDetailViewpagerAdapter(this, contentList)
        binding.viewPagerArticleDetail.adapter = articleDetailPagerAdapter

    }
}