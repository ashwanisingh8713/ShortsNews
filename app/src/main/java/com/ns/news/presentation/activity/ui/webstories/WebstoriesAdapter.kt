package com.ns.news.presentation.activity.ui.webstories

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class WebstoriesAdapter(fragment:Fragment, private val articleShortsList:List<ImageData>):FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int  = articleShortsList.size

    override fun createFragment(position: Int): Fragment {
      return WebstoriesViewHolder.newInstance(articleShortsList[position])
    }
}