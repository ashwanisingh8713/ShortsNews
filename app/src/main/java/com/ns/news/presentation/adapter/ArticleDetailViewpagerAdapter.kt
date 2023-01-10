package com.ns.news.presentation.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ns.news.data.api.model.AWDataItem
import com.ns.news.presentation.activity.ui.detail.ArticleDetailFragment

class ArticleDetailViewpagerAdapter(fragment: Fragment, var cells: List<AWDataItem>) : FragmentStateAdapter(fragment){
    override fun getItemCount(): Int {
       return cells.size
    }

    override fun createFragment(position: Int): Fragment {
        return ArticleDetailFragment.newInstance(cells[position])
    }
}