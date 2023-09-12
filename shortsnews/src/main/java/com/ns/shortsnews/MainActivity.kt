package com.ns.shortsnews

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.palette.graphics.Palette
import coil.load
import coil.request.ImageRequest
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.ns.shortsnews.adapters.CategoryAdapter
import com.ns.shortsnews.adapters.GridAdapter
import com.ns.shortsnews.cache.HlsOneByOnePreloadCoroutine
import com.ns.shortsnews.cache.VideoPreloadCoroutine
import com.ns.shortsnews.data.repository.UserDataRepositoryImpl
import com.ns.shortsnews.data.repository.VideoCategoryRepositoryImp
import com.ns.shortsnews.databinding.ActivityMainBinding
import com.ns.shortsnews.domain.models.Data
import com.ns.shortsnews.domain.usecase.channel.ChannelInfoUseCase
import com.ns.shortsnews.domain.usecase.followunfollow.FollowUnfollowUseCase
import com.ns.shortsnews.domain.usecase.video_category.VideoCategoryUseCase
import com.ns.shortsnews.domain.usecase.videodata.VideoDataUseCase
import com.ns.shortsnews.ui.viewmodel.*
import com.ns.shortsnews.utils.AppConstants
import com.ns.shortsnews.utils.AppPreference
import com.ns.shortsnews.utils.IntentLaunch
import com.rommansabbir.networkx.NetworkXProvider
import com.videopager.ui.VideoPagerFragment
import com.videopager.utils.CategoryConstants
import com.videopager.utils.NoConnection
import com.videopager.vm.SharedEventViewModelFactory
import com.videopager.vm.VideoSharedEventViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get


class MainActivity : AppCompatActivity(), onProfileItemClick {
    private lateinit var binding: ActivityMainBinding
    private lateinit var categoryAdapter: CategoryAdapter
    private var videoPagerFragment: VideoPagerFragment? = null
    private val sharedEventViewModel: VideoSharedEventViewModel by viewModels { SharedEventViewModelFactory }
    private var videoCategoryViewModel: VideoCategoryViewModel? = null
    private val videoDataViewModel: UserBookmarksViewModel by viewModels {
        BookmarksViewModelFactory().apply {
            inject(VideoDataUseCase(UserDataRepositoryImpl(get())))
        }
    }


    private val channelInfoViewModel: ChannelInfoViewModel by viewModels {
        ChannelInfoViewModelFactory().apply {
            inject(ChannelInfoUseCase(UserDataRepositoryImpl(get())))
        }
    }
    private val followUnfollowViewModel: FollowUnfollowViewModel by viewModels {
        FollowUnfollowViewModelFactory().apply {
            inject(FollowUnfollowUseCase(UserDataRepositoryImpl(get())))
        }
    }

    lateinit var standardBottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private var bottomSheetRecyclerAdapter: GridAdapter? = null
    private var channelDescription = ""

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppPreference.init(this@MainActivity)
        standardBottomSheetBehavior =
            BottomSheetBehavior.from(binding.persistentBottomsheet.bottomSheet)
        standardBottomSheetBehavior.isDraggable = false
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.black)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        if (AppPreference.isUserLoggedIn) {
            if (AppPreference.userProfilePic == "") {
                binding.profileIcon.setImageResource(R.drawable.profile_avatar)
            } else {
                binding.profileIcon.load(AppPreference.userProfilePic)
            }
        }

        // Disable Touch or Click event of BottomSheet for non clickable places
        binding.persistentBottomsheet.bottomSheet.setOnTouchListener(object : OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                return true
            }

        })

        binding.profileIcon.setOnClickListener {
            launchProfileActivity()
        }
        AppPreference.init(this@MainActivity)
        val videoCategories = AppPreference.categoryList
        Log.i("kamlesh", "Language category onSuccess ::: $videoCategories")


        CoroutineScope(Dispatchers.IO).launch {
            delay(1000)
            videoCategoryViewModel =
                ViewModelProvider(this@MainActivity, VideoCategoryViewModelFactory().apply {
                    inject(VideoCategoryUseCase(VideoCategoryRepositoryImp(get())))
                })[VideoCategoryViewModel::class.java]
        }

        if (AppPreference.categoryListStr!!.isEmpty()) {
            showTryAgainText()
        } else {

            categoryAdapter = CategoryAdapter(
                itemList = videoCategories,
                itemListener = this@MainActivity
            )
            binding.recyclerView.adapter = categoryAdapter
            val defaultCate = videoCategories[0]
            Log.i("lifecycle", "OnCreate loadHomeFragment")
            Log.i("language","$AppPreference.selectedLanguages!!")
                    loadHomeFragment(defaultCate.id, AppPreference.selectedLanguages!!)
            hideTryAgainText()
        }

        // Bottom Sheet
        bottomSheetDialog()
        // Register GetInfo Listener
        bottomSheetGetInfoListener()
        bottomSheetFollowingClick()
        registerVideoCache()
        bottomSheetDescClick()
        askNotificationPermission()
        launchLoginStateFlow()
        AppPreference.isMainActivityLaunched = true
    }

    private fun bottomSheetDescClick() {
        binding.persistentBottomsheet.channelDes.setOnClickListener {
            showDialog(channelDescription)
        }
    }

    private fun listenFollowUnfollow(channelId: String) {
        followUnfollowViewModel.requestFollowUnfollowApi(channelId)
        lifecycleScope.launch {
            followUnfollowViewModel.FollowUnfollowSuccessState.filterNotNull().collectLatest {
                AppPreference.isFollowingUpdateNeeded = true
                binding.persistentBottomsheet.profileCount.text = it.data.follow_count
                if (it.data.following) {
                    binding.persistentBottomsheet.following.text = "Following"
                    binding.persistentBottomsheet.followingExpanded.text = "Following"
                } else {
                    binding.persistentBottomsheet.following.text = "Follow"
                    binding.persistentBottomsheet.followingExpanded.text = "Follow"
                }

            }
        }
    }

    // Notification permission launcher
    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                registerForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) { isGranted ->
                    if (isGranted) {
                        // FCM SDK (and your app) can post notifications.
                    } else {
                        // TODO: Inform user that that your app will not show notifications.
                    }

                }.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }


    private fun getSelectedLanguagesValuesOnClick(requiredId: String) {
        Log.i("lifecycle", "Main activity get selected language msg")
        Log.i("language","$AppPreference.selectedLanguages!!")
        loadHomeFragment(requiredId, AppPreference.selectedLanguages!!)
        sharedEventViewModel.sendUserPreferenceData(
            AppPreference.isUserLoggedIn,
            AppPreference.userToken
        )
    }

    override fun onPause() {
        super.onPause()
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Log.i("lifecycle", "Main activity onPause")
    }

    override fun onResume() {
        super.onResume()
        Log.i("lifecycle", "Main activity OnResume")
        sharedEventViewModel.sendUserPreferenceData(
            AppPreference.isUserLoggedIn,
            AppPreference.userToken
        )

        if (AppPreference.isProfileUpdated) {
            binding.profileIcon.load(AppPreference.userProfilePic)
            AppPreference.isProfileUpdated = false
        }
        if (AppPreference.isRefreshRequired) {
            AppPreference.isRefreshRequired = false
            AppPreference.init(this)
            val videoCategories = AppPreference.categoryList
            categoryAdapter.setData(videoCategories)
            categoryAdapter = CategoryAdapter(
                itemList = videoCategories,
                itemListener = this@MainActivity
            )
            binding.recyclerView.adapter = categoryAdapter
            val defaultCate = videoCategories[0]
            loadHomeFragment(defaultCate.id, AppPreference.selectedLanguages!!)
            hideTryAgainText()
        }

        // When Bottom Slider is opened and selected any video to play
        // then coming back it should collapse the slider and play the video
        // Playing video is happening using lifecycle
        standardBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun reloadActivity() {
        AppPreference.isRefreshRequired = false
        AppPreference.init(this@MainActivity)
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }


    /**
     * It listens category item click
     */
    override fun itemclick(requiredId: String, position: Int, size: Int) {
        if (NetworkXProvider.isInternetConnected) {
            bottomSheetClearChannelId()
            getSelectedLanguagesValuesOnClick(requiredId)
        } else {
            // No Internet Snackbar: Fire
            NoConnection.noConnectionSnackBarInfinite(binding.root, this@MainActivity)
        }
    }

    /*
    Load profile activity
    */
    private fun launchProfileActivity() {
        if (NetworkXProvider.isInternetConnected) {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("fromActivity", "MainActivity")
            startActivity(intent)
        } else {
            // No Internet Snackbar: Fire
            NoConnection.noConnectionSnackBarInfinite(binding.root, this@MainActivity)
        }
    }


    /**
     * Loads Home Fragment
     */
    private fun loadHomeFragment(categoryType: String, languages: String) {
        Log.i("lifecycle", "Main activity Load Home fragment inside method")
        if (!supportFragmentManager.isStateSaved) {
            val ft = supportFragmentManager.beginTransaction()
            videoPagerFragment = AppConstants.makeVideoPagerInstance(
                categoryType,
                CategoryConstants.DEFAULT_VIDEO_DATA,
                this@MainActivity,
                languages = languages
            )
            ft.replace(
                R.id.fragment_container,
                videoPagerFragment!!
            )
            ft.commitAllowingStateLoss()
        }
    }

    // It gets callback from VideoPagerFragment
    // When User clicks on Follow, Like, Bookmark then it checks
    // If User is not logged in then it is called
    private fun launchLoginStateFlow() {
        lifecycleScope.launch {
            sharedEventViewModel.getLoginEventStatus.collectLatest {
                Log.i("lifecycle", "Main activity login flow called")
                launchProfileActivity()
            }
        }
    }


    /*Get API call for setting video categories*/
    private fun showCategory() {
        lifecycleScope.launch {
            videoCategoryViewModel!!.videoCategorySuccessState.filterNotNull().filter {
                it.videoCategories.isNotEmpty()
            }.collectLatest {
                // Setup recyclerView
                categoryAdapter = CategoryAdapter(
                    itemList = it.videoCategories,
                    itemListener = this@MainActivity
                )
                binding.recyclerView.adapter = categoryAdapter
                val defaultCate = it.videoCategories[0]
                Log.i("lifecycle", "Show category")
                Log.i("language","$AppPreference.selectedLanguages!!")
                loadHomeFragment(defaultCate.id, AppPreference.selectedLanguages!!)
                hideTryAgainText()
            }
        }

        lifecycleScope.launch {
            videoCategoryViewModel!!.errorState.filterNotNull().collectLatest {
                showTryAgainText()
            }
        }

    }

    private fun hideTryAgainText() {
        binding.tryAgain.visibility = View.GONE
        binding.noNetworkImg.visibility = View.GONE
    }

    private fun showTryAgainText() {
        binding.noNetworkParent.visibility = View.VISIBLE
        binding.tryAgain.visibility = View.VISIBLE
        binding.tryAgain.setOnClickListener {
            if (NetworkXProvider.isInternetConnected) {
                Log.i("language","$AppPreference.selectedLanguages!!")
                videoCategoryViewModel!!.loadVideoCategory(AppPreference.selectedLanguages!!)
                showCategory()
                if (AppPreference.userProfilePic == "") {
                    binding.profileIcon.setImageResource(R.drawable.profile_avatar)
                } else {
                    binding.profileIcon.load(AppPreference.userProfilePic)
                }
            } else {
                NoConnection.noConnectionSnackBarInfinite(binding.root, this@MainActivity)
            }

        }
        if (!NetworkXProvider.isInternetConnected) {
            binding.tryAgain.visibility = View.VISIBLE
            binding.noNetworkImg.visibility = View.VISIBLE
            binding.tryAgain.text = getString(R.string.no_internet_category)
            NoConnection.noConnectionSnackBarInfinite(binding.root, this@MainActivity)
        } else {
            binding.tryAgain.visibility = View.VISIBLE
            binding.noNetworkImg.visibility = View.GONE
            binding.tryAgain.text = getString(R.string.some_thing_went_wrong)
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
//                VideoPreloadCoroutine.schedulePreloadWork(videoUrls = videoUrls, ids = videoIds)
                HlsOneByOnePreloadCoroutine.schedulePreloadWork(
                    videoUrls = videoUrls,
                    ids = videoIds
                )
            }
        }

        lifecycleScope.launch {
            sharedEventViewModel.cacheVideoUrl_2.filterNotNull().collectLatest {
                val url = it.first
                val id = it.second
                val videoUrls = Array(1) { url }
                val videoIds = Array(1) { id }
                VideoPreloadCoroutine.schedulePreloadWork(videoUrls = videoUrls, ids = videoIds)
            }
        }
    }

    // It is having Bottom Sheet Implementation
    private fun bottomSheetDialog() {
        standardBottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                Log.i("BottomSheetCallback", "$slideOffset")
                binding.persistentBottomsheet.cardViewClientImage.alpha = 1.0f - slideOffset
                binding.persistentBottomsheet.following.alpha = 1.0f - slideOffset

                if (slideOffset == 1.0f) {
                    binding.persistentBottomsheet.following.isEnabled = false
                } else if (slideOffset == 0.0f) {
                    binding.persistentBottomsheet.following.isEnabled = true
                }
            }

            @SuppressLint("UseCompatLoadingForDrawables")
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        sharedEventViewModel.sendSliderState(BottomSheetBehavior.STATE_EXPANDED)
                        binding.persistentBottomsheet.imgDownArrow.setImageDrawable(
                            resources.getDrawable(R.drawable.slide_down_arrow_icon, null)
                        )
                    }

                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        Log.i("BottomSlider", "STATE_COLLAPSED")
                        sharedEventViewModel.sendSliderState(BottomSheetBehavior.STATE_COLLAPSED)
                        binding.persistentBottomsheet.imgDownArrow.setImageDrawable(
                            resources.getDrawable(R.drawable.slide_up_arrow_icon, null)
                        )
                    }

                    BottomSheetBehavior.STATE_DRAGGING -> {
                        Log.i("BottomSlider", "STATE_DRAGGING")
                        standardBottomSheetBehavior.setState(
                            BottomSheetBehavior.STATE_COLLAPSED
                        )
                    }

                    BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                        Log.i("BottomSlider", "STATE_HALF_EXPANDED")
                    }

                    BottomSheetBehavior.STATE_HIDDEN -> {
                        Log.i("BottomSlider", "STATE_HIDDEN")
                    }

                    BottomSheetBehavior.STATE_SETTLING -> {
                        Log.i("BottomSlider", "STATE_SETTLING")
                    }
                }
            }
        })

        // Bottom Sheet Image Arrow CLick Listener to OPEN or CLOSE Drawer
        binding.persistentBottomsheet.imgDownArrow.setOnClickListener {
            if (standardBottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                Log.i("BottomSlider", "STATE_EXPANDED-2")
                standardBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            } else {
                if (NetworkXProvider.isInternetConnected) {

                    channelInfoViewModel.clearChannelInfo()
                    videoDataViewModel.clearVideoData()

                    val channelId = binding.persistentBottomsheet.imgDownArrow.tag?.toString()
                    channelId?.let {
                        if (channelId != "") {
                            binding.persistentBottomsheet.progressBar.visibility = View.VISIBLE
                            binding.persistentBottomsheet.imgDownArrow.visibility = View.GONE
                            bottomSheetChannelInfo(channelId = channelId)
                            bottomSheetGetChannelVideoData(channelId = channelId)
                        }
                    }
                } else {
                    NoConnection.noConnectionSnackBarInfinite(binding.root, this@MainActivity)
                }
            }
        }
    }

    private fun bottomSheetHeaderViewsShowHide(show: Boolean) {
        val showHide = if (show) View.VISIBLE else View.GONE
        binding.persistentBottomsheet.following.visibility = showHide
        binding.persistentBottomsheet.clientImage.visibility = showHide
        binding.persistentBottomsheet.cardViewClientImage.visibility = showHide
        binding.persistentBottomsheet.progressBar.visibility = showHide
        binding.persistentBottomsheet.imgDownArrow.visibility = showHide
    }

    // Bottom Sheet Video Get Info Listen & Update
    private fun bottomSheetGetInfoListener() {

        // On App launch, BottomSheet Header should be in gone state
        bottomSheetHeaderViewsShowHide(false)

        lifecycleScope.launch {
            sharedEventViewModel.videoInfo.filterNotNull().collectLatest {

                bottomSheetHeaderViewsShowHide(true)

                if (it.following) {
                    binding.persistentBottomsheet.following.text = getString(R.string.following)
                    binding.persistentBottomsheet.followingExpanded.text =
                        getString(R.string.following)
                } else {
                    binding.persistentBottomsheet.following.text = getString(R.string.follow)
                    binding.persistentBottomsheet.followingExpanded.text =
                        getString(R.string.follow)
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
                            bottomSheetHeaderBg(bitmap, it.channel_id)
                            binding.persistentBottomsheet.channelLogo.setImageBitmap(bitmap)
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
                    binding.persistentBottomsheet.following.text = getString(R.string.following)
                    binding.persistentBottomsheet.followingExpanded.text =
                        getString(R.string.following)
                } else {
                    binding.persistentBottomsheet.following.text = getString(R.string.follow)
                    binding.persistentBottomsheet.followingExpanded.text =
                        getString(R.string.follow)
                }

            }
        }
    }

    //Get Channel info by channelId
    private fun bottomSheetChannelInfo(channelId: String) {
        Log.i("channelInfo", "Channel id :: $channelId")
        channelInfoViewModel.requestChannelInfoApi(channelId)
        lifecycleScope.launch {
            channelInfoViewModel.ChannelInfoSuccessState.filterNotNull().collectLatest {
                binding.persistentBottomsheet.profileCount.text = it.data.follow_count
                binding.persistentBottomsheet.channelDes.text = it.data.description
                channelDescription = it.data.description
            }
        }

        lifecycleScope.launch {
            channelInfoViewModel.errorState.filterNotNull().collectLatest {
                Log.i("channelInfo", it)
            }
        }

        lifecycleScope.launch {
            channelInfoViewModel.loadingState.filterNotNull().collectLatest {
                Log.i("channelInfo", "$it")
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
        bottomSheetRecyclerAdapter =
            GridAdapter(videoFrom = CategoryConstants.CHANNEL_VIDEO_DATA, channelId = channelId)

        lifecycleScope.launch {
            videoDataViewModel.errorState.filterNotNull().collectLatest {
                binding.persistentBottomsheet.progressBar.visibility = View.GONE
                binding.persistentBottomsheet.imgDownArrow.visibility = View.VISIBLE
            }
        }

        lifecycleScope.launch {
            videoDataViewModel.videoDataState.filterNotNull().collectLatest {
                bottomSheetRecyclerAdapter?.updateVideoData(mutableListOf<Data>())
                delay(500) // To show the progress bar properly
                binding.persistentBottomsheet.progressBar.visibility = View.GONE
                binding.persistentBottomsheet.imgDownArrow.visibility = View.VISIBLE
                it.let {
                    bottomSheetRecyclerAdapter?.updateVideoData(it.data)
                    binding.persistentBottomsheet.channelRecyclerview.adapter =
                        bottomSheetRecyclerAdapter
                    standardBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                }
            }
        }

        // Channel Item click listener
        lifecycleScope.launch {
            bottomSheetRecyclerAdapter!!.clicks().collectLatest {
                IntentLaunch.launchPlainVideoPlayer(it, this@MainActivity)
            }
        }

    }

    @SuppressLint("ResourceAsColor")
    private fun bottomSheetClearChannelId() {
        binding.persistentBottomsheet.following.tag = null
        binding.persistentBottomsheet.imgDownArrow.tag = null
        videoDataViewModel.clearChannelVideoData()
        bottomSheetRecyclerAdapter?.clearChannelData()
        binding.persistentBottomsheet.clientImage.setImageBitmap(null)
        binding.persistentBottomsheet.bottomSheetHeader.setBackgroundColor(R.color.black)
        binding.persistentBottomsheet.bottomSheetHeader.alpha = 0.3f
    }


    // Bottom Sheet Following Click Listener
    private fun bottomSheetFollowingClick() {

        binding.persistentBottomsheet.following.setOnClickListener {
            if (NetworkXProvider.isInternetConnected) {
                val channelId = binding.persistentBottomsheet.following.tag
                channelId?.let {
                    if (it != "") {
                        sharedEventViewModel.followRequest(channelId.toString())
                    }
                }
            } else {
                NoConnection.noConnectionSnackBarInfinite(binding.root, this@MainActivity)
            }
        }

        binding.persistentBottomsheet.followingExpanded.setOnClickListener {
            if (NetworkXProvider.isInternetConnected) {
                val channelId = binding.persistentBottomsheet.following.tag
                channelId?.let {
                    if (it != "") {
                        listenFollowUnfollow(channelId.toString())
                    }
                }
            } else {
                NoConnection.noConnectionSnackBarInfinite(binding.root, this@MainActivity)
            }
        }
    }

    /*Bottom sheet pallet color using bitmap icon from response URL*/
    private fun bottomSheetHeaderBg(bitmap: Bitmap, channelId: String) {
        binding.persistentBottomsheet.bottomSheetHeader.alpha = 1.0f

        val mutableBitmap = bitmap.copy(Bitmap.Config.RGBA_F16, true)

        Palette.from(mutableBitmap).generate { palette ->
            val headerTag = "HeaderBg"
            Log.i(headerTag, "============================== :: $channelId")
            val lightVibrantSwatch = palette?.lightVibrantSwatch?.rgb
            lightVibrantSwatch?.let {
                Log.i(headerTag, "lightVibrantSwatch :: $channelId")
                binding.persistentBottomsheet.bottomSheetHeader.setBackgroundColor(
                    lightVibrantSwatch
                )
                binding.persistentBottomsheet.channelTopView.setBackgroundColor(lightVibrantSwatch)
            }

            val vibrantSwatch = palette?.vibrantSwatch?.rgb
            vibrantSwatch?.let {
                Log.i(headerTag, "vibrantSwatch :: $channelId")
                binding.persistentBottomsheet.bottomSheetHeader.setBackgroundColor(vibrantSwatch)
                binding.persistentBottomsheet.channelTopView.setBackgroundColor(vibrantSwatch)
            }

            val lightMutedSwatch = palette?.lightMutedSwatch?.rgb
            lightMutedSwatch?.let {
                Log.i(headerTag, "lightMutedSwatch :: $channelId")
                binding.persistentBottomsheet.bottomSheetHeader.setBackgroundColor(lightMutedSwatch)
                binding.persistentBottomsheet.channelTopView.setBackgroundColor(lightMutedSwatch)
            }

            val mutedSwatch = palette?.mutedSwatch?.rgb
            mutedSwatch?.let {
                Log.i(headerTag, "mutedSwatch :: $channelId")
                binding.persistentBottomsheet.bottomSheetHeader.setBackgroundColor(mutedSwatch)
                binding.persistentBottomsheet.channelTopView.setBackgroundColor(mutedSwatch)
            }

            val darkMutedSwatch = palette?.darkMutedSwatch?.rgb
            darkMutedSwatch?.let {
                Log.i(headerTag, "darkVibrantSwatch :: $channelId")
                binding.persistentBottomsheet.bottomSheetHeader.setBackgroundColor(darkMutedSwatch)
                binding.persistentBottomsheet.channelTopView.setBackgroundColor(darkMutedSwatch)
            }

            val darkVibrantSwatch = palette?.darkVibrantSwatch?.rgb
            darkVibrantSwatch?.let {
                Log.i(headerTag, "darkVibrantSwatch :: $channelId")

                binding.persistentBottomsheet.bottomSheetHeader.setBackgroundColor(
                    darkVibrantSwatch
                )
                binding.persistentBottomsheet.channelTopView.setBackgroundColor(darkVibrantSwatch)
            }

        }
    }

    /*Handling Back pressed for main activity and channel bottom sheet state*/
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (standardBottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            Log.i("BottomSlider", "STATE_EXPANDED-4")
            standardBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        } else {
            onBackPressedDispatcher.onBackPressed()
            this@MainActivity.finish()
        }
    }


//    private fun notificationDataFromIntent(videoId:String, previewUrl:String, mediaUri:String){
//        videoPagerFragment?.getNotificationData(videoId,previewUrl,mediaUri)
//    }

    private fun showDialog(title: String) {
        val dialog = Dialog(this)
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