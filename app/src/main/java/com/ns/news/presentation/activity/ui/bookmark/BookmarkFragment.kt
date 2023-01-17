package com.ns.news.presentation.activity.ui.bookmark

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ns.news.data.db.Bookmark
import com.ns.news.data.db.NewsDb
import com.ns.news.databinding.FragmentBookmarkBinding
import com.ns.news.presentation.activity.NewsSharedViewModel
import com.ns.news.presentation.activity.NewsSharedViewModelFactory
import com.ns.news.presentation.activity.SharedChannelEvent
import com.ns.news.presentation.adapter.BookmarkAdapter
import kotlinx.coroutines.launch


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

        loadBookmarkData()
    }

    override fun onResume() {
        super.onResume()
        newsShareViewModel.disableDrawer()

        loadBookmarkData()

        observeBookmarkRefreshData();

    }

    private fun observeBookmarkRefreshData() {
        lifecycleScope.launch{
            newsShareViewModel.sharedChannelEvent.collect{
                if(it == SharedChannelEvent.REFRESH_BOOKMARK_LIST) {
                    loadBookmarkData()
                }
            }
        }
    }

    private fun loadBookmarkData() {
        lifecycleScope.launch {
            viewModel?.getAllBookmarks()?.collect{
                binding.recyclerView.adapter = BookmarkAdapter(it.reversed(), this@BookmarkFragment)
                if (it.isEmpty()) {
                    binding.recyclerView.visibility = View.GONE
                    binding.viewEmpty.visibility = View.VISIBLE
                } else {
                    binding.recyclerView.visibility = View.VISIBLE
                    binding.viewEmpty.visibility = View.GONE
                }
            }
        }
    }



    override fun onPause() {
        super.onPause()
        newsShareViewModel.enableDrawer()
    }

    override fun onBookmarkClick(item: Bookmark) {
        val direction = BookmarkFragmentDirections.actionBookmarkFragmentToDetailFragment("Bookmark", "Bookmark", "Bookmark", item.articleId)
        findNavController().navigate(direction)
    }

}