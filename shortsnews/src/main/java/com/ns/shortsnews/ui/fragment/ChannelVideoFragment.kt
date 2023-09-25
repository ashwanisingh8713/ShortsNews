package com.ns.shortsnews.ui.fragment

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
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
import com.ns.shortsnews.domain.usecase.channel.ChannelInfoUseCase
import com.ns.shortsnews.domain.usecase.followunfollow.FollowUnfollowUseCase
import com.ns.shortsnews.domain.usecase.videodata.VideoDataUseCase
import com.ns.shortsnews.ui.viewmodel.*
import com.ns.shortsnews.utils.AppPreference
import com.ns.shortsnews.utils.IntentLaunch
import com.videopager.utils.CategoryConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.get

class ChannelVideosFragment : Fragment(R.layout.fragment_channel_videos) {

    private lateinit var binding: FragmentChannelVideosBinding
    lateinit var adapter: GridAdapter
    private var channelId = ""
    private var channelTitle = ""
    private var channelUrl = ""
    private var channelDes = ""
    private val channelInfoViewModel: ChannelInfoViewModel by activityViewModels {
        ChannelInfoViewModelFactory().apply {
            inject(ChannelInfoUseCase(UserDataRepositoryImpl(get())))
        }
    }

    private val channelsVideosViewModel: ChannelVideoViewModel by activityViewModels { ChannelVideoViewModelFactory().apply {
        inject(VideoDataUseCase(UserDataRepositoryImpl(get())))
    }}
    private val followUnfollowViewModel:FollowUnfollowViewModel by activityViewModels {FollowUnfollowViewModelFactory().apply {
        inject(FollowUnfollowUseCase(UserDataRepositoryImpl(get())))
    }  }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        channelId = arguments?.getString("channelId").toString()
        channelTitle = arguments?.getString("channelTitle").toString()
        channelUrl = arguments?.getString("channelUrl").toString()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentChannelVideosBinding.bind(view)
        listenChannelInfo()
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

        adapter = GridAdapter(videoFrom = CategoryConstants.CHANNEL_VIDEO_DATA, channelId = channelId)
        listenChannelVideos()
        binding.following.setOnClickListener {
            listenFollowUnfollow(channelId)
        }
        binding.channelDes.setOnClickListener {
            showDialog(channelDes)
        }
    }

    override fun onResume() {
        super.onResume()
        channelsVideosViewModel.requestChannelVideoData(Pair(CategoryConstants.CHANNEL_VIDEO_DATA, channelId))
    }

    override fun onDetach() {
        super.onDetach()
        channelsVideosViewModel.clearChannelVideos()
        channelInfoViewModel.clearChannelInfo()

    }

    private fun listenFollowUnfollow(channelId:String) {
        followUnfollowViewModel.requestFollowUnfollowApi(channelId)
        viewLifecycleOwner.lifecycleScope.launch {
            followUnfollowViewModel.FollowUnfollowSuccessState.filterNotNull().collectLatest {
                AppPreference.isFollowingUpdateNeeded = true
                binding.profileCount.text = it.data.follow_count
                if (it.data.following) {
                    binding.following.text = "Following"
                } else {
                    binding.following.text = "Follow"
                }
            }
        }
    }

    private fun listenChannelInfo(){
        channelInfoViewModel.requestChannelInfoApi(channelId)
        viewLifecycleOwner.lifecycleScope.launch {
            channelInfoViewModel.ChannelInfoSuccessState.filterNotNull().collectLatest {
                binding.channelDes.text = it.data.description
                binding.profileCount.text = it.data.follow_count
                binding.following.visibility = View.VISIBLE
                channelDes = it.data.description
                if (it.data.following){
                    binding.following.text = "Following"
                } else {
                    binding.following.text = "Follow"
                }
            }
        }
    }



    private fun listenChannelVideos() {
        viewLifecycleOwner.lifecycleScope.launch {
            channelsVideosViewModel.BookmarksSuccessState.filterNotNull().collectLatest {
                it.let {
                    binding.noChannelDataText.visibility = View.GONE
                    binding.channelImageRecyclerview.visibility = View.VISIBLE
                    adapter.updateVideoData(it!!.data)
                    binding.channelImageRecyclerview.adapter = adapter
                }

            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            channelsVideosViewModel.loadingState.filterNotNull().collectLatest {
                if (it) {
                    binding.progressBar.visibility = View.VISIBLE
                } else {
                    binding.progressBar.visibility = View.GONE
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            channelsVideosViewModel.errorState.filterNotNull().collectLatest {
                binding.noChannelDataText.visibility = View.VISIBLE
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

    private fun showDialog(title: String) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.description_alert_item)
        val body = dialog.findViewById(R.id.alert_des) as TextView
        body.text = title
        val yesBtn = dialog.findViewById(R.id.const_close_button) as ConstraintLayout
        yesBtn.setOnClickListener {
            // delete data to DataStore
            dialog.dismiss()
        }
        dialog.show()

    }
}