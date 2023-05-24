package com.videopager.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.savedstate.SavedStateRegistryOwner
import androidx.viewpager2.widget.ViewPager2
import coil.ImageLoader
import com.google.android.material.snackbar.Snackbar
import com.player.ui.AppPlayerView
import com.videopager.R
import com.videopager.data.VideoInfoData
import com.videopager.databinding.VideoPagerFragmentBinding
import com.videopager.models.*
import com.videopager.ui.extensions.*
import com.videopager.ui.fragment.CommentsFragment
import com.videopager.utils.CategoryConstants
import com.videopager.vm.VideoSharedEventViewModel
import com.videopager.vm.SharedEventViewModelFactory
import com.videopager.vm.VideoPagerViewModel
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.setVMContext(requireContext())
        viewModel.setPlayerVieww(appPlayerView.getPlayerView())

        binding = VideoPagerFragmentBinding.bind(view)
        // This single player view instance gets attached to the ViewHolder of the active ViewPager page
//        val appPlayerView = appPlayerViewFactory.create(view.context)
        pagerAdapter = PagerAdapter(imageLoader)
        binding.viewPager.adapter = pagerAdapter
        binding.viewPager.isUserInputEnabled = false
        binding.viewPager.offscreenPageLimit = 1 // Preload neighbouring page image previews
        commentFragment = CommentsFragment()



        // Start point of Events, Flow, States
//        viewModel.initApi(shortsType)

        // Listening user status via shared view Module from main activity preference utils
        registerSharedViewModel()

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
                        sharedEventViewModel.shareVideoInfo(effect.videoInfo)
                        }
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
            },

            getYoutubeUri().map {
                var nextYoutubeUriPage = 0
                nextYoutubeUriPage = if (currentItem < pagerAdapter.itemCount - 1) {
                    currentItem + 1
                } else {
                    currentItem
                }
                val data = pagerAdapter.getVideoData(nextYoutubeUriPage)
                GetYoutubeUriEvent(data.type, nextYoutubeUriPage)
            },
            getYoutubeUri_2().map {
                var nextYoutubeUriPage = 0
                nextYoutubeUriPage = if (currentItem < pagerAdapter.itemCount - 2) {
                    currentItem + 2
                } else {
                    currentItem
                }
                val data = pagerAdapter.getVideoData(nextYoutubeUriPage)
                GetYoutubeUriEvent_2(data.type, nextYoutubeUriPage)
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
            }

            )
    }



    private fun CommentsFragment.postClickComment(): Flow<ViewEvent> {
        return clicks().map {
            PostClickCommentEvent(it.first, it.second, it.third)
        }
    }

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
                        val currentItem = binding.viewPager.currentItem
                        val videoData = pagerAdapter.getVideoData(currentItem)
                        BookmarkClickEvent(videoId = videoData.id, currentItem)
                    }
                }
                else -> NoFurtherEvent

            }

        }
    }

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
    }
}
