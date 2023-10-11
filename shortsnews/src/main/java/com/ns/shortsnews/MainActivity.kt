package com.ns.shortsnews

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.Window
import android.view.WindowManager
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.ns.shortsnews.adapters.CategoryAdapter
import com.ns.shortsnews.cache.HlsOneByOnePreloadCoroutine
import com.ns.shortsnews.cache.VideoPreloadCoroutine
import com.ns.shortsnews.data.model.VideoClikedItem
import com.ns.shortsnews.data.repository.UserDataRepositoryImpl
import com.ns.shortsnews.data.repository.VideoCategoryRepositoryImp
import com.ns.shortsnews.databinding.ActivityMainBinding
import com.ns.shortsnews.domain.usecase.channel.ChannelInfoUseCase
import com.ns.shortsnews.domain.usecase.followunfollow.FollowUnfollowUseCase
import com.ns.shortsnews.domain.usecase.video_category.UpdateVideoCategoriesUseCase
import com.ns.shortsnews.domain.usecase.video_category.VideoCategoryUseCase
import com.ns.shortsnews.domain.usecase.videodata.VideoDataUseCase
import com.ns.shortsnews.ui.fragment.ChannelVideosFragment
import com.ns.shortsnews.ui.paging.ChannelVideoAdapter
import com.ns.shortsnews.ui.viewmodel.*
import com.ns.shortsnews.utils.AppPreference
import com.ns.shortsnews.utils.IntentLaunch
import com.player.models.VideoData
import com.rommansabbir.networkx.NetworkXProvider
import com.videopager.data.VideoInfoData
import com.videopager.ui.VideoPagerFragment_2
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
//    private var videoPagerFragment: VideoPagerFragment? = null
    private var videoPagerFragment: VideoPagerFragment_2? = null
    private val sharedEventViewModel: VideoSharedEventViewModel by viewModels { SharedEventViewModelFactory }
    private var videoCategoryViewModel: VideoCategoryViewModel? = null

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
//    private var bottomSheetRecyclerAdapter: ChannelVideoAdapter? = null
    private var channelDescription = ""

    private lateinit var  channelsVideosViewModel2: ChannelVideoViewModel

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


        // Disable Touch or Click event of BottomSheet for non clickable places
        binding.persistentBottomsheet.bottomSheet.setOnTouchListener(object : OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                return true
            }
        })

        // Bottom Sheet Adapter Initialisation
//        bottomSheetRecyclerAdapter =
//            ChannelVideoAdapter(videoFrom = CategoryConstants.CHANNEL_VIDEO_DATA, channelId = "")

        binding.profileIcon.setOnClickListener {
            launchProfileActivity()
        }
        val videoCategories = AppPreference.categoryList

        CoroutineScope(Dispatchers.IO).launch {
            delay(1000)
            videoCategoryViewModel =
                ViewModelProvider(this@MainActivity, VideoCategoryViewModelFactory().apply {
                    inject(VideoCategoryUseCase(VideoCategoryRepositoryImp(get())), UpdateVideoCategoriesUseCase(VideoCategoryRepositoryImp(get())))
                })[VideoCategoryViewModel::class.java]
        }
        AppPreference.init(this@MainActivity)

        val isInternetConnected = NetworkXProvider.isInternetConnected

        if (AppPreference.categoryListStr!!.isEmpty() || !isInternetConnected) {
            showTryAgainText()
        } else  {
            categoryAdapter = CategoryAdapter(
                itemList = videoCategories,
                itemListener = this@MainActivity
            )
            binding.recyclerView.adapter = categoryAdapter
            val defaultCate = videoCategories[0]
            loadHomeFragment(defaultCate.id)
            hideTryAgainText()
        }

        // Bottom Sheet
        bottomSheetDialog()
        // Register GetInfo Listener
        bottomSheetGetInfoListener()
        bottomSheetFollowingClick()
        registerVideoCache()
        bottomSheetDescClick()
//        askNotificationPermission()
        launchLoginStateFlow()
        loadProfileImage()
        AppPreference.isMainActivityLaunched = true
    }

    private fun loadProfileImage() {
        if (AppPreference.isUserLoggedIn) {
            if (AppPreference.userProfilePic == "") {
                binding.profileIcon.setImageResource(R.drawable.profile_avatar)
            } else {
                Glide.with(MainApplication.instance!!).load(AppPreference.userProfilePic).into(binding.profileIcon)
            }
        }
    }

    private fun bottomSheetDescClick() {
//        binding.persistentBottomsheet.channelDes.setOnClickListener {
//            showDialog(channelDescription)
//        }
    }


    private fun getSelectedLanguagesValuesOnClick(requiredId: String) {
        Log.i("language","$AppPreference.selectedLanguages!!")
        loadHomeFragment(requiredId)
        sharedEventViewModel.sendUserPreferenceData(
            AppPreference.isUserLoggedIn,
            AppPreference.userToken
        )
    }

    override fun onPause() {
        super.onPause()
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    override fun onResume() {
        super.onResume()
        sharedEventViewModel.sendUserPreferenceData(
            AppPreference.isUserLoggedIn,
            AppPreference.userToken
        )

        if (AppPreference.isProfileUpdated) {
            Glide.with(MainApplication.instance!!).load(AppPreference.userProfilePic).into(binding.profileIcon)
            AppPreference.isProfileUpdated = false
        }
        if (AppPreference.isRefreshRequired) {
           updateCategoryList()
        }

        // When Bottom Slider is opened and selected any video to play
        // then coming back it should collapse the slider and play the video
        // Playing video is happening using lifecycle
        standardBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun updateCategoryList() {
        AppPreference.isRefreshRequired = false
        AppPreference.init(this@MainActivity)
        val videoCategories = AppPreference.categoryList
        categoryAdapter.setData(videoCategories)
        categoryAdapter = CategoryAdapter(
            itemList = videoCategories,
            itemListener = this@MainActivity
        )
        binding.recyclerView.adapter = categoryAdapter
        val defaultCate = videoCategories[0]
        loadHomeFragment(defaultCate.id)
        hideTryAgainText()
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

    fun getSeekbar(): SeekBar {
        return binding.videoSeekbar
    }

    /**
     * Loads Home Fragment
     */
    private fun loadHomeFragment(categoryId: String) {
        if (NetworkXProvider.isInternetConnected) {
            val videoItems = VideoClikedItem(requiredId = categoryId, videoFrom = CategoryConstants.DEFAULT_VIDEO_DATA, selectedPosition = 0, loadedVideoData = emptyList())
            videoPagerFragment = VideoPagerFragment_2()
            val bundle = Bundle()
            bundle.putBoolean("logged_in", AppPreference.isUserLoggedIn)
            bundle.putParcelable("videoItems", videoItems)
            videoPagerFragment!!.arguments = bundle
            val ft = supportFragmentManager.beginTransaction()
            ft.replace(R.id.fragment_container, videoPagerFragment!!)
            ft.commit()

        } else {
            // No Internet Snackbar: Fire
            NoConnection.noConnectionSnackBarInfinite(binding.root, this@MainActivity)

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
                loadHomeFragment(defaultCate.id)
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
                if (AppPreference.categoryList.isEmpty()) {
                    videoCategoryViewModel!!.loadVideoCategory()
                    showCategory()
                    if (AppPreference.userProfilePic == "") {
                        binding.profileIcon.setImageResource(R.drawable.profile_avatar)
                    } else {
                        Glide.with(MainApplication.instance!!).load(AppPreference.userProfilePic).into(binding.profileIcon)
                    }
                } else {
                    Glide.with(MainApplication.instance!!).load(AppPreference.userProfilePic).into(binding.profileIcon)
                    AppPreference.init(this@MainActivity)
                    val videoCategories = AppPreference.categoryList
                    categoryAdapter = CategoryAdapter(
                        itemList = videoCategories,
                        itemListener = this@MainActivity
                    )
                    binding.recyclerView.adapter = categoryAdapter
                    val defaultCate = videoCategories[0]
                    loadHomeFragment(defaultCate.id)
                    hideTryAgainText()
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

    private fun showSeekbar() {
        binding.videoSeekbar.visibility = View.VISIBLE
    }
    private fun hideSeekbar() {
        binding.videoSeekbar.visibility = View.GONE
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
                        hideSeekbar()
                        Log.i("BottomSlider", "STATE_DRAGGING")
                        sharedEventViewModel.sendSliderState(BottomSheetBehavior.STATE_EXPANDED)
                        binding.persistentBottomsheet.imgDownArrow.setImageDrawable(
                            resources.getDrawable(R.drawable.slide_down_arrow_icon, null)
                        )
                    }

                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        showSeekbar()
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
                standardBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            } else {
                if (NetworkXProvider.isInternetConnected) {

                    channelInfoViewModel.clearChannelInfo()

                    val videoInfoData = binding.persistentBottomsheet.imgDownArrow.tag
                    val videoInfo = videoInfoData as VideoInfoData
                    videoInfo.let {
                        if (videoInfo.channel_id != "") {
                            binding.persistentBottomsheet.progressBar.visibility = View.VISIBLE
                            binding.persistentBottomsheet.imgDownArrow.visibility = View.GONE
                            bottomSheetChannelInfo(channelId = it.channel_id)
                            bottomSheetGetChannelVideoData(videoInfoData = it)
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
                } else {
                    binding.persistentBottomsheet.following.text = getString(R.string.follow)
                }

                val channelId = binding.persistentBottomsheet.following.tag
//                if(channelId != it.channel_id) {
                binding.persistentBottomsheet.following.tag = it.channel_id
                binding.persistentBottomsheet.imgDownArrow.tag = it

                if (it.channel_image.isNotEmpty()) {
                    // Loading Channel Icon and Background Color from bitmap
                    loadBottomChannelLogoPallet(it)
                    binding.persistentBottomsheet.cardViewClientImage.visibility = View.VISIBLE
                } else {
                    binding.persistentBottomsheet.cardViewClientImage.visibility = View.INVISIBLE
                }
//                }
            }
        }

        lifecycleScope.launch {
            sharedEventViewModel.followResponse.filterNotNull().collectLatest {
                if (it.following) {
                    binding.persistentBottomsheet.following.text = getString(R.string.following)
                } else {
                    binding.persistentBottomsheet.following.text = getString(R.string.follow)
                }

            }
        }
    }

    companion object {
        var channelVisibleBitmap: Bitmap? = null
    }
    private fun loadBottomChannelLogoPallet(videoInfoData: VideoInfoData) {
        Glide.with(MainApplication.instance!!)
            .asBitmap()
            .load(videoInfoData.channel_image)
            .listener(object : RequestListener<Bitmap> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Bitmap>,
                    isFirstResource: Boolean
                ): Boolean {
                    Log.i("HeaderBg", "MainActivity: Channel Icon Bitmap NOT loaded")
                    return false
                }

                override fun onResourceReady(
                    bitmap: Bitmap,
                    model: Any,
                    target: com.bumptech.glide.request.target.Target<Bitmap>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    Log.i("HeaderBg", "MainActivity: Channel Icon Bitmap loaded :: ")
                    lifecycleScope.launch {
                        channelVisibleBitmap = bitmap
                        binding.persistentBottomsheet.clientImage.setImageBitmap(bitmap)
                        Log.i("HeaderBg", "MainActivity: Channel Icon Bitmap loaded :: ${videoInfoData.channel_id}")
                        bottomSheetHeaderBg(bitmap)
                    }

//                    binding.persistentBottomsheet.cardViewClientImage.visibility = View.VISIBLE
                    return false
                }
            }).submit()
    }

    //Get Channel info by channelId
    private fun bottomSheetChannelInfo(channelId: String) {
        Log.i("channelInfo", "Channel id :: $channelId")
        channelInfoViewModel.requestChannelInfoApi(channelId)
        lifecycleScope.launch {
            channelInfoViewModel.ChannelInfoSuccessState.filterNotNull().collectLatest {
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


    /**
     * Bottom Sheet Channel Video Data
     */
    private fun bottomSheetGetChannelVideoData(videoInfoData: VideoInfoData) {
        val channelVideosFragment = ChannelVideosFragment().apply {
            val bundle = Bundle()
            bundle.putString("channelId", videoInfoData.channel_id)
            bundle.putString("channelTitle", videoInfoData.title)
            bundle.putString("channelUrl", videoInfoData.channel_image)
            bundle.putString("channelDes", videoInfoData.description)
            bundle.putString("from", "MainActivity")
            bundle.putString("follow_count", videoInfoData.follow_count)
            bundle.putBoolean("following", videoInfoData.following)
            arguments = bundle
        }
        AppPreference.isUpdateNeeded = true
        supportFragmentManager.beginTransaction()
            .replace(R.id.channel_container, channelVideosFragment)
            .commit()
        standardBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        binding.persistentBottomsheet.progressBar.visibility = View.GONE
        binding.persistentBottomsheet.imgDownArrow.visibility = View.VISIBLE

        hideSeekbar()
    }

    @SuppressLint("ResourceAsColor")
    private fun bottomSheetClearChannelId() {
        binding.persistentBottomsheet.following.tag = null
        binding.persistentBottomsheet.imgDownArrow.tag = null
//        videoDataViewModel.clearChannelVideoData()
//        bottomSheetRecyclerAdapter?.clearChannelData()
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


    }

    /*Bottom sheet pallet color using bitmap icon from response URL*/
    private fun bottomSheetHeaderBg(bitmap: Bitmap) {
        binding.persistentBottomsheet.bottomSheetHeader.alpha = 1.0f

        val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)

        Palette.from(mutableBitmap).generate { palette ->

            val lightVibrantSwatch = palette?.lightVibrantSwatch?.rgb
            lightVibrantSwatch?.let {
                binding.persistentBottomsheet.bottomSheetHeader.setBackgroundColor(
                    lightVibrantSwatch
                )
            }

            val vibrantSwatch = palette?.vibrantSwatch?.rgb
            vibrantSwatch?.let {
                binding.persistentBottomsheet.bottomSheetHeader.setBackgroundColor(vibrantSwatch)
            }

            val lightMutedSwatch = palette?.lightMutedSwatch?.rgb
            lightMutedSwatch?.let {
                binding.persistentBottomsheet.bottomSheetHeader.setBackgroundColor(lightMutedSwatch)
            }

            val mutedSwatch = palette?.mutedSwatch?.rgb
            mutedSwatch?.let {
                binding.persistentBottomsheet.bottomSheetHeader.setBackgroundColor(mutedSwatch)
            }

            val darkMutedSwatch = palette?.darkMutedSwatch?.rgb
            darkMutedSwatch?.let {
                binding.persistentBottomsheet.bottomSheetHeader.setBackgroundColor(darkMutedSwatch)
            }

            val darkVibrantSwatch = palette?.darkVibrantSwatch?.rgb
            darkVibrantSwatch?.let {
                binding.persistentBottomsheet.bottomSheetHeader.setBackgroundColor(
                    darkVibrantSwatch
                )
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