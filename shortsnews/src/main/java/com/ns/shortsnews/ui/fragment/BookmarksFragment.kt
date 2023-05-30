package com.ns.shortsnews.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.ns.shortsnews.R
import com.ns.shortsnews.adapters.GridAdapter
import com.ns.shortsnews.databinding.FragmentBookmarkBinding
import com.ns.shortsnews.data.repository.UserDataRepositoryImpl
import com.ns.shortsnews.domain.usecase.videodata.VideoDataUseCase
import com.ns.shortsnews.ui.viewmodel.BookmarksViewModelFactory
import com.ns.shortsnews.ui.viewmodel.UserBookmarksViewModel
import com.ns.shortsnews.utils.IntentLaunch
import com.videopager.utils.CategoryConstants
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentBookmarkBinding.bind(view)
        likesViewModel.requestVideoData(params = Pair(CategoryConstants.BOOKMARK_VIDEO_DATA, ""))
        adapter = GridAdapter(videoFrom = CategoryConstants.BOOKMARK_VIDEO_DATA, channelId = "")

        viewLifecycleOwner.lifecycleScope.launch {
            likesViewModel.errorState.filterNotNull().collectLatest {
                binding.progressBar.visibility = View.GONE
                if(it != "NA"){
                    Log.i("kamlesh","ProfileFragment onError ::: $it")
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            likesViewModel.BookmarksSuccessState.filterNotNull().collectLatest {
                Log.i("kamlesh","ProfileFragment onSuccess ::: $it")
                it.let {
                    binding.progressBar.visibility = View.GONE
                    adapter.updateVideoData(it.data)
                    binding.likesRecyclerview.adapter = adapter
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
                }
            }
        }
    }
}