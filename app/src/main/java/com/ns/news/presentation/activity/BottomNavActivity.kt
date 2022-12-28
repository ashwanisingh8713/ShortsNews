package com.ns.news.presentation.activity

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.ns.news.R
import com.ns.news.databinding.ActivityBottomNavBinding

class BottomNavActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBottomNavBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBottomNavBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewPager: ViewPager2 = binding.viewPager
        viewPager.adapter = BottomNavPagerAdapter(this)

        val navView: BottomNavigationView = binding.bottomNavView
        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home->{
                    viewPager.currentItem = 0
                }
                R.id.navigation_dashboard->{viewPager.currentItem = 1 }
                R.id.navigation_notifications->{viewPager.currentItem = 2 }
                R.id.navigation_more->{viewPager.currentItem = 3 }

            }
            true
        }



    }
}