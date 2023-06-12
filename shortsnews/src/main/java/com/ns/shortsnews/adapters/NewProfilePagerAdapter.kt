package com.ns.shortsnews.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ns.shortsnews.ui.fragment.BookmarksFragment
import com.ns.shortsnews.ui.fragment.FollowingFragment

class NewProfilePagerAdapter(fragmentManager: FragmentActivity) : FragmentStateAdapter(fragmentManager) {
    override fun getItemCount(): Int {
       return 2
    }

     fun getTabTitle(position : Int): String{
        return when(position) {
            0 -> "Bookmark"
            1 -> "Following"
            else -> "Bookmark"
        }
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> BookmarksFragment()
            1 -> FollowingFragment()
            else -> BookmarksFragment()
        }
    }


}