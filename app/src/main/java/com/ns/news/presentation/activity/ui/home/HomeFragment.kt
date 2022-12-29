package com.ns.news.presentation.activity.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.ns.news.data.api.model.SectionItem
import com.ns.news.databinding.FragmentHomeBinding
import com.ns.news.domain.model.Section
import com.ns.news.presentation.activity.ArticleNdWidgetViewModel
import com.ns.news.presentation.activity.ArticleNdWidgetViewModelFactory
import com.ns.news.presentation.activity.SectionViewModel
import com.ns.news.presentation.activity.SectionViewModelFactory
import com.ns.news.presentation.shared.SectionTypeSharedViewModel
import com.ns.news.presentation.shared.SectionTypeSharedViewModelFactory

class HomeFragment : Fragment() {

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

    fun setupUI(sections: List<Section>) {
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
                Log.i("Ashwani", "Breadcrumb :: "+it.size)
                setupUI(it)
            }
        }
    }
}