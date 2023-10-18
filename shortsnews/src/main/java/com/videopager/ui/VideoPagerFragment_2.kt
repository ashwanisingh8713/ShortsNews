package com.videopager.ui

import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.content.Intent
import android.os.*
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.viewpager2.widget.ViewPager2
import com.exo.manager.DemoUtil
import com.exo.manager.DownloadTracker
import com.exo.players.ExoAppPlayerFactory
import com.exo.ui.ExoAppPlayerViewFactory
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.ns.shortsnews.MainActivity
import com.ns.shortsnews.MainApplication
import com.rommansabbir.networkx.NetworkXProvider.isInternetConnected
import com.ns.shortsnews.R
import com.ns.shortsnews.data.model.VideoClikedItem
import com.ns.shortsnews.data.source.UserApiService
import com.ns.shortsnews.databinding.VideoPagerFragmentBinding
import com.ns.shortsnews.utils.AppPreference
import com.ns.shortsnews.video.data.VideoDataRepositoryImpl
import com.videopager.data.VideoInfoData
import com.videopager.models.*
import com.videopager.ui.extensions.*
import com.videopager.ui.fragment.CommentsFragment
import com.videopager.utils.NoConnection
import com.videopager.vm.SharedEventViewModelFactory
import com.videopager.vm.VideoPagerViewModel
import com.videopager.vm.VideoPagerViewModelFactory_2
import com.videopager.vm.VideoSharedEventViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent


class VideoPagerFragment_2 : Fragment(R.layout.video_pager_fragment) {

//    private val viewModel: VideoPagerViewModel by viewModels { viewModelFactory(this) }


    private val sharedEventViewModel: VideoSharedEventViewModel by activityViewModels { SharedEventViewModelFactory }
    lateinit var binding: VideoPagerFragmentBinding
    private lateinit var pagerAdapter: PagerAdapter
    private lateinit var commentFragment: CommentsFragment
    private var isUserLoggedIn = false
    private var isFirstVideoInfoLoaded = false
    private var isNotificationVideoCame: Boolean = false
    private var redirectFrom: String? = null

    private lateinit var videoItems: VideoClikedItem

    private lateinit var viewModel: VideoPagerViewModel
    private var seekbar: SeekBar? = null


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity) {
            seekbar = context.getSeekbar()
        }
    }


    /*fun setSeekbar(seekbar: SeekBar?) {
        this.seekbar = seekbar
        Log.e("showTryAgainText", "getSeekbar = $seekbar")
        // Update Progress in Bottom
        view?.let{
            this.seekbar?.let {
                setupSeekbarProgress(it)
            }
        }
    }*/


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            isUserLoggedIn = arguments?.getBoolean("logged_in")!!
            redirectFrom = arguments?.getString("directFrom")
            videoItems = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arguments?.getParcelable("videoItems", VideoClikedItem::class.java)!!
            } else {
                arguments?.getParcelable("videoItems")!!
            }
        }

        // Initialising ViewModel Factory
        val viewModelfactory = VideoPagerViewModelFactory_2(
            repository = VideoDataRepositoryImpl(userApiService = KoinJavaComponent.getKoin().get<UserApiService>()),
            appPlayerFactory = ExoAppPlayerFactory(
                context = requireContext(), cache = MainApplication.cache, currentMediaItemIndex = videoItems.selectedPosition
            ),
            requiredId = videoItems.requiredId,
            videoFrom = videoItems.videoFrom,
            languages = AppPreference.getSelectedLanguagesAsString(),
            selectedPlay = videoItems.selectedPosition,
            loadedVideoData = videoItems.loadedVideoData
        ).create(this)

        //  Initialising ViewModel
        viewModel = ViewModelProvider(this, viewModelfactory).get(VideoPagerViewModel::class.java)

    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Listening user status via shared view Module from main activity preference utils
        registerSharedViewModel()

        val appPlayerView =  ExoAppPlayerViewFactory().create(requireContext())

        viewModel.setVMContext(requireContext())
        viewModel.setPlayerVieww(appPlayerView.getPlayerView())

        binding = VideoPagerFragmentBinding.bind(view)
        // This single player view instance gets attached to the ViewHolder of the active ViewPager page
//        val appPlayerView = appPlayerViewFactory.create(view.context)
        pagerAdapter = PagerAdapter()
        binding.viewPager.adapter = pagerAdapter
        binding.viewPager.isUserInputEnabled = false
        binding.viewPager.offscreenPageLimit = 5 // Preload neighbouring page image previews
        commentFragment = CommentsFragment()

//        lifecycleScope.launch {
//            viewModel.videoProgressBar.collectLatest {
//                pagerAdapter.showPlayerFor(0)
//                binding.progressBarVideoShorts.visibility = View.GONE
//            }
//        }


        val states = viewModel.states
            .onEach { state ->
                // Await the list submission so that the adapter list is in sync with state.videoData
                pagerAdapter.awaitList(state.videoData)

                // Attach the player to the View whenever it's ready. Note that attachPlayer can
                // be false while appPlayer is non-null during configuration changes and, conversely,
                // attachPlayer can be true while appPlayer is null when the appPlayer hasn't been
                // set up but the view is ready for it. That is why both are checked here.
                if (state.attachPlayer && state.appPlayer != null) {
                    appPlayerView.attach(state.appPlayer)
                } else {
                    appPlayerView.detachPlayer()
                }

                // Restore any saved page state from process recreation and configuration changes.
                // Guarded by an isIdle check so that state emissions mid-swipe or during page change
                // animations are ignored. There would have a jarring page-change effect without that.
                if (binding.viewPager.isIdle) {
                    binding.viewPager.setCurrentItem(state.page, false)
                }

                // Can't query any ViewHolders if the adapter has no pages
                if (pagerAdapter.currentList.isNotEmpty()) {
                    // Set the player view on the active page. Note that ExoPlayer won't render
                    // any frames until the output view (here, appPlayerView) is on-screen
                    pagerAdapter.attachPlayerView(appPlayerView, state.page)

                    // If the player media is rendering frames, then show the player
                    if (state.showPlayer) {
                        pagerAdapter.showPlayerFor(state.page)
                        binding.progressBarVideoShorts.visibility = View.GONE
                    }
                    binding.viewPager.isUserInputEnabled = true

                    if (!isFirstVideoInfoLoaded) {
                        binding.viewPager.setCurrentItem(videoItems.selectedPosition, true)
                        val data = pagerAdapter.getVideoData(videoItems.selectedPosition)
                        viewModel.processEvent(VideoInfoEvent(data.id, videoItems.selectedPosition))
                        isFirstVideoInfoLoaded = true
                    }
                }
            }



        val effects = viewModel.effects
            .onEach { effect ->
                Log.i("AshwaniEffect", "$effect")
                when (effect) {
                    is BookmarkEffect -> {
                        pagerAdapter.refreshBookmarkUI(effect.position)
                        val videoData = pagerAdapter.getVideoData(effect.position)
                        bookmarkBroadcast(id = videoData.id, videoData.saved)
                    }

                    is CommentEffect -> {
                        //adapter.refreshUI(0)
                        // Pass Data to Bottom sheet dialog fragment
                        commentFragment.setRecyclerData(
                            effect.videoId,
                            effect.comments,
                            effect.position
                        )
                    }

                    is LikeEffect -> {
                        pagerAdapter.refreshLikeUI(effect.position)
                        pagerAdapter.refreshBookmarkUI(effect.position)
                        val videoData = pagerAdapter.getVideoData(effect.position)
                        likeBroadcast(id = videoData.id, videoData.liking, videoData.like_count)
                    }

                    is PageEffect -> {
                        pagerAdapter.renderEffect(
                            binding.viewPager.currentItem,
                            effect
                        )
                    }

                    is PlayerErrorEffect -> {
                        /*Snackbar.make(
                            binding.root,
                            effect.throwable.message ?: "Error",
                            Snackbar.LENGTH_LONG
                        ).show()*/
                    }

                    is GetVideoInfoEffect -> {
                        if (effect.videoInfo.id.isNotEmpty()) {
                            pagerAdapter.getInfoRefreshUI(effect.position)
                        }
                        sharedEventViewModel.shareVideoInfo(effect.videoInfo)

                    }

                    is PostCommentEffect -> {
                        pagerAdapter.getInfoRefreshUI(effect.position)
                        val currentItem = binding.viewPager.currentItem
                        val videoData = pagerAdapter.getVideoData(currentItem)
                        viewModel.processEvent(CommentClickEvent(videoData.id, currentItem))
                    }


                    is MediaItemTransitionEffect -> { // Auto Scroll to next video
                        val currentItem = binding.viewPager.currentItem
                        if(currentItem < pagerAdapter.itemCount-1 ) {
                            binding.viewPager.setCurrentItem(currentItem + 1)
                        } else {
                            binding.viewPager.setCurrentItem(0, false)
                        }
                    }

                    else -> {}
                }
            }

        val events = merge(
            viewLifecycleOwner.lifecycle.viewEvents(),
            binding.viewPager.viewEvents(),
            pagerAdapter.clicksEvent(),
            commentFragment.postClickComment()

        ).onEach(viewModel::processEvent)

        merge(states, effects, events)
            .launchIn(viewLifecycleOwner.lifecycleScope)

        // This is Important to set / check because we are using two seekbar
        // one in MainActivity, because Thumb was truncating due to bottom sliding sheet.
        // and another in VideoPagerFragment
        seekbar = seekbar?:binding.videoSeekbar.apply { visibility = View.VISIBLE }
        // Update Progress in Bottom
        seekbar?.let {
            setupSeekbarProgress(it)
        }

    }


    override fun onResume() {
        super.onResume()

        if (pagerAdapter.itemCount > 0) {
            val data = pagerAdapter.getVideoData(binding.viewPager.currentItem)
            viewModel.processEvent(VideoInfoEvent(data.id, binding.viewPager.currentItem))
        }

        Log.d("AshwaniPerformance", "VideoPagerFragment onResume()")
    }

    private fun Lifecycle.viewEvents(): Flow<ViewEvent> {
        return events()
            .filter { event -> event == Lifecycle.Event.ON_START || event == Lifecycle.Event.ON_STOP }
            .map { event ->
                // Fragment starting or stopping is a signal to create or tear down the player, respectively.
                // The player should not be torn down across config changes, however.
                when (event) {
                    Lifecycle.Event.ON_START -> PlayerLifecycleEvent.Start
                    Lifecycle.Event.ON_STOP -> PlayerLifecycleEvent.Stop(requireActivity().isChangingConfigurations)
                    else -> error("Unhandled event: $event")
                }
            }
    }

    private fun ViewPager2.viewEvents(): Flow<ViewEvent> {
        return merge(

            // Idling on a page after a scroll is a signal to try and change player playlist positions
            pageIdlings().map {
                OnPageSettledEvent(currentItem)
            },
            // A page change (which can happen before a page is idled upon) is a signal to pause media. This
            // is useful for when a user is quickly swiping thru pages and the idle state isn't getting reached.
            // It doesn't make sense for a video on a previous page to continue playing while the user is
            // swiping quickly thru pages.
            pageChanges().map {
                PauseVideoEvent
            },
            getPageInfo().map {
                val data = pagerAdapter.getVideoData(currentItem)
                VideoInfoEvent(data.id, currentItem)
            },
            videoCache().map {
                var nextVideoCacheIndex = 0
                nextVideoCacheIndex = if (currentItem < pagerAdapter.itemCount - 1) {
                    currentItem + 1
                } else {
                    currentItem
                }
                val nextVideoCacheData = pagerAdapter.getVideoData(nextVideoCacheIndex)
                sharedEventViewModel.cacheVideoData(
                    nextVideoCacheData.mediaUri,
                    nextVideoCacheData.id
                )

                NoFurtherEvent
            },
            loadMoreVideoData().map {
                if (isInternetConnected) {
                    val Tag = "PagePreload"
                    Log.i("AshwaniXYZ", "")
                    val loadedLastPage = viewModel.states.value.videoData!!.get(viewModel.states.value.videoData!!.size-1).page
                    Log.i(Tag, "loadMoreVideoData() ${loadedLastPage+1}")
                    // For handling load more video case to not load more video when it is redirected from plainVideo activity
//                    if (redirectFrom == null) {
                        viewModel.processEvent(
                            LoadVideoDataEvent(
                                categoryId = viewModel.categoryId,
                                videoFrom = viewModel.videoFrom,
                                page = loadedLastPage+1,
                                perPage = VideoPagerViewModel.perPage,
                                languages = viewModel.languages
                            )
                        )
//                    }
                }
                NoFurtherEvent
            }
        )
    }


    private fun CommentsFragment.postClickComment(): Flow<ViewEvent> {
        return clicks().map {
            PostClickCommentEvent(it.first, it.second, it.third)
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun PagerAdapter.clicksEvent(): Flow<ViewEvent> {
        return clicks().map {
            when (it.second) {
                PlayPauseClick -> TappedPlayerEvent

                ChannelClick -> {
                    if (isInternetConnected) {
                        val currentItem = binding.viewPager.currentItem
                        val videoData = pagerAdapter.getVideoData(currentItem)
                        ChannelClickEvent
                    } else {
                        NoConnection.noConnectionSnackBarInfinite(
                            view,
                            requireActivity() as AppCompatActivity
                        )
                        NoFurtherEvent
                    }

                }

                ShareClick -> {
                    val currentItem = binding.viewPager.currentItem
                    val videoData = pagerAdapter.getVideoData(currentItem)
                    val chooserIntent = Intent(Intent.ACTION_SEND)
                    chooserIntent.type = "text/plain"
                    chooserIntent.putExtra(Intent.EXTRA_TEXT, "NewsDx Shorts")
//                    var sAux = "\n Let me recommend you this application\n\n"
//                    sAux += "https://play.google.com/store/apps/details?id=in.newsdx.preview \n\n"

                    var sAux = videoData.mediaUri

                    chooserIntent.putExtra(Intent.EXTRA_TEXT, sAux)
                    activity?.startActivity(Intent.createChooser(chooserIntent, "Share Via"))
                    ShareClickEvent
                }

                CommentClick -> {
                    if (isInternetConnected) {
                        if (!isUserLoggedIn) {
                            sharedEventViewModel.launchLoginEvent(true)
                            NoFurtherEvent
                        } else {
                            commentFragment.show(childFragmentManager, "comments")
                            val currentItem = binding.viewPager.currentItem
                            val videoData = pagerAdapter.getVideoData(currentItem)
                            CommentClickEvent(videoData.id, currentItem)
                        }
                    } else {
                        NoConnection.noConnectionSnackBarInfinite(
                            view,
                            requireActivity() as AppCompatActivity
                        )
                        NoFurtherEvent
                    }
                }

                DoubleTapClick -> { // This is performing same as LikeClick
                    if (isInternetConnected) {
                        if (!isUserLoggedIn) {
                            sharedEventViewModel.launchLoginEvent(true)
                            NoFurtherEvent
                        } else {
                            vibratePhone()
                            val currentItem = binding.viewPager.currentItem
                            val videoData = pagerAdapter.getVideoData(currentItem)
                            LikeClickEvent(videoData.id, currentItem)
                        }
                    } else {
                        NoConnection.noConnectionSnackBarInfinite(
                            view,
                            requireActivity() as AppCompatActivity
                        )
                        NoFurtherEvent
                    }
                }

                LikeClick -> {
                    if (isInternetConnected) {
                        if (!isUserLoggedIn) {
                            sharedEventViewModel.launchLoginEvent(true)
                            NoFurtherEvent
                        } else {
                            vibratePhone()
                            val currentItem = binding.viewPager.currentItem
                            val videoData = pagerAdapter.getVideoData(currentItem)
                            LikeClickEvent(videoData.id, currentItem)
                        }
                    } else {
                        NoConnection.noConnectionSnackBarInfinite(
                            view,
                            requireActivity() as AppCompatActivity
                        )
                        NoFurtherEvent
                    }
                }

                BookmarkClick -> {
                    if (isInternetConnected) {
                        if (!isUserLoggedIn) {
                            sharedEventViewModel.launchLoginEvent(true)
                            NoFurtherEvent
                        } else {
                            vibratePhone()
                            val currentItem = binding.viewPager.currentItem
                            val videoData = pagerAdapter.getVideoData(currentItem)
                            BookmarkClickEvent(videoId = videoData.id, currentItem)
                        }
                    } else {
                        NoConnection.noConnectionSnackBarInfinite(
                            view,
                            requireActivity() as AppCompatActivity
                        )
                        NoFurtherEvent
                    }
                }

                TrackInfoClick -> {
                    downloadTracker = DemoUtil.getDownloadTracker(requireActivity())
                    val renderersFactory = DemoUtil.buildRenderersFactory(requireActivity())
                    downloadTracker?.toggleDownload(
                        requireActivity().supportFragmentManager,
                        viewModel.states.value.appPlayer?.player?.currentMediaItem,
                        renderersFactory
                    )
                    NoFurtherEvent
                }

                else -> NoFurtherEvent

            }

        }
    }

    private var downloadTracker: DownloadTracker? = null

    private fun registerSharedViewModel() {
        lifecycleScope.launch {
            sharedEventViewModel.cacheUserStatus.filterNotNull().collectLatest {
                isUserLoggedIn = it.first
                Log.i("newDataUser", "User logged in status ${it.first}")
            }
        }

        lifecycleScope.launch {
            // It listens Follow/Unfollow update,
            // When User is not logged in then it will launch login screen
            // User is logged in
            sharedEventViewModel.paletteColor.collectLatest {
                if (!isUserLoggedIn) {
                    sharedEventViewModel.launchLoginEvent(true)
                }
            }
        }

        lifecycleScope.launch {
            sharedEventViewModel.sliderState.filterNotNull().collectLatest {
                when (it) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        viewModel.playerView?.player?.pause()
                    }

                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        viewModel.playerView?.player?.play()
                    }

                }
            }
        }
    }


    private fun progressBarValue(position: Long, player: ExoPlayer): Int {
        val PROGRESS_BAR_MAX = 100
        val duration = player.duration
        val value =
            if (duration == C.TIME_UNSET || duration == 0L) 0 else (position * PROGRESS_BAR_MAX / duration)
        return value.toInt()
    }


    private var progressBarJob: Job? = null

    /**
     * Setup Seekbar progress
     */
    private fun setupSeekbarProgress(seekbar: SeekBar) {

        progressBarJobUpdater(seekbar)

        seekbar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
//                if(fromUser) {
//                    val player = viewModel.states.value.appPlayer?.player
                // When we seekTo player, then onPlaybackStateChanged is called, with playingState = 3
//                    player?.seekTo(progress.toLong() * player.duration / 100)
//                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                progressBarJob?.cancel()
                Log.i("JOBCan", "CANCELLED")
                seekBar?.let{it.thumb.alpha = 255}
                val player = viewModel.states.value.appPlayer?.player
                player?.pause()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                val player = viewModel.states.value.appPlayer?.player
                val progress = seekBar?.progress ?: 0
                // When we seekTo player, then onPlaybackStateChanged is called, with playingState = 3
                player?.seekTo(progress.toLong() * player.duration / 100)
                seekBar?.let { progressBarJobUpdater(it) }
            }

        })

    }

    /**
     * Coroutine Job for Seekbar progress updater
     */
    private fun progressBarJobUpdater(seekbar: SeekBar) {
        progressBarJob?.cancel()
        seekbar.thumb.alpha = 0
        progressBarJob = viewLifecycleOwner.lifecycleScope.launch {
            while (isActive) {
                val player = viewModel.states.value.appPlayer?.player
                player?.let {
                    val duration: Long = player.duration
                    val position: Long = player.currentPosition
                    seekbar.progress = progressBarValue(position, player)
                    seekbar.secondaryProgress = progressBarValue(duration, player)
                    Log.i("JOBCan", "DOING")
                }
                delay(100)
            }
        }
    }


    /**
     * Vibrate phone for
     */
    @RequiresApi(Build.VERSION_CODES.S)
    private fun Fragment.vibratePhone() {
        val vib = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                activity?.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            activity?.getSystemService(VIBRATOR_SERVICE) as Vibrator
        }
        vib.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
    }



    fun getNotificationData(videoId: String, previewUrl: String, videoUrl: String) {
        isNotificationVideoCame = true
        viewModel.processEvent(
            FromNotificationInsertVideoEvent(
                videoId = videoId,
                previewUrl = previewUrl,
                videoUrl = videoUrl,
                type = "GN"
            )
        )
//         viewModel.processEvent(InsertVideoEvent(videoId = videoId, previewUrl = previewUrl, videoUrl = videoUrl, type = "GN"))
//         viewModel.processEvent(NotificationVideoPlayerSettleEvent( viewModel.playerView?.player?.currentMediaItemIndex ?:0, videoId))
    }

    private fun bookmarkBroadcast(id: String, bookmark: Boolean) {
        val intent = Intent("BookmarkFragmentUpdated")
        intent.putExtra("actionType", "BookmarkEffect")
        intent.putExtra("id", id)
        intent.putExtra("bookmark", bookmark)
        LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
    }

    private fun likeBroadcast(id: String, liked: Boolean, likeCount: String) {
        val intent = Intent("BookmarkFragmentUpdated")
        intent.putExtra("actionType", "LikeEffect")
        intent.putExtra("id", id)
        intent.putExtra("liked", liked)
        intent.putExtra("likeCount", likeCount)
        LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
    }





}
