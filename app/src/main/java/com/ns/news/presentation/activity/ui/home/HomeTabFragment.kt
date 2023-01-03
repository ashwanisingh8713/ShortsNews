package com.ns.news.presentation.activity.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.ns.news.databinding.FragmentHomeBinding
import com.ns.news.domain.model.SectionDrawer
import com.ns.news.presentation.shared.SectionTypeSharedViewModel
import com.ns.news.presentation.shared.SectionTypeSharedViewModelFactory

class HomeTabFragment : Fragment() {

    private val sharedSectionViewModel: SectionTypeSharedViewModel by activityViewModels { SectionTypeSharedViewModelFactory() }

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        observeSectionUpdates()

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    fun setupUI(sections: List<SectionDrawer>) {
        val sectionsPagerAdapter = SectionsPagerAdapter(this, sections)
        val viewPager: ViewPager2 = binding.viewPager
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = binding.tabs

        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = sections.get(position).sectionName
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Observing Breadcrumb Data
     */
    private fun observeSectionUpdates() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            sharedSectionViewModel.breadcrumbSharedViewModel.collect {
                setupUI(it)
            }
        }
    }
}