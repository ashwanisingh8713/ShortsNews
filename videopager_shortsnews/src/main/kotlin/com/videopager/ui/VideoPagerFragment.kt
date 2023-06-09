package com.videopager.ui

import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.content.Intent
import android.os.*
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.savedstate.SavedStateRegistryOwner
import androidx.viewpager2.widget.ViewPager2
import coil.ImageLoader
import com.exo.manager.DemoUtil
import com.exo.manager.DownloadTracker
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.player.ui.AppPlayerView
import com.videopager.R
import com.videopager.data.VideoInfoData
import com.videopager.databinding.VideoPagerFragmentBinding
import com.videopager.models.*
import com.videopager.ui.extensions.*
import com.videopager.ui.fragment.CommentsFragment
import com.videopager.utils.CategoryConstants
import com.videopager.vm.SharedEventViewModelFactory
import com.videopager.vm.VideoPagerViewModel
import com.videopager.vm.VideoSharedEventViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


class VideoPagerFragment(
    private val viewModelFactory: (SavedStateRegistryOwner) -> ViewModelProvider.Factory,
    private val appPlayerView: AppPlayerView,
    private val imageLoader: ImageLoader,
) : Fragment(R.layout.video_pager_fragment) {
    private val viewModel: VideoPagerViewModel by viewModels { viewModelFactory(this) }
    private val sharedEventViewModel: VideoSharedEventViewModel by activityViewModels { SharedEventViewModelFactory }
    lateinit var binding: VideoPagerFragmentBinding
    private lateinit var pagerAdapter: PagerAdapter
    private lateinit var commentFragment: CommentsFragment
    private var isUserLoggedIn = false
    private var isFirstVideoInfoLoaded = false
    private var selectedPlay = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            selectedPlay = arguments?.getInt(CategoryConstants.KEY_SelectedPlay)!!
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Listening user status via shared view Module from main activity preference utils
        registerSharedViewModel()

        viewModel.setVMContext(requireContext())
        viewModel.setPlayerVieww(appPlayerView.getPlayerView())

        binding = VideoPagerFragmentBinding.bind(view)
        // This single player view instance gets attached to the ViewHolder of the active ViewPager page
//        val appPlayerView = appPlayerViewFactory.create(view.context)
        pagerAdapter = PagerAdapter(imageLoader)
        binding.viewPager.adapter = pagerAdapter
        binding.viewPager.isUserInputEnabled = false
        binding.viewPager.offscreenPageLimit = 5 // Preload neighbouring page image previews
        commentFragment = CommentsFragment()


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
                    /*if (state.showPlayer) {
                        pagerAdapter.showPlayerFor(state.page)
                    }*/
                    if (state.showPlayer && state.youtubeUriError) {
                        pagerAdapter.showPlayerFor(state.page)
                        binding.progressBarVideoShorts.visibility = View.VISIBLE
                    } else if (state.showPlayer) {
                        pagerAdapter.showPlayerFor(state.page)
                        binding.progressBarVideoShorts.visibility = View.GONE
                    }
                    binding.viewPager.isUserInputEnabled = true

                    if (!isFirstVideoInfoLoaded) {
                        binding.viewPager.setCurrentItem(selectedPlay,true)
                        val data = pagerAdapter.getVideoData(selectedPlay)
                        viewModel.processEvent(VideoInfoEvent(data.id, selectedPlay))
                        isFirstVideoInfoLoaded = true
                    }
                }
            }

        val effects = viewModel.effects
            .onEach { effect ->
                when (effect) {
                    is BookmarkEffect -> {
                        pagerAdapter.refreshBookmarkUI(effect.position)
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
                    }
                    is FollowEffect -> {
//                        pagerAdapter.refreshFollowUI(effect.position)
                        sharedEventViewModel.followResponse(pagerAdapter.getVideoData(effect.position))
                    }
                    is PageEffect -> {
                        pagerAdapter.renderEffect(
                            binding.viewPager.currentItem,
                            effect
                        )
                    }
                    is PlayerErrorEffect -> {
                        Snackbar.make(
                            binding.root,
                            effect.throwable.message ?: "Error",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                    is GetVideoInfoEffect -> {
                        if(effect.videoInfo.id.isNotEmpty()) {
                            pagerAdapter.getInfoRefreshUI(effect.position)
                        }
                        sharedEventViewModel.shareVideoInfo(effect.videoInfo)
                    }
                    is GetYoutubeUriEffect -> {
                        // TODO, Nothing,
                    }

                    is PostCommentEffect -> {
                        pagerAdapter.getInfoRefreshUI(effect.position)
                        commentFragment.updateCommentAdapter(effect.data)
                    }
                    is YoutubeUriErrorEffect -> {
                        Log.i("", "")
                    }
                    is MediaItemTransitionEffect-> {
                        binding.viewPager.setCurrentItem(binding.viewPager.currentItem + 1)
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

        updateProgress()
    }

    override fun onResume() {
        super.onResume()

        if(pagerAdapter.itemCount > 0) {
            val data = pagerAdapter.getVideoData(binding.viewPager.currentItem)
            viewModel.processEvent(VideoInfoEvent(data.id, binding.viewPager.currentItem))
        }
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
//                sharedEventViewModel.shareVideoInfo(VideoInfoData())
                val data = pagerAdapter.getVideoData(currentItem)
                VideoInfoEvent(data.id, currentItem)
//                NoFurtherEvent
            },
            videoCache().map {
                var nextVideoCacheIndex = 0
                nextVideoCacheIndex = if (currentItem < pagerAdapter.itemCount - 1) {
                    currentItem + 1
                } else {
                    currentItem
                }
                val nextVideoCacheData = pagerAdapter.getVideoData(nextVideoCacheIndex)
                sharedEventViewModel.cacheVideoData(nextVideoCacheData.mediaUri, nextVideoCacheData.id)

                NoFurtherEvent
            },
            loadMoreVideoData().map {
                Log.i("VideoPreload", "")
                Log.i("VideoPreload", "########### NEW PAGE REQUEST IS SENT TO SERVER #################")
                Log.i("VideoPreload", "VideoFrom :: ${viewModel.videoFrom}, CategoryId :: ${viewModel.categoryId}, Page :: ${viewModel.page}")
                viewModel.processEvent(LoadVideoDataEvent(
                    categoryId = viewModel.categoryId,
                    videoFrom = viewModel.videoFrom,
                    page = viewModel.page,
                    perPage = VideoPagerViewModel.perPage
                ))
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
                FollowClick -> {
                    if (!isUserLoggedIn) {
                        sharedEventViewModel.launchLoginEvent(true)
                        NoFurtherEvent
                    } else {
                        val currentItem = binding.viewPager.currentItem
                        val videoData = pagerAdapter.getVideoData(currentItem)
                        FollowClickEvent(videoData.channel_id, currentItem)
                    }
                }
                ChannelClick -> {
                    val currentItem = binding.viewPager.currentItem
                    val videoData = pagerAdapter.getVideoData(currentItem)
                    ChannelClickEvent
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

                    if (!isUserLoggedIn) {
                        sharedEventViewModel.launchLoginEvent(true)
                        NoFurtherEvent
                    } else {
                        commentFragment.show(childFragmentManager, "comments")
                        val currentItem = binding.viewPager.currentItem
                        val videoData = pagerAdapter.getVideoData(currentItem)
                        CommentClickEvent(videoData.id, currentItem)
                    }
                }
                LikeClick -> {
                    if (!isUserLoggedIn) {
                        sharedEventViewModel.launchLoginEvent(true)
                        NoFurtherEvent
                    } else {
                        vibratePhone()
                        val currentItem = binding.viewPager.currentItem
                        val videoData = pagerAdapter.getVideoData(currentItem)
                        LikeClickEvent(videoData.id, currentItem)
                    }
                }
                BookmarkClick -> {
                    if (!isUserLoggedIn) {
                        sharedEventViewModel.launchLoginEvent(true)
                        NoFurtherEvent
                    } else {
                        vibratePhone()
                        val currentItem = binding.viewPager.currentItem
                        val videoData = pagerAdapter.getVideoData(currentItem)
                        BookmarkClickEvent(videoId = videoData.id, currentItem)
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
            sharedEventViewModel.cacheUserStatus.collectLatest {
                isUserLoggedIn = it.first
                Log.i("newDataUser","User logged in status ${it.first}")
            }
        }

        lifecycleScope.launch {
            sharedEventViewModel.followRequest.collectLatest {
                if (!isUserLoggedIn) {
                    sharedEventViewModel.launchLoginEvent(true)
                    NoFurtherEvent
                } else {
                    val currentItem = binding.viewPager.currentItem
                    val videoData = pagerAdapter.getVideoData(currentItem)
                    viewModel.processEvent(FollowClickEvent(videoData.channel_id, currentItem))
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
        val value = if (duration == C.TIME_UNSET || duration == 0L) 0 else (position * PROGRESS_BAR_MAX / duration)
        return value.toInt()
    }




    private val updateProgressAction = Runnable { updateProgress() }

    private fun updateProgress() {
        val player = viewModel.states.value.appPlayer?.player
        player?.let {
            val duration: Long = player.duration
            val bufferedPosition = player.bufferedPosition
            var position: Long = player.currentPosition
            binding.videoSeekbar.progress = progressBarValue(position, player)
            binding.videoSeekbar.secondaryProgress = progressBarValue(bufferedPosition, player)
        }
        handler.postDelayed(updateProgressAction, 100)

    }
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


    private val handler = Handler(Looper.myLooper()!!)
}
