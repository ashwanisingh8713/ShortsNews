package com.ns.shortsnews.ui.fragment

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.palette.graphics.Palette
import coil.load
import coil.request.ImageRequest
import com.google.ads.interactivemedia.v3.internal.it
import com.ns.shortsnews.MainApplication
import com.ns.shortsnews.R
import com.ns.shortsnews.adapters.GridAdapter
import com.ns.shortsnews.databinding.FragmentChannelVideosBinding
import com.ns.shortsnews.data.repository.UserDataRepositoryImpl
import com.ns.shortsnews.domain.usecase.videodata.VideoDataUseCase
import com.ns.shortsnews.ui.viewmodel.BookmarksViewModelFactory
import com.ns.shortsnews.ui.viewmodel.UserBookmarksViewModel
import com.ns.shortsnews.utils.Alert
import com.ns.shortsnews.utils.AppConstants
import com.ns.shortsnews.utils.IntentLaunch
import com.videopager.utils.CategoryConstants
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
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
        if (channelUrl.isNotEmpty()){
            val loader = MainApplication.instance!!.newImageLoader()
            val req = ImageRequest.Builder(MainApplication.applicationContext()).data(channelUrl)
                .target { result ->
                    val bitmap = (result as BitmapDrawable).bitmap
                    bottomSheetHeaderBg(bitmap)
                }
                .build()

            loader.enqueue(req)
        }
        binding.channelLogo.load(channelUrl)
        binding.following.text = "Following"
        channelsVideosViewModel.requestVideoData(Pair(CategoryConstants.CHANNEL_VIDEO_DATA, channelId))
        adapter = GridAdapter(videoFrom = CategoryConstants.CHANNEL_VIDEO_DATA, channelId = channelId)
        listenChannelVideos()
    }


    private fun listenChannelVideos() {
        viewLifecycleOwner.lifecycleScope.launch {
            channelsVideosViewModel.BookmarksSuccessState.filterNotNull().collectLatest {
                adapter.updateVideoData(it!!.data)
                binding.channelImageRecyclerview.adapter = adapter
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            channelsVideosViewModel.errorState.filterNotNull().collectLatest {
                Alert().showGravityToast(requireActivity(), AppConstants.NO_CHANNEL_DATA)
            }
        }

        // Item click listener
        viewLifecycleOwner.lifecycleScope.launch {
            adapter.clicks().collectLatest {
                IntentLaunch.launchPlainVideoPlayer(it, requireActivity())
            }
        }
    }

    private fun bottomSheetHeaderBg(bitmap: Bitmap) {
        val mutableBitmap = bitmap.copy(Bitmap.Config.RGBA_F16, true)

        Palette.from(mutableBitmap).generate { palette ->
            val lightVibrantSwatch = palette?.lightVibrantSwatch?.rgb
            lightVibrantSwatch?.let {
               binding.channelTopView.setBackgroundColor(lightVibrantSwatch)
            }

            val vibrantSwatch = palette?.vibrantSwatch?.rgb
            vibrantSwatch?.let {
                binding.channelTopView.setBackgroundColor(vibrantSwatch)
            }

            val lightMutedSwatch = palette?.lightMutedSwatch?.rgb
            lightMutedSwatch?.let {
                binding.channelTopView.setBackgroundColor(lightMutedSwatch)
            }

            val mutedSwatch = palette?.mutedSwatch?.rgb
            mutedSwatch?.let {
                binding.channelTopView.setBackgroundColor(mutedSwatch)
            }

            val darkMutedSwatch = palette?.darkMutedSwatch?.rgb
            darkMutedSwatch?.let {
                binding.channelTopView.setBackgroundColor(darkMutedSwatch)
            }

            val darkVibrantSwatch = palette?.darkVibrantSwatch?.rgb
            darkVibrantSwatch?.let {
                binding.channelTopView.setBackgroundColor(darkVibrantSwatch)
            }

        }
    }
}