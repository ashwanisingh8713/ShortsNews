package com.ns.articledetail.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ns.articledetail.data.ArticleData
import com.ns.articledetail.fragment.ItemDetailFragment

class ArticleDetailViewpagerAdapter(fragmentActivity: FragmentActivity, var stringList: List<ArticleData>) : FragmentStateAdapter(fragmentActivity){
    override fun getItemCount(): Int {
       return stringList.size
    }

    override fun createFragment(position: Int): Fragment {
        return  ItemDetailFragment(stringList.get(position).articleId,position)
    }
}