package com.ns.shortsnews.ui.fragment

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
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
import com.ns.shortsnews.ui.paging.PassengersAdapter
import com.ns.shortsnews.ui.paging.PassengersLoadStateAdapter
import com.ns.shortsnews.ui.viewmodel.BookmarksViewModelFactory
import com.ns.shortsnews.ui.viewmodel.UserBookmarksViewModel
import com.ns.shortsnews.utils.AppPreference
import com.ns.shortsnews.utils.IntentLaunch
import com.rommansabbir.networkx.NetworkXProvider
import com.videopager.utils.CategoryConstants
import com.videopager.utils.NoConnection
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get


class BookmarksFragment : Fragment(R.layout.fragment_bookmark) {
    lateinit var binding:FragmentBookmarkBinding

    private val bookmarksViewModel: UserBookmarksViewModel by activityViewModels { BookmarksViewModelFactory().apply {
        inject(VideoDataUseCase(UserDataRepositoryImpl(get())))
    }}

    private lateinit var passengersAdapter: PassengersAdapter

    private inner class BookmarkBroadcast: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if(intent != null) {
                val actionType = intent.getStringExtra("actionType")!!
                if(actionType == "LikeEffect") {
                    passengersAdapter.updateLikeStatus(
                        id = intent.getStringExtra("id")!!,
                        liked = intent.getBooleanExtra("liked", false),
                        likeCount = intent.getStringExtra("likeCount")!!
                    )
//                    passengersAdapter.retry()
                } else {
                    passengersAdapter.refresh()
//                    passengersAdapter.retry()
                }
            }

        }
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentBookmarkBinding.bind(view)


        passengersAdapter =
            PassengersAdapter(videoFrom = CategoryConstants.BOOKMARK_VIDEO_DATA, channelId = "")


        passengersAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.ALLOW


        binding.likesRecyclerview.adapter = passengersAdapter.withLoadStateHeaderAndFooter(
            header = PassengersLoadStateAdapter { passengersAdapter.retry() },
            footer = PassengersLoadStateAdapter { passengersAdapter.retry() }
        )



        // Item click listener
        viewLifecycleOwner.lifecycleScope.launch {
            passengersAdapter.clicks().collectLatest {
                IntentLaunch.launchPlainVideoPlayer(it, requireActivity())
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            bookmarksViewModel.bookmarks.collectLatest { pagedData ->

                binding.progressBar.visibility = View.GONE
                binding.likesRecyclerview.visibility = View.VISIBLE
                passengersAdapter.submitData(pagedData)
            }
        }

        // Initializing Broadcast Receiver to listen Bookmark / UN Bookmark
        val broadcastReceiver = BookmarkBroadcast()


        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(broadcastReceiver, IntentFilter("BookmarkFragmentUpdated"));

        passengersAdapter.addLoadStateListener { loadState ->

            when {
                loadState.refresh is LoadState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                loadState.refresh is LoadState.NotLoading -> {
                    binding.progressBar.visibility = View.GONE
                    if (passengersAdapter.itemCount < 1) {
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
                    if (passengersAdapter.itemCount < 1) {
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
            NoConnection.noConnectionSnackBarInfinite(binding.root,
                requireContext() as AppCompatActivity
            )
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        AppPreference.isUpdateNeeded = false
    }
}