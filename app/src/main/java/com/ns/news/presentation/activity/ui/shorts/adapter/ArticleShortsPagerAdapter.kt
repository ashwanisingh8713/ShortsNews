package com.ns.news.presentation.activity.ui.shorts.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ns.news.presentation.activity.ui.fragments.ArticleShortsViewHolderFragment
import com.ns.news.presentation.activity.ui.shorts.data.ArticleShortsData

class ArticleShortsPagerAdapter(fragment:Fragment, private val articleShortsList:List<ArticleShortsData>):FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int  = articleShortsList.size

    override fun createFragment(position: Int): Fragment {
      return ArticleShortsViewHolderFragment.newInstance(articleShortsList[position])
    }
}