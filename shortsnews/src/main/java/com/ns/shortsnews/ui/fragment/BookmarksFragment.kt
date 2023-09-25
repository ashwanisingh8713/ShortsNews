package com.ns.shortsnews.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.ns.shortsnews.R
import com.ns.shortsnews.adapters.GridAdapter
import com.ns.shortsnews.databinding.FragmentBookmarkBinding
import com.ns.shortsnews.data.repository.UserDataRepositoryImpl
import com.ns.shortsnews.domain.models.Data
import com.ns.shortsnews.domain.usecase.videodata.VideoDataUseCase
import com.ns.shortsnews.ui.viewmodel.BookmarksViewModelFactory
import com.ns.shortsnews.ui.viewmodel.UserBookmarksViewModel
import com.ns.shortsnews.utils.AppPreference
import com.ns.shortsnews.utils.IntentLaunch
import com.rommansabbir.networkx.NetworkXProvider
import com.videopager.utils.CategoryConstants
import com.videopager.utils.NoConnection
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get


class BookmarksFragment : Fragment(R.layout.fragment_bookmark) {
    lateinit var binding:FragmentBookmarkBinding
    lateinit var adapter:GridAdapter

    private val likesViewModel: UserBookmarksViewModel by activityViewModels { BookmarksViewModelFactory().apply {
        inject(VideoDataUseCase(UserDataRepositoryImpl(get())))
    }}


    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentBookmarkBinding.bind(view)


        adapter = GridAdapter(videoFrom = CategoryConstants.BOOKMARK_VIDEO_DATA, channelId = "")
        binding.likesRecyclerview.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            likesViewModel.errorState.filterNotNull().collectLatest {
                Log.i("kamlesh","ProfileFragment onError ::: $it")
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            likesViewModel.videoDataState.filterNotNull().collectLatest {
                Log.i("kamlesh","ProfileFragment onSuccess ::: $it")
                it.let {
                    binding.likesRecyclerview.visibility = View.VISIBLE
                    adapter.updateVideoData(it.data)
                    if (it.data.isEmpty()){
                        binding.noBookmarksText.visibility = View.VISIBLE
                    } else {
                        binding.noBookmarksText.visibility = View.GONE
                    }
                }

            }
        }

        // Item click listener
        viewLifecycleOwner.lifecycleScope.launch {
            adapter.clicks().collectLatest {
                IntentLaunch.launchPlainVideoPlayer(it, requireActivity())
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            likesViewModel.loadingState.filterNotNull().collectLatest {
                if (it) {
                    binding.progressBar.visibility = View.VISIBLE
                } else {
                    binding.progressBar.visibility = View.GONE
                }
            }
        }

    }

    override fun onResume() {
        super.onResume()
        if (NetworkXProvider.isInternetConnected) {
            likesViewModel.requestVideoData(params = Pair(CategoryConstants.BOOKMARK_VIDEO_DATA, ""))
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