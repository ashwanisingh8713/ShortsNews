package com.ns.news.presentation.activity.ui.bookmark

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ns.news.data.db.Bookmark
import com.ns.news.data.db.NewsDb
import com.ns.news.databinding.FragmentBookmarkBinding
import com.ns.news.presentation.activity.NewsSharedViewModel
import com.ns.news.presentation.activity.NewsSharedViewModelFactory
import com.ns.news.presentation.adapter.BookmarkAdapter


class BookmarkFragment:Fragment(), BookmarkAdapter.Callback {

    private val newsShareViewModel: NewsSharedViewModel by activityViewModels { NewsSharedViewModelFactory }
    private var viewModel: BookmarkViewModel? = null
    private var _binding: FragmentBookmarkBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookmarkBinding.inflate(inflater, container, false)
        val root: View = binding.root
        viewModel = ViewModelProvider(this, BookmarkViewModelFactory(NewsDb.create(requireActivity()).bookmarkDao()))[BookmarkViewModel::class.java]
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenStarted {
            viewModel!!.getAllBookmarks().collect{
                binding.recyclerView.adapter = BookmarkAdapter(it.reversed(), this@BookmarkFragment)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        newsShareViewModel.disableDrawer()
    }

    override fun onPause() {
        super.onPause()
        newsShareViewModel.enableDrawer()
    }

    override fun onBookmarkClick(item: Bookmark) {
//        val direction = LaunchFragmentDirections.actionSectionFragmentToDetailFragment(cellType, type, sectionId, articleId)
//        val direction = BookmarkFragmentDirections.actionSectionFragmentToDetailFragment(cellType, type, sectionId, articleId)
        val direction = BookmarkFragmentDirections.actionBookmarkFragmentToDetailFragment("Bookmark", "Bookmark", "Bookmark", item.articleId)
        findNavController().navigate(direction)
    }

}