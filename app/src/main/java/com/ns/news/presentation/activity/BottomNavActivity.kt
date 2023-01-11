package com.ns.news.presentation.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.ns.news.R
import com.ns.news.databinding.ActivityBottomNavBinding
import com.ns.news.databinding.ContentNavigationHeaderBinding
import com.ns.news.presentation.activity.ui.home.SectionDBViewModel
import com.ns.news.presentation.activity.ui.home.SectionDBViewModelFactory
import com.ns.news.presentation.activity.ui.settings.SettingsActivity
import com.ns.news.presentation.adapter.NavigationExpandableListViewAdapter
import com.ns.news.utils.loadSvg


class BottomNavActivity : AppCompatActivity() {

    private val viewModel: SectionApiViewModel by viewModels { SectionViewModelFactory }
    private val viewModelHamburger: SectionDBViewModel by viewModels { SectionDBViewModelFactory }
    private val launchShareViewModel: NewsSharedViewModel by viewModels { NewsSharedViewModelFactory}
    private lateinit var expandableListViewAdapter: NavigationExpandableListViewAdapter
    private lateinit var binding: ActivityBottomNavBinding
    private lateinit var navigationHeaderBinding: ContentNavigationHeaderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBottomNavBinding.inflate(layoutInflater)
        navigationHeaderBinding = binding.navigationViewContent
        setContentView(binding.root)

        // This callback will only be called when MyFragment is at least Started.
        // This callback will only be called when MyFragment is at least Started.
        /*val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {

                }
            }
        onBackPressedDispatcher.addCallback(this, callback)*/


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

        //search
        binding.navigationViewContent.buttonClose.setOnClickListener {
            val intent = Intent(this,SettingsActivity::class.java)
            startActivity(intent)
        }

        observeDrawerItems()
        requestSections()
        showIcons()
        observeSharedClickEvents()
    }

    /**
     * Observing Drawer Click Event
     */
    private fun observeSharedClickEvents() {
        lifecycleScope.launchWhenStarted {
            launchShareViewModel.drawerSharedClick.collect {
                when(it) {
                    SharedClickEvent.DRAWER_OPEN-> binding.drawerLayout.open()
                    SharedClickEvent.DRAWER_CLOSE-> binding.drawerLayout.close()
                }
            }
        }
    }


    /**
     * Observing Drawer related Data
     */
    private fun observeDrawerItems() {
        lifecycleScope.launchWhenStarted {
            viewModelHamburger.getHamburger().collect {
                expandableListViewAdapter = NavigationExpandableListViewAdapter(this@BottomNavActivity, it, launchShareViewModel)
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
        navigationHeaderBinding.logoNavigation.loadSvg("file:///android_asset/logo_hamburger.svg")

        navigationHeaderBinding.buttonClose.loadSvg("file:///android_asset/close_icon_hamburger.svg")
        navigationHeaderBinding.buttonClose.setOnClickListener {binding.drawerLayout.close()}
    }

}