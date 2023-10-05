package com.videopager.ui.view

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

/**
 * Created by Ashwani Kumar Singh on 29,May,2023.
 */
class LastPageHalfHeightTransformer : ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        val viewPager = page.parent as RecyclerView
        val offset = viewPager.measuredWidth * position

        if (position == 0f || position == 1f) {
            page.translationX = offset
        } else {
            page.translationX = -offset
        }

        if (position == 1f) {
            page.scaleY = 0.5f // Scale the last page to half height
        } else {
            page.scaleY = 1f
        }
    }
}