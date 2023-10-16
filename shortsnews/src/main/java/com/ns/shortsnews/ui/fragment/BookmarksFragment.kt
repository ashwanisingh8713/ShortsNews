package com.ns.shortsnews.ui.fragment

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.ns.shortsnews.R
import com.ns.shortsnews.data.repository.UserDataRepositoryImpl
import com.ns.shortsnews.databinding.FragmentBookmarkBinding
import com.ns.shortsnews.domain.usecase.videodata.VideoDataUseCase
import com.ns.shortsnews.ui.paging.BookmarksAdapter
import com.ns.shortsnews.ui.viewmodel.BookmarksViewModelFactory
import com.ns.shortsnews.ui.viewmodel.UserBookmarksViewModel
import com.ns.shortsnews.utils.AppPreference
import com.ns.shortsnews.utils.IntentLaunch
import com.rommansabbir.networkx.NetworkXProvider
import com.videopager.utils.CategoryConstants
import com.videopager.utils.NoConnection
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get


class BookmarksFragment : Fragment(R.layout.fragment_bookmark) {
    lateinit var binding:FragmentBookmarkBinding

    private val bookmarksViewModel: UserBookmarksViewModel by activityViewModels { BookmarksViewModelFactory().apply {
        inject(VideoDataUseCase(UserDataRepositoryImpl(get())))
    }}

    private lateinit var bookmarksAdapter: BookmarksAdapter

    var position = 0

    private inner class BookmarkBroadcast: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if(intent != null) {
                val actionType = intent.getStringExtra("actionType")!!
                Log.i("AshwaniXYZ", "BookmarkBroadcast, onReceive")
                if(actionType == "LikeEffect") {
                    bookmarksAdapter.updateLikeStatus(
                        id = intent.getStringExtra("id")!!,
                        liked = intent.getBooleanExtra("liked", false),
                        likeCount = intent.getStringExtra("likeCount")!!
                    )
                } else {
                    bookmarksAdapter.refresh()
                }
            }

        }
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentBookmarkBinding.bind(view)


        bookmarksAdapter =
            BookmarksAdapter(videoFrom = CategoryConstants.BOOKMARK_VIDEO_DATA, channelId = "")


        binding.likesRecyclerview.adapter = bookmarksAdapter
        bookmarksAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.ALLOW

        binding.likesRecyclerview.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val offset: Int = binding.likesRecyclerview.computeHorizontalScrollOffset()
                if (offset % binding.likesRecyclerview.width == 0) {
                    position = offset / binding.likesRecyclerview.width

                }
            }
        })

        // Item click listener
        viewLifecycleOwner.lifecycleScope.launch {
            bookmarksAdapter.clicks().collectLatest {
                IntentLaunch.launchPlainVideoPlayer(it, requireActivity())
            }
        }

        // Consumer to listen bookmarks
        viewLifecycleOwner.lifecycleScope.launch {
            bookmarksViewModel.bookmarks.collectLatest { pagedData ->
                binding.progressBar.visibility = View.GONE
                binding.likesRecyclerview.visibility = View.VISIBLE
                bookmarksAdapter.submitData(pagedData)
            }
        }

        // Initializing Broadcast Receiver to listen Bookmark/Like / UN Bookmark/ UN Like
        val broadcastReceiver = BookmarkBroadcast()
        // Registering broadcast receiver to LocalBroadcastManager
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(broadcastReceiver, IntentFilter("BookmarkFragmentUpdated"));

        bookmarksAdapter.addLoadStateListener { loadState ->

            when {
                loadState.refresh is LoadState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                loadState.refresh is LoadState.NotLoading -> {
                    binding.progressBar.visibility = View.GONE
                    if (bookmarksAdapter.itemCount < 1) {
                        binding.noBookmarksText.visibility = View.VISIBLE
                    } else {
                        binding.noBookmarksText.visibility = View.GONE
                    }
                }

                loadState.refresh is LoadState.Error -> {
                    binding.noBookmarksText.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.GONE
                }

                loadState.append is LoadState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                loadState.append is LoadState.NotLoading -> {
                    binding.progressBar.visibility = View.GONE
                    if (bookmarksAdapter.itemCount < 1) {
                        binding.noBookmarksText.visibility = View.VISIBLE
                    } else {
                        binding.noBookmarksText.visibility = View.GONE
                    }
                }

                loadState.append is LoadState.Error -> {
                    binding.noBookmarksText.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.GONE
                }
            }

            }

    }


    override fun onResume() {
        super.onResume()
        if (NetworkXProvider.isInternetConnected) {

        } else {
            // No Internet Snack bar: Fire
//            NoConnection.noConnectionSnackBarInfinite(binding.root,
//                requireContext() as AppCompatActivity
//            )
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        AppPreference.isUpdateNeeded = false
    }
}