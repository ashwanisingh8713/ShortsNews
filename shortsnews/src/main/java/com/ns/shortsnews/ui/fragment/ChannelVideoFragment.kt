package com.ns.shortsnews.ui.fragment

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.view.Window
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.paging.LoadState
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.ns.shortsnews.MainActivity
import com.ns.shortsnews.MainApplication
import com.ns.shortsnews.R
import com.ns.shortsnews.databinding.FragmentChannelVideosBinding
import com.ns.shortsnews.data.repository.UserDataRepositoryImpl
import com.ns.shortsnews.domain.usecase.channel.ChannelInfoUseCase
import com.ns.shortsnews.domain.usecase.followunfollow.FollowUnfollowUseCase
import com.ns.shortsnews.ui.paging.ChannelVideoAdapter
import com.ns.shortsnews.ui.viewmodel.*
import com.ns.shortsnews.utils.AppPreference
import com.ns.shortsnews.utils.IntentLaunch
import com.ns.shortsnews.utils.setBottomSheetHeaderBg
import com.videopager.utils.CategoryConstants
import com.videopager.vm.SharedEventViewModelFactory
import com.videopager.vm.VideoSharedEventViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get

class ChannelVideosFragment : Fragment(R.layout.fragment_channel_videos) {

    private lateinit var binding: FragmentChannelVideosBinding
    lateinit var channelVideoAdapter: ChannelVideoAdapter
    private var channelId = ""
    private var channelTitle = ""
    private var channelUrl = ""
    private var channelDes = ""
    private var from = ""
    private var follow_count = ""
    private var following: Boolean? = false

    private val channelInfoViewModel: ChannelInfoViewModel by activityViewModels {
        ChannelInfoViewModelFactory().apply {
            inject(ChannelInfoUseCase(UserDataRepositoryImpl(get())))
        }
    }

    private val followUnfollowViewModel:FollowUnfollowViewModel by activityViewModels {FollowUnfollowViewModelFactory().apply {
        inject(FollowUnfollowUseCase(UserDataRepositoryImpl(get())))
    }  }

    private lateinit var  channelsVideosViewModel2: ChannelVideoViewModel

    private val sharedEventViewModel: VideoSharedEventViewModel by activityViewModels { SharedEventViewModelFactory }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        channelId = arguments?.getString("channelId").toString()
        channelTitle = arguments?.getString("channelTitle").toString()
        channelUrl = arguments?.getString("channelUrl").toString()
        channelDes = arguments?.getString("channelDes").toString()
        from = arguments?.getString("from").toString()
        follow_count = arguments?.getString("follow_count").toString()
        following = arguments?.getBoolean("following")

        val factory = ChannelVideoPagingViewModelFactory(channelId) // Factory
        channelsVideosViewModel2 = ViewModelProvider(this, factory).get(ChannelVideoViewModel::class.java)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentChannelVideosBinding.bind(view)


        // Making Channel Info API call
        channelInfoViewModel.requestChannelInfoApi(channelId)

        if(from == "MainActivity") {
            // Here, we are getting channel info and channel icon bitmap from MainActivity which is already loaded
            /*if(MainActivity.channelVisibleBitmap != null) {
                Log.i("HeaderBg", "ChannelVideoFragment: Channel Icon Bitmap loaded from MainActivity")
                lifecycleScope.launch {
                    binding.channelLogo.setImageBitmap(MainActivity.channelVisibleBitmap)
                    setBottomSheetHeaderBg(MainActivity.channelVisibleBitmap!!, binding.channelTopView)
                }
            } else {
                Log.i("HeaderBg", "ChannelVideoFragment: Channel Icon Bitmap is Null in MainActivity")
                channelLogoBitmap()
            }*/

        } else { // This Else condition is being called from Profile-> Following Channel List Screen


            // Initializing Broadcast Receiver to listen Like / UN Like
            val broadcastReceiver = ChannelVideoBroadcast()
            // Registering broadcast receiver to LocalBroadcastManager
            LocalBroadcastManager.getInstance(requireContext()).registerReceiver(broadcastReceiver, IntentFilter("BookmarkFragmentUpdated"))
        }

        // Loading Channel Icon and Background Color from bitmap
        channelLogoBitmap()

        // Listens Channel Info
        listenChannelInfo()

        // Listens Follow / UnFollow
        listenFollowUnfollow()

        channelVideoAdapter = ChannelVideoAdapter(videoFrom = CategoryConstants.CHANNEL_VIDEO_DATA, channelId = channelId)
        channelVideoAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.ALLOW
        binding.channelImageRecyclerview.adapter = channelVideoAdapter

        binding.following.setOnClickListener {
            followUnfollowViewModel.requestFollowUnfollowApi(channelId)
        }
        binding.channelDes.setOnClickListener {
            descriptionDialog(channelDes)
        }


        channelVideoAdapter.addLoadStateListener { loadState ->

            when {
                loadState.refresh is LoadState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                loadState.refresh is LoadState.NotLoading -> {
                    binding.progressBar.visibility = View.GONE
                    if (channelVideoAdapter.itemCount < 1) {
                        binding.noChannelDataText.visibility = View.VISIBLE
                    } else {
                        binding.noChannelDataText.visibility = View.GONE
                    }
                }

                loadState.refresh is LoadState.Error -> {
                    binding.noChannelDataText.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.GONE
                }

                loadState.append is LoadState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                loadState.append is LoadState.NotLoading -> {
                    binding.progressBar.visibility = View.GONE
                    if (channelVideoAdapter.itemCount < 1) {
                        binding.noChannelDataText.visibility = View.VISIBLE
                    } else {
                        binding.noChannelDataText.visibility = View.GONE
                    }
                }

                loadState.append is LoadState.Error -> {
                    binding.noChannelDataText.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.GONE
                }
            }

        }

        // Item click listener
        viewLifecycleOwner.lifecycleScope.launch {
            channelVideoAdapter.clicks().collectLatest {
                IntentLaunch.launchPlainVideoPlayer(it, requireActivity())
            }
        }


        viewLifecycleOwner.lifecycleScope.launch {
            channelsVideosViewModel2.channelVideoData.collectLatest { pagedData ->
                viewLifecycleOwner.lifecycleScope.launch {
                    binding.channelImageRecyclerview.visibility = View.VISIBLE
                    channelVideoAdapter.submitData(pagedData)
                }
            }
        }

        /*viewLifecycleOwner.lifecycleScope.launch {
            sharedEventViewModel.paletteColor.collectLatest {
                if (it != null) {
                    binding.channelTopView.setBackgroundColor(it)
                }
            }
        }*/



    }

    private fun channelLogoBitmap() {
        if (channelUrl.isNotEmpty()) {
            Glide.with(MainApplication.instance!!)
                .asBitmap()
                .load(channelUrl)
                .listener(object : RequestListener<Bitmap> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Bitmap>,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.i("HeaderBg", "ChannelVideoFragment: Channel Icon Bitmap NOT loaded")
                        return false
                    }

                    override fun onResourceReady(
                        bitmap: Bitmap,
                        model: Any,
                        target: com.bumptech.glide.request.target.Target<Bitmap>?,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        lifecycleScope.launch {
                            binding.channelLogo.setImageBitmap(bitmap)
                            setBottomSheetHeaderBg(bitmap, binding.channelTopView)
                        }

                        return false
                    }
                }).submit()
        }
    }

    private fun listenFollowUnfollow() {
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

    private fun listenChannelInfo() {

        viewLifecycleOwner.lifecycleScope.launch {
            channelInfoViewModel.ChannelInfoSuccessState.filterNotNull().collectLatest {
                binding.channelDes.text = it.data.description
                binding.profileCount.text = it.data.follow_count
                binding.following.visibility = View.VISIBLE
                channelDes = it.data.description

                Log.i("VideoInfoOnSwipe", "ChannelVideosFragment: listenChannelInfo() Des: $channelDes")

                if (it.data.following){
                    binding.following.text = "Following"
                } else {
                    binding.following.text = "Follow"
                }
            }
        }
    }




    private fun descriptionDialog(title: String) {
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

    private inner class ChannelVideoBroadcast: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if(intent != null) {
                val actionType = intent.getStringExtra("actionType")!!
                if(actionType == "LikeEffect") {
                    channelVideoAdapter.updateLikeStatus(
                        id = intent.getStringExtra("id")!!,
                        liked = intent.getBooleanExtra("liked", false),
                        likeCount = intent.getStringExtra("likeCount")!!
                    )
                }
            }

        }
    }
}