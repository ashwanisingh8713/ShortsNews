package com.ns.news.presentation.activity.ui.launch

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.ns.news.R
import com.ns.news.databinding.FragmentLaunchBinding
import com.ns.news.presentation.activity.ui.search.SearchActivity
import com.ns.news.presentation.activity.NewsSharedViewModel
import com.ns.news.presentation.activity.NewsSharedViewModelFactory
import com.ns.news.utils.loadSvg

class LaunchFragment : Fragment() {
    private var _binding: FragmentLaunchBinding? = null
    private val binding get() = _binding!!
    private val newsShareViewModel: NewsSharedViewModel by activityViewModels { NewsSharedViewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLaunchBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ViewPager2 and It's Adapter Setup
        binding.viewPager.adapter = LaunchNavPagerAdapter(this)

        // To Enable or Disable swipe of Pager
        binding.viewPager.isUserInputEnabled = false;
        binding.fab.setupParentLayout(binding.sheetParent)
        activity?.let { binding.articleShortOption.setupArticleOptionView(it,binding.articleShortOption,it, binding.fab) }
        activity?.let { binding.videoShortOption.setupVideoOptionView(it,binding.videoShortOption,it,  binding.fab) }
        activity?.let { binding.podcastShortOption.setupPodcastOptionView(it,binding.podcastShortOption,it,  binding.fab) }
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
        showIcons()

    }


    /**
     * It shows Icons
     */
    private fun showIcons() {
        binding.buttonHamburger.loadSvg("file:///android_asset/ic_hamburger.svg")
        binding.buttonHamburger.setOnClickListener {
            newsShareViewModel.openDrawer()
        }

        binding.logoHamburger.loadSvg("file:///android_asset/logo_hamburger.svg")

        binding.buttonLivetv.loadSvg("file:///android_asset/live_tv_icon.svg")

        binding.buttonNotification.loadSvg("file:///android_asset/notification_icon.svg")

        binding.buttonSearch.loadSvg("file:///android_asset/search_icon.svg")
        binding.buttonSearch.setOnClickListener {
            val intent = Intent(context,SearchActivity::class.java)
            startActivity(intent)
        }

    }


}