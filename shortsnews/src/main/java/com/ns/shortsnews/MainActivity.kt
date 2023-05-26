package com.ns.shortsnews

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.palette.graphics.Palette
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
import com.ns.shortsnews.utils.IntentLaunch
import com.videopager.utils.CategoryConstants
import com.videopager.vm.VideoSharedEventViewModel
import com.videopager.vm.SharedEventViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get


class MainActivity : AppCompatActivity(), onProfileItemClick {
    private lateinit var binding: ActivityMainBinding
    private lateinit var caAdapter: CategoryAdapter
    private val sharedEventViewModel: VideoSharedEventViewModel by viewModels { SharedEventViewModelFactory }
    private val videoCategoryViewModel: VideoCategoryViewModel by viewModels {
        VideoCategoryViewModelFactory().apply {
            inject(
                VideoCategoryUseCase(VideoCategoryRepositoryImp(get()))
            )
        }
    }
    private val videoDataViewModel: UserBookmarksViewModel by viewModels {
        BookmarksViewModelFactory().apply {
            inject(VideoDataUseCase(UserDataRepositoryImpl(get())))
        }
    }

    lateinit var standardBottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        standardBottomSheetBehavior =
            BottomSheetBehavior.from(binding.persistentBottomsheet.bottomSheet)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        val view = binding.root

        setContentView(view)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.black)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        if (AppPreference.isUserLoggedIn) {
            if (AppPreference.userProfilePic == ""){
                binding.profileIcon.setImageResource(R.drawable.profile_avatar)
            } else {
                binding.profileIcon.load(AppPreference.userProfilePic)
            }

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
        bottomSheetGetInfoListener()

        bottomSheetFollowingClick()

        registerVideoCache()
    }

    override fun onResume() {
        super.onResume()
        sharedEventViewModel.sendUserPreferenceData(
            AppPreference.isUserLoggedIn,
            AppPreference.userToken
        )
    }


    /**
     * It listens category item click
     */
    override fun itemclick(requiredId: String, position: Int, size: Int) {
        bottomSheetClearChannelId()
        loadHomeFragment(requiredId)
        sharedEventViewModel.sendUserPreferenceData(
            AppPreference.isUserLoggedIn,
            AppPreference.userToken
        )
    }

    private fun launchProfileIntent() {
        val intent = Intent(this, ProfileActivity::class.java)
        intent.putExtra("fromActivity", "MainActivity")
        startActivity(intent)
    }


    /**
     * Loads Home Fragment
     */
    private fun loadHomeFragment(categoryType: String) {
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(
            R.id.fragment_container,
            AppConstants.makeVideoPagerInstance(
                categoryType,
                CategoryConstants.DEFAULT_VIDEO_DATA,
                this@MainActivity
            )
        )
        ft.commit()

    }

    // It gets callback from VideoPagerFragment
    // When User clicks on Follow, Like, Bookmark then it checks
    // If User is not logged in then it is called
    private fun launchLoginStateFlow() {
        lifecycleScope.launch {
            sharedEventViewModel.getLoginEventStatus.collectLatest {
                launchProfileIntent()
            }
        }
    }


    private fun showCategory() {
        lifecycleScope.launch {
            videoCategoryViewModel.videoCategorySuccessState.filterNotNull().filter {
                it.videoCategories.isNotEmpty()
            }.collectLatest {
                // Setup recyclerView
                caAdapter = CategoryAdapter(
                    itemList = it.videoCategories,
                    itemListener = this@MainActivity
                )
                binding.recyclerView.adapter = caAdapter
                val defaultCate = it.videoCategories[0]
                loadHomeFragment(defaultCate.id)
                hideTryAgainText()
            }
        }

        lifecycleScope.launch {
            videoCategoryViewModel.errorState.filterNotNull().collectLatest {
                showTryAgainText()
            }
        }

    }

    private fun hideTryAgainText() {
        binding.tryAgain.visibility = View.GONE
    }

    private fun showTryAgainText() {
            binding.tryAgain.visibility = View.VISIBLE
            binding.tryAgain.setOnClickListener {
                videoCategoryViewModel.loadVideoCategory()
            }
    }



    // Register Hot-Flow to listen Video Caching
    private fun registerVideoCache() {
        lifecycleScope.launch {
            sharedEventViewModel.cacheVideoUrl.filterNotNull().collectLatest {
                val url = it.first
                val id = it.second
                var videoUrls = Array(1) { url }
                var videoIds = Array(1) { id }
                VideoPreloadCoroutine.schedulePreloadWork(videoUrls = videoUrls, ids = videoIds)
            }
        }

        lifecycleScope.launch {
            sharedEventViewModel.cacheVideoUrl_2.filterNotNull().collectLatest {
                val url = it.first
                val id = it.second
                var videoUrls = Array(1) { url }
                var videoIds = Array(1) { id }
                VideoPreloadCoroutine.schedulePreloadWork(videoUrls = videoUrls, ids = videoIds)
            }
        }
    }

    // It is having Bottom Sheet Implementation
    private fun bottomSheetDialog() {
        standardBottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                Log.i("", "$slideOffset")
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> binding.persistentBottomsheet.imgDownArrow.setImageDrawable(
                        resources.getDrawable(R.drawable.slide_down_arrow_icon, null)
                    )
                    BottomSheetBehavior.STATE_COLLAPSED -> binding.persistentBottomsheet.imgDownArrow.setImageDrawable(
                        resources.getDrawable(R.drawable.slide_up_arrow_icon, null)
                    )
                    BottomSheetBehavior.STATE_DRAGGING -> standardBottomSheetBehavior.setState(
                        BottomSheetBehavior.STATE_COLLAPSED
                    )
                }
            }
        })

        binding.persistentBottomsheet.imgDownArrow.setOnClickListener {
            if (standardBottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                standardBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            } else {
                val channelId = binding.persistentBottomsheet.imgDownArrow.tag?.toString()
                channelId?.let {
                    binding.persistentBottomsheet.progressBar.visibility = View.VISIBLE
                    binding.persistentBottomsheet.imgDownArrow.visibility = View.GONE
                    bottomSheetGetChannelVideoData(channelId = channelId)
                }
            }
        }
    }

    // Bottom Sheet Video Get Info Listen & Update
    private fun bottomSheetGetInfoListener() {
        lifecycleScope.launch {
            sharedEventViewModel.videoInfo.filterNotNull().collectLatest {

                if (it.following) {
                    binding.persistentBottomsheet.following.text = "Following"
                } else {
                    binding.persistentBottomsheet.following.text = "Follow"
                }

                val channelId = binding.persistentBottomsheet.following.tag

//                if(channelId != it.channel_id) {
                binding.persistentBottomsheet.following.tag = it.channel_id
                binding.persistentBottomsheet.imgDownArrow.tag = it.channel_id

                if (it.channel_image.isNotEmpty()) {
                    val loader = MainApplication.instance!!.newImageLoader()
                    val req = ImageRequest.Builder(MainApplication.applicationContext())
                        .data(it.channel_image) // demo link
                        .target { result ->
                            val bitmap = (result as BitmapDrawable).bitmap
                            binding.persistentBottomsheet.clientImage.setImageBitmap(bitmap)
                            bottomSheetHeaderBg(bitmap)
                        }
                        .build()

                    loader.enqueue(req)
                    /* binding.persistentBottomsheet.clientImage.load(
                         it.channel_image
                     )*/
                    binding.persistentBottomsheet.cardViewClientImage.visibility = View.VISIBLE
                } else {
                    binding.persistentBottomsheet.cardViewClientImage.visibility =
                        View.INVISIBLE
                }
//                }
            }
        }

        lifecycleScope.launch {
            sharedEventViewModel.followResponse.filterNotNull().collectLatest {
                if (it.following) {
                    binding.persistentBottomsheet.following.text = "Following"
                } else {
                    binding.persistentBottomsheet.following.text = "Follow"
                }

            }
        }
    }


    // Get Channel Video Data
    private fun bottomSheetGetChannelVideoData(channelId: String) {
        videoDataViewModel.requestVideoData(
            params = Pair(
                CategoryConstants.CHANNEL_VIDEO_DATA,
                channelId
            )
        )
        val adapter =
            GridAdapter(videoFrom = CategoryConstants.CHANNEL_VIDEO_DATA, channelId = channelId)

        lifecycleScope.launch {
            videoDataViewModel.errorState.filterNotNull().collectLatest {
                binding.persistentBottomsheet.progressBar.visibility = View.GONE
                binding.persistentBottomsheet.imgDownArrow.visibility = View.VISIBLE
            }
        }

        lifecycleScope.launch {
            videoDataViewModel.BookmarksSuccessState.filterNotNull().collectLatest {
                delay(500) // To show the progress bar properly
                binding.persistentBottomsheet.progressBar.visibility = View.GONE
                binding.persistentBottomsheet.imgDownArrow.visibility = View.VISIBLE
                it.let {
                    adapter.updateVideoData(it.data)

                    binding.persistentBottomsheet.channelRecyclerview.adapter = adapter

                    if (standardBottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                        standardBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    } else {
                        standardBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                    }
                }
            }
        }

        // Channel Item click listener
        lifecycleScope.launch {
            adapter.clicks().collectLatest {
                IntentLaunch.launchPlainVideoPlayer(it, this@MainActivity)
                standardBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }

    }

    private fun bottomSheetClearChannelId() {
        binding.persistentBottomsheet.following.tag = null
        binding.persistentBottomsheet.imgDownArrow.tag = null
    }

    // Bottom Sheet Following Click Listener
    private fun bottomSheetFollowingClick() {
        binding.persistentBottomsheet.following.setOnClickListener {
            val channelId = binding.persistentBottomsheet.following.tag
            channelId?.let {
                sharedEventViewModel.followRequest(channelId.toString())
            }
        }
    }


    private fun bottomSheetHeaderBg(bitmap: Bitmap) {
        val mutableBitmap = bitmap.copy(Bitmap.Config.RGBA_F16, true)
        
        Palette.from(mutableBitmap).generate { palette ->
            val lightVibrantSwatch = palette?.lightVibrantSwatch?.rgb
            lightVibrantSwatch?.let {
                binding.persistentBottomsheet.bottomSheetHeader.setBackgroundColor(lightVibrantSwatch)
                binding.persistentBottomsheet.channelTopView.setBackgroundColor(lightVibrantSwatch)
            }

            val vibrantSwatch = palette?.vibrantSwatch?.rgb
            vibrantSwatch?.let { binding.persistentBottomsheet.bottomSheetHeader.setBackgroundColor(vibrantSwatch)
                binding.persistentBottomsheet.channelTopView.setBackgroundColor(vibrantSwatch)
            }

            val lightMutedSwatch = palette?.lightMutedSwatch?.rgb
            lightMutedSwatch?.let {
                binding.persistentBottomsheet.bottomSheetHeader.setBackgroundColor(lightMutedSwatch)
                binding.persistentBottomsheet.channelTopView.setBackgroundColor(lightMutedSwatch)
            }

            val mutedSwatch = palette?.mutedSwatch?.rgb
            mutedSwatch?.let {
                binding.persistentBottomsheet.bottomSheetHeader.setBackgroundColor(mutedSwatch)
                binding.persistentBottomsheet.channelTopView.setBackgroundColor(mutedSwatch)
            }

            val darkMutedSwatch = palette?.darkMutedSwatch?.rgb
            darkMutedSwatch?.let {
                binding.persistentBottomsheet.bottomSheetHeader.setBackgroundColor(darkMutedSwatch)
                binding.persistentBottomsheet.channelTopView.setBackgroundColor(darkMutedSwatch)
            }

            val darkVibrantSwatch = palette?.darkVibrantSwatch?.rgb
            darkVibrantSwatch?.let {
                binding.persistentBottomsheet.bottomSheetHeader.setBackgroundColor(
                    darkVibrantSwatch
                )
                binding.persistentBottomsheet.channelTopView.setBackgroundColor(darkVibrantSwatch)
            }

        }
    }


}