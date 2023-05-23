package com.ns.shortsnews

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
import android.view.WindowManager
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.load
import coil.request.ImageRequest
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.ns.shortsnews.adapters.CategoryAdapter
import com.ns.shortsnews.adapters.GridAdapter
import com.ns.shortsnews.cache.VideoPreloadCoroutine
import com.ns.shortsnews.databinding.ActivityMainBinding
import com.ns.shortsnews.user.data.repository.UserDataRepositoryImpl
import com.ns.shortsnews.user.data.repository.VideoCategoryRepositoryImp
import com.ns.shortsnews.user.domain.usecase.videodata.VideoDataUseCase
import com.ns.shortsnews.user.domain.usecase.video_category.VideoCategoryUseCase
import com.ns.shortsnews.user.ui.viewmodel.BookmarksViewModelFactory
import com.ns.shortsnews.user.ui.viewmodel.UserBookmarksViewModel
import com.ns.shortsnews.user.ui.viewmodel.VideoCategoryViewModel
import com.ns.shortsnews.user.ui.viewmodel.VideoCategoryViewModelFactory
import com.ns.shortsnews.utils.AppConstants
import com.ns.shortsnews.utils.AppPreference
import com.videopager.utils.CategoryConstants
import com.videopager.vm.SharedEventViewModel
import com.videopager.vm.SharedEventViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get


class MainActivity : AppCompatActivity(), onProfileItemClick {
    private lateinit var binding: ActivityMainBinding
    private lateinit var caAdapter: CategoryAdapter
    private val sharedEventViewModel: SharedEventViewModel by viewModels { SharedEventViewModelFactory }
    private val videoCategoryViewModel: VideoCategoryViewModel by viewModels { VideoCategoryViewModelFactory().apply {
        inject(
            VideoCategoryUseCase(VideoCategoryRepositoryImp(get()))
        )
    } }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        val view = binding.root

        setContentView(view)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.black)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        if (AppPreference.isUserLoggedIn) {
            binding.profileIcon.load(AppPreference.userProfilePic)
        }

        binding.profileIcon.setOnClickListener {
            launchProfileIntent()
        }
        videoCategoryViewModel.loadVideoCategory()
        showCategory()
        // To get status of should launch in login flow for non logged-in user
        launchLoginStateFlow()

        // Bottom Sheet
        bottomSheetDialog()

        // Register GetInfo Listener
        registerGetInfo()

        followingClick()
    }

    override fun onResume() {
        super.onResume()
        statusBarFullScreen()
        if (!AppPreference.isUserLoggedIn) {
            sharedEventViewModel.sendUserPreferenceData(false,"")
        } else {
            sharedEventViewModel.sendUserPreferenceData(AppPreference.isUserLoggedIn,AppPreference.userToken!!)
        }
    }




    // It is having Bottom Sheet Implementation
    private fun bottomSheetDialog() {
        val standardBottomSheetBehavior = BottomSheetBehavior.from(binding.persistentBottomsheet.bottomSheet)
        standardBottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                Log.i("", "$slideOffset")
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when(newState) {
                    BottomSheetBehavior.STATE_EXPANDED-> binding.persistentBottomsheet.imgDownArrow.setImageDrawable(resources.getDrawable(R.drawable.arrow_bottom_button, null))
                    BottomSheetBehavior.STATE_COLLAPSED -> binding.persistentBottomsheet.imgDownArrow.setImageDrawable(resources.getDrawable(R.drawable.arrow_up_button, null))
                    BottomSheetBehavior.STATE_DRAGGING -> standardBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
                }
            }
        })

        binding.persistentBottomsheet.imgDownArrow.setOnClickListener{
            if(standardBottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                standardBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            } else {
                binding.persistentBottomsheet.progressBar.visibility = View.VISIBLE
                binding.persistentBottomsheet.imgDownArrow.visibility = View.GONE
                getChannelVideoData(channelId = binding.persistentBottomsheet.imgDownArrow.tag.toString())
            }

        }
    }

    /**
     * It listens category item click
     */
    override fun itemclick(shortsType: String, position:Int, size:Int) {
        loadHomeFragment(shortsType)
    }

    private fun launchProfileIntent(){
        val intent = Intent(this, ProfileActivity::class.java)
        intent.putExtra("fromActivity","MainActivity")
        startActivity(intent)
    }


    /**
     * Loads Home Fragment
     */
    private fun loadHomeFragment(categoryType: String) {
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_container,
            AppConstants.makeVideoPagerInstance(categoryType, CategoryConstants.DEFAULT_VIDEO_DATA, this@MainActivity))
        ft.commit()
        AppPreference.userToken?.let {
            sharedEventViewModel.sendUserPreferenceData(
                AppPreference.isUserLoggedIn, it
            )
        }
        registerVideoCache()
    }

    private fun launchLoginStateFlow() {
        lifecycleScope.launch {
            sharedEventViewModel.getLoginEventStatus.collectLatest {
                launchProfileIntent()
            }
        }
    }



    private fun showCategory() {
        lifecycleScope.launch {
            videoCategoryViewModel.videoCategorySuccessState.filterNotNull().collectLatest {
                // Setup recyclerView
                caAdapter = CategoryAdapter(
                    itemList = it.videoCategories,
                    itemListener = this@MainActivity
                )
                binding.recyclerView.adapter = caAdapter
                val defaultCate = it.videoCategories[0]
                loadHomeFragment(defaultCate.id)
            }
        }
    }


    // Register Hot-Flow to listen Video Caching
    private fun registerVideoCache() {
        lifecycleScope.launch {
            sharedEventViewModel.cacheVideoUrl.filterNotNull().collectLatest {
                val url = it.first
                val id = it.second
                var videoUrls = Array(1){url}
                var videoIds = Array(1){id}
                VideoPreloadCoroutine.schedulePreloadWork(videoUrls=videoUrls, ids=videoIds)
            }
        }

        lifecycleScope.launch {
            sharedEventViewModel.cacheVideoUrl_2.filterNotNull().collectLatest {
                val url = it.first
                val id = it.second
                var videoUrls = Array(1){url}
                var videoIds = Array(1){id}
                VideoPreloadCoroutine.schedulePreloadWork(videoUrls=videoUrls, ids=videoIds)
            }
        }
    }

    private fun registerGetInfo() {
        lifecycleScope.launch {
            sharedEventViewModel.videoInfo.filterNotNull().collectLatest {
                if (it.following) {
                    binding.persistentBottomsheet.following.text = "Following"
                } else {
                    binding.persistentBottomsheet.following.text = "Follow"
                }
                binding.persistentBottomsheet.following.tag = it.channel_id
                binding.persistentBottomsheet.imgDownArrow.tag = it.channel_id
                binding.persistentBottomsheet.following.visibility = View.VISIBLE

                if (it.channel_image.isNotEmpty()) {
                    binding.persistentBottomsheet.clientImage.loadSvg(
                        it.channel_image,
                        binding.persistentBottomsheet.clientImage.context
                    )
                    binding.persistentBottomsheet.cardViewClientImage.visibility = View.VISIBLE
                } else {
                    binding.persistentBottomsheet.cardViewClientImage.visibility = View.INVISIBLE
                }
            }
        }

        lifecycleScope.launch {
            sharedEventViewModel.followResponse.filterNotNull().collectLatest {
                if (it.following) {
                    binding.persistentBottomsheet.following.text = "Following"
                } else{
                    binding.persistentBottomsheet.following.text = "Follow"
                }

            }
        }
    }

    private val likesViewModel: UserBookmarksViewModel by viewModels { BookmarksViewModelFactory().apply {
        inject(VideoDataUseCase(UserDataRepositoryImpl(get())))
    }}

    // Get Channel Video Data
    private fun getChannelVideoData(channelId: String) {

        likesViewModel.requestBookmarksApi(params = Pair(CategoryConstants.CHANNEL_VIDEO_DATA, channelId))
        val adapter = GridAdapter(videoFrom = CategoryConstants.CHANNEL_VIDEO_DATA)

        lifecycleScope.launch {
            likesViewModel.errorState.filterNotNull().collectLatest {
                binding.persistentBottomsheet.progressBar.visibility = View.GONE
                binding.persistentBottomsheet.imgDownArrow.visibility = View.VISIBLE
            }
        }

        lifecycleScope.launch {
            likesViewModel.BookmarksSuccessState.filterNotNull().collectLatest {
                delay(500) // To show the progress bar properly
                binding.persistentBottomsheet.progressBar.visibility = View.GONE
                binding.persistentBottomsheet.imgDownArrow.visibility = View.VISIBLE
                it.let {
                    adapter.updateVideoData(it.data)
                    binding.persistentBottomsheet.channelRecyclerview.adapter = adapter
                    val standardBottomSheetBehavior = BottomSheetBehavior.from(binding.persistentBottomsheet.bottomSheet)
                    if(standardBottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                        standardBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    } else {
                        standardBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                    }
                }
            }
        }
    }


    private fun followingClick() {
        binding.persistentBottomsheet.following.setOnClickListener{
            val channelId = binding.persistentBottomsheet.following.tag
            if(channelId != null) {
                sharedEventViewModel.followRequest(channelId.toString())
            }
        }
    }


    // To set status bar in Full-Screen
    private fun statusBarFullScreen() {
        if (Build.VERSION.SDK_INT in 21..29) {
            window.statusBarColor = Color.TRANSPARENT
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.decorView.systemUiVisibility =
                SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or SYSTEM_UI_FLAG_LAYOUT_STABLE

        } else if (Build.VERSION.SDK_INT >= 30) {
            window.statusBarColor = Color.TRANSPARENT
            // Making status bar overlaps with the activity
            WindowCompat.setDecorFitsSystemWindows(window, false)
        }
    }


    // SVG Image Caching
    private fun ImageView.loadSvg(url: String, context: Context) {
        val imageLoader = ImageLoader.Builder(context)
            .components {
                add(SvgDecoder.Factory())
            }
            .build()

        val request = ImageRequest.Builder(this.context)
            .data(url)
            .target(this)
            .placeholder(com.videopager.R.drawable.channel_placeholder)
            .build()
        imageLoader.enqueue(request)
    }

}