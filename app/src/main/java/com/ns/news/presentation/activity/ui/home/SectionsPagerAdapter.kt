package com.ns.news.presentation.activity.ui.home

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ns.news.domain.model.Section


/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(fragment: Fragment, private val sections: List<Section>) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return sections.size
    }

    override fun createFragment(position: Int): Fragment {
        return HomeArticleNdWidgetFragment.newInstance(sections[position])
    }
}