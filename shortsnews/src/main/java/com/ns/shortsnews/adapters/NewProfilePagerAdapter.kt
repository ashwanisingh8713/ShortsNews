package com.ns.shortsnews.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class NewProfilePagerAdapter(fragmentManager: FragmentActivity) : FragmentStateAdapter(fragmentManager) {
    private val mFragmentList: MutableList<Fragment> = ArrayList()
    private val mFragmentTitleList: MutableList<String> = ArrayList()
    override fun getItemCount(): Int {
       return 2
    }

    public fun getTabTitle(position : Int): String{
        return mFragmentTitleList[position]
    }

    fun addFragment(fragment: Fragment, title: String) {
        mFragmentList.add(fragment)
        mFragmentTitleList.add(title)
    }



    override fun createFragment(position: Int): Fragment {
        return mFragmentList[position]
    }
}