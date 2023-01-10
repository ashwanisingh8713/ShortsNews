package com.ns.news.presentation.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ns.articledetail.data.ArticleData
import com.ns.news.presentation.activity.ui.detail.ItemDetailFragment

class ArticleDetailViewpagerAdapter(fragment: Fragment, var stringList: List<ArticleData>) : FragmentStateAdapter(fragment){
    override fun getItemCount(): Int {
       return stringList.size
    }

    override fun createFragment(position: Int): Fragment {
        return  ItemDetailFragment(stringList.get(position).articleId,position)
    }
}