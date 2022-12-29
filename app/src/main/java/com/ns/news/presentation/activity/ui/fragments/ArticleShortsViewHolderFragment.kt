package com.ns.news.presentation.activity.ui.fragments

import android.os.Build
import android.os.Build.VERSION
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import coil.load
import com.ns.news.R
import com.ns.news.databinding.FragmentArticleShortsViewHolderBinding
import com.ns.news.presentation.activity.ui.shorts.adapter.ArticleShortsPagerAdapter
import com.ns.news.presentation.activity.ui.shorts.data.ArticleShortsData

class ArticleShortsViewHolderFragment : Fragment() {
    private var articleShort: ArticleShortsData? = null
     lateinit var binding: FragmentArticleShortsViewHolderBinding

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if (VERSION.SDK_INT>=33){
                articleShort = it.getParcelable(ARM_ARTICLE_SHORT, ArticleShortsData::class.java)
            }
            articleShort = it.getParcelable(ARM_ARTICLE_SHORT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentArticleShortsViewHolderBinding.inflate(inflater, container, false)
        val root = binding.root
        setupView()
        return root
    }

    companion object {
        private const val ARM_ARTICLE_SHORT = "shorts"
        @JvmStatic
        fun newInstance(param1: ArticleShortsData) =
            ArticleShortsViewHolderFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARM_ARTICLE_SHORT, param1)
                }
            }
    }
    fun setupView(){
        articleShort.let {
            binding.imageView.load(it?.articleImage)
            binding.articleSection?.text = it?.section
            binding.articlePublishTime.text = it?.timeStamp
            binding.articleTitle.text = it?.articleTitle
            binding.shortDes?.text = it?.shortDes?.get(0)
        }

    }
}