package com.ns.news.presentation.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.view.animation.Animation
import android.view.animation.OvershootInterpolator
import android.view.animation.TranslateAnimation
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ns.news.R
import com.ns.news.databinding.ActivityBottomNavBinding

class BottomNavActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBottomNavBinding
    private var flag = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBottomNavBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewPager.adapter = BottomNavPagerAdapter(this)

        // To Enable or Disable swipe of Pager
        binding.viewPager.isUserInputEnabled = false;
        binding.fab.setOnClickListener {
            animateFab(flag)
        }

        binding.bottomNavView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home->{
                    binding.viewPager.currentItem = 0
                }
                R.id.navigation_dashboard->{binding.viewPager.currentItem = 1 }
                R.id.navigation_notifications->{binding.viewPager.currentItem = 2 }
                R.id.navigation_more->{binding.viewPager.currentItem = 3 }

            }
            true
        }

        // NavigationDrawer Toggle Action Setup
        val toggle = object : ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        ) {
            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)

            }
        }
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Toolbar Navigation Button Click
        binding.toolbar.setNavigationOnClickListener(object : OnClickListener {
            override fun onClick(v: View?) {
                binding.drawerLayout.open()
            }

        })




        // Toolbar Navigation Button Click
        binding.toolbar.setNavigationOnClickListener(object : OnClickListener {
            override fun onClick(v: View?) {
                binding.drawerLayout.open()
            }

        })




    }
    fun animateFab(flags: Boolean) {
        val interpolator = OvershootInterpolator()
        ViewCompat.animate(binding.fab).setInterpolator(interpolator).setListener(null)
            .rotationBy(360f).withLayer().setDuration(900).start()
        if (flags) {
            binding.fab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.fab_cross))
            slideUp(binding.sheetParent)
            flag = false
        } else if (!flags) {
            binding.fab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.fab_image))
            slideDown(binding.sheetParent)
            flag = true
        }

    }

    fun slideUp(view: View) {
        val animate = TranslateAnimation(
            0F,  // fromXDelta
            0F,  // toXDelta
            view.height.toFloat(),  // fromYDelta
            0F
        ) // toYDelta
        animate.duration = 500
        animate.fillBefore = false
        animate.fillBefore = true
        view.startAnimation(animate)
        animate.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                binding.sheetParent.visibility = View.VISIBLE
            }

            override fun onAnimationEnd(animation: Animation) {}
            override fun onAnimationRepeat(animation: Animation) {}
        })
    }

    fun slideDown(view: View) {
        var animate = TranslateAnimation(
            0F,  // fromXDelta
            0F,  // toXDelta
            0F,  // fromYDelta
            binding.sheetParent.height.toFloat()
        ) // toYDelta
        animate.duration = 500
        animate.isFillEnabled = false
        animate.repeatCount = 0
        view.startAnimation(animate)
        animate.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                binding.sheetParent.clearAnimation()
                binding.sheetParent.visibility = View.GONE
            }

            override fun onAnimationRepeat(animation: Animation) {
                Log.i("","")
            }
        })
    }

}