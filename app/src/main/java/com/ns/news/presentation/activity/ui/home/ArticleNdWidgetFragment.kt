package com.ns.news.presentation.activity.ui.home

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ns.news.databinding.FragmentArticleNdWidgetBinding
import com.ns.news.domain.model.Section
import com.ns.news.presentation.activity.ArticleNdWidgetViewModel
import com.ns.news.presentation.activity.ArticleNdWidgetViewModelFactory
import com.ns.news.presentation.adapter.ArticleNdWidgetAdapter
import kotlinx.coroutines.flow.collectLatest


class ArticleNdWidgetFragment : Fragment() {

    private val adapter = ArticleNdWidgetAdapter()
    private val viewModel: ArticleNdWidgetViewModel by viewModels { ArticleNdWidgetViewModelFactory }

    private var _binding: FragmentArticleNdWidgetBinding? = null
    private lateinit var section: Section

    private val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        section = if (Build.VERSION.SDK_INT >= 33) {
            arguments?.getParcelable(ARG_SECTION, Section::class.java)!!
        } else {
            arguments?.getParcelable(ARG_SECTION)!!
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentArticleNdWidgetBinding.inflate(inflater, container, false)
        val root = binding.root
        binding.recyclerView.adapter = adapter
        requestPaginationApi()
        return root
    }


    /**
     * Making API Pagination API Request to get Section's Article & Widget
     */
    private fun requestPaginationApi() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.getArticleNdWidget(section.sectionApi)
                .collectLatest {
                    adapter.submitData(it)
                    Log.i("Ashwani", "Response :: ArticleNdWidget")
                }
        }
    }

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private const val ARG_SECTION = "section"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        @JvmStatic
        fun newInstance(section: Section): ArticleNdWidgetFragment {
            return ArticleNdWidgetFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_SECTION, section)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}