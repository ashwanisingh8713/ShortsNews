package com.ns.news.presentation.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.lifecycleScope
import com.example.videopager.ui.MainActivity
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.ns.news.R
import com.ns.news.databinding.ActivityBottomNavBinding
import com.ns.news.databinding.ContentNavigationHeaderBinding
import com.ns.news.presentation.adapter.NavigationExpandableListViewAdapter
import com.ns.news.presentation.shared.SectionTypeSharedViewModel
import com.ns.news.presentation.shared.SectionTypeSharedViewModelFactory
import com.ns.news.utils.Constants
import com.ns.news.utils.CommonFunctions
import com.ns.news.utils.IntentUtils

class BottomNavActivity : AppCompatActivity() {

    private val viewModel: SectionViewModel by viewModels { SectionViewModelFactory }
    private val sharedSectionViewModel: SectionTypeSharedViewModel by viewModels { SectionTypeSharedViewModelFactory() }
    private lateinit var expandableListViewAdapter: NavigationExpandableListViewAdapter

    private lateinit var binding: ActivityBottomNavBinding
    private lateinit var navigationHeaderBinding: ContentNavigationHeaderBinding
    private var flag = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBottomNavBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navigationHeaderBinding = binding.navigationViewContent

        binding.viewPager.adapter = BottomNavPagerAdapter(this)

        // To Enable or Disable swipe of Pager
        binding.viewPager.isUserInputEnabled = false;
        binding.fab.setOnClickListener {
            CommonFunctions.animateFab(flag, binding.fab, binding.sheetParent, this)
            flag = !flag
        }
        binding.articleShortOption.setOnClickListener {
            IntentUtils.openArticleShorts(this, Constants.shortsArticleIntentTag)
        }
        binding.videoShortOption.setOnClickListener {
//            IntentUtils.openArticleShorts(this, Constants.shortsVideoIntentTag)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        binding.podcastShortOption.setOnClickListener {
            IntentUtils.openArticleShorts(this, Constants.podcastIntentTag)
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

        //Toolbar Navigation Button Click
        binding.toolbar.setNavigationOnClickListener { binding.drawerLayout.open() }

        observeViewStateUpdates()
        requestSections()

        showIcons();
    }

    private fun AppCompatImageView.loadSvg(url: String) {
        val imageLoader = ImageLoader.Builder(context)
            .components { add(SvgDecoder.Factory()) }
            .build()

        val request = ImageRequest.Builder(this.context)
            .crossfade(true)
            .crossfade(500)
            .data(url)
            .target(this)
            .build()

        imageLoader.enqueue(request)
    }

    /**
     * Observing Language & State Data
     */
    private fun observeViewStateUpdates() {
        lifecycleScope.launchWhenStarted {
            viewModel.viewState.collect{
                sharedSectionViewModel.setDrawerSections(it.first)
                sharedSectionViewModel.setBreadcrumbSections(it.second)

                expandableListViewAdapter = NavigationExpandableListViewAdapter(this@BottomNavActivity, it.first)
                navigationHeaderBinding.expandableListViewNavigation.setAdapter(expandableListViewAdapter)
            }
        }
    }

    /**
     * Making API Request to Get Sections Data (Breadcrumb and Drawer Data)
     */
    private fun requestSections() {
        viewModel.requestSections()
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
    }

}