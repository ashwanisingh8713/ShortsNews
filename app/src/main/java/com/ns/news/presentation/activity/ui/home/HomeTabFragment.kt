package com.ns.news.presentation.activity.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.ns.news.data.db.Section
import com.ns.news.databinding.FragmentHomeBinding
import com.ns.news.presentation.activity.LaunchSharedViewModel
import com.ns.news.presentation.activity.LaunchSharedViewModelFactory

class HomeTabFragment : Fragment() {

//    private val sharedSectionViewModel: SectionTypeSharedViewModel by activityViewModels { SectionTypeSharedViewModelFactory() }
    private val viewModel: SectionDBViewModel by viewModels { SectionDBViewModelFactory }
    private val launchShareViewModel: LaunchSharedViewModel by activityViewModels { LaunchSharedViewModelFactory }
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var sectionsPagerAdapter: SectionsPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        observeSectionUpdates()
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeNavigationDrawerClicks()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Observing Navigation Drawer Click Event
     */
    private fun observeNavigationDrawerClicks() {
        lifecycleScope.launchWhenStarted {
            launchShareViewModel.navigationItemClick.collect {
                val index = sectionsPagerAdapter.sections.indexOf(it.first)
                if(index != -1) binding.viewPager.currentItem = index
            }
        }
    }

    /**
     * Observing Breadcrumb Data
     */
    private fun observeSectionUpdates() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.getBreadcrumb().collect {
                setupUI(it)
            }
        }
    }


    private fun setupUI(sections: List<Section>) {
        sectionsPagerAdapter = SectionsPagerAdapter(this, sections)
        binding.apply {
            viewPager.isUserInputEnabled = false
            viewPager.adapter = sectionsPagerAdapter
            val tabs: TabLayout = binding.tabs
            TabLayoutMediator(tabs, viewPager) { tab, position ->
                tab.text = sections.get(position).name
            }.attach()
        }

    }
}