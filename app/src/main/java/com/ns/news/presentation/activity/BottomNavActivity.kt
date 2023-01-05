package com.ns.news.presentation.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.videopager.ui.MainActivity
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.google.android.material.snackbar.Snackbar
import com.ns.news.R
import com.ns.news.databinding.ActivityBottomNavBinding
import com.ns.news.databinding.ContentNavigationHeaderBinding
import com.ns.news.presentation.activity.ui.home.SectionDBViewModel
import com.ns.news.presentation.activity.ui.home.SectionDBViewModelFactory
import com.ns.news.presentation.adapter.NavigationExpandableListViewAdapter
import com.ns.news.presentation.shared.SectionTypeSharedViewModel
import com.ns.news.presentation.shared.SectionTypeSharedViewModelFactory
import com.ns.news.utils.Constants
import com.ns.news.utils.CommonFunctions
import com.ns.news.utils.IntentUtils
import com.ns.news.utils.loadSvg

class BottomNavActivity : AppCompatActivity() {

    private val viewModel: SectionViewModel by viewModels { SectionViewModelFactory }
    private val viewModelHamburger: SectionDBViewModel by viewModels { SectionDBViewModelFactory }
//    private val sharedSectionViewModel: SectionTypeSharedViewModel by viewModels { SectionTypeSharedViewModelFactory() }
    private lateinit var expandableListViewAdapter: NavigationExpandableListViewAdapter
    private lateinit var binding: ActivityBottomNavBinding
    private lateinit var navigationHeaderBinding: ContentNavigationHeaderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBottomNavBinding.inflate(layoutInflater)
        navigationHeaderBinding = binding.navigationViewContent
        setContentView(binding.root)

        binding.viewPager.adapter = BottomNavPagerAdapter(this)

        // To Enable or Disable swipe of Pager
        binding.viewPager.isUserInputEnabled = false;
        binding.fab.setupParentLayout(binding.sheetParent)
        binding.articleShortOption.setupArticleOptionView(this,binding.articleShortOption,this)
        binding.videoShortOption.setupVideoOptionView(this,binding.videoShortOption,this)
        binding.podcastShortOption.setupPodcastOptionView(this,binding.podcastShortOption,this)
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

        //Toolbar Navigation Button Click
        binding.toolbar.setNavigationOnClickListener { binding.drawerLayout.open() }

        observeViewStateUpdates()
        requestSections()

        showIcons();
    }

    /**
     * Observing Language & State Data
     */
    private fun observeViewStateUpdates() {
        lifecycleScope.launchWhenStarted {
            viewModelHamburger.getHamburger().collect {
                expandableListViewAdapter = NavigationExpandableListViewAdapter(this@BottomNavActivity, it)
                navigationHeaderBinding.expandableListViewNavigation.setAdapter(expandableListViewAdapter)
            }
        }
    }

    /**
     * Making API Request to Get Sections Data (Breadcrumb and Drawer Data)
     */
    private fun requestSections() {
        viewModel.requestSections("1")
    }

    /**
     * It shows Icons
     */
    fun showIcons() {
        binding.buttonHamburger.loadSvg("file:///android_asset/ic_hamburger.svg")
        binding.buttonHamburger.setOnClickListener {binding.drawerLayout.open()}

        binding.logoHamburger.loadSvg("file:///android_asset/logo_hamburger.svg")

        binding.buttonLivetv.loadSvg("file:///android_asset/live_tv_icon.svg")

        binding.buttonNotification.loadSvg("file:///android_asset/notification_icon.svg")

        binding.buttonSearch.loadSvg("file:///android_asset/search_icon.svg")

        navigationHeaderBinding.logoNavigation.loadSvg("file:///android_asset/logo_hamburger.svg")

        navigationHeaderBinding.buttonClose.loadSvg("file:///android_asset/close_icon_hamburger.svg")
        navigationHeaderBinding.buttonClose.setOnClickListener {binding.drawerLayout.close()}
    }

}