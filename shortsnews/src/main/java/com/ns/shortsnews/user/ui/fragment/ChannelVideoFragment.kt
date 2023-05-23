package com.ns.shortsnews.user.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import coil.load
import com.ns.shortsnews.R
import com.ns.shortsnews.adapters.GridAdapter
import com.ns.shortsnews.databinding.FragmentChannelVideosBinding
import com.ns.shortsnews.user.data.repository.UserDataRepositoryImpl
import com.ns.shortsnews.user.domain.usecase.videodata.VideoDataUseCase
import com.ns.shortsnews.user.ui.viewmodel.BookmarksViewModelFactory
import com.ns.shortsnews.user.ui.viewmodel.UserBookmarksViewModel
import com.ns.shortsnews.utils.Alert
import com.ns.shortsnews.utils.AppConstants
import com.videopager.utils.CategoryConstants
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get

class ChannelVideosFragment : Fragment(R.layout.fragment_channel_videos) {

    private lateinit var binding: FragmentChannelVideosBinding
    lateinit var adapter: GridAdapter
    private var channelId = ""
    private var channelTitle = ""
    private var channelUrl = ""

    private val channelsVideosViewModel: UserBookmarksViewModel by activityViewModels { BookmarksViewModelFactory().apply {
        inject(VideoDataUseCase(UserDataRepositoryImpl(get())))
    }}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        channelId = arguments?.getString("channelId").toString()
        channelTitle = arguments?.getString("channelTitle").toString()
        channelUrl = arguments?.getString("channelUrl").toString()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentChannelVideosBinding.bind(view)
        binding.channelLogo.load(channelUrl)
        binding.following.text = "Following"
        channelsVideosViewModel.requestBookmarksApi(Pair(CategoryConstants.CHANNEL_VIDEO_DATA, channelId))
        adapter = GridAdapter(videoFrom = CategoryConstants.CHANNEL_VIDEO_DATA, channelId = channelId)
        listenChannelVideos()
    }


    private fun listenChannelVideos() {
        viewLifecycleOwner.lifecycleScope.launch {
            channelsVideosViewModel.BookmarksSuccessState.collectLatest {
                adapter.updateVideoData(it!!.data)
                binding.channelImageRecyclerview.adapter = adapter
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            channelsVideosViewModel.errorState.collectLatest {
                Alert().showGravityToast(requireActivity(), AppConstants.NO_CHANNEL_DATA)
            }
        }

        // Item click listener
        viewLifecycleOwner.lifecycleScope.launch {
            adapter.clicks().collectLatest {
                val fragment = AppConstants.makeVideoPagerInstance(it.first, CategoryConstants.CHANNEL_VIDEO_DATA, requireActivity())
                val bundle = Bundle()
                bundle.putInt(CategoryConstants.KEY_SelectedPlay, it.second)
                fragment.arguments = bundle
                requireActivity().supportFragmentManager.beginTransaction().replace(R.id.fragment_containerProfile, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        }
    }
}