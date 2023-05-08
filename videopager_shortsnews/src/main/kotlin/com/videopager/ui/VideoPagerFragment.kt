package com.videopager.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
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
import com.videopager.ui.fragment.CommentsFragment
import com.videopager.R
import com.videopager.databinding.VideoPagerFragmentBinding
import com.videopager.models.*
import com.videopager.models.OnPageSettledEvent
import com.videopager.models.PageEffect
import com.videopager.models.PlayerErrorEffect
import com.videopager.models.PlayerLifecycleEvent
import com.videopager.models.ViewEvent
import com.videopager.ui.extensions.*
import com.videopager.ui.extensions.awaitList
import com.videopager.ui.extensions.events
import com.videopager.ui.extensions.isIdle
import com.videopager.ui.extensions.pageChanges
import com.videopager.ui.extensions.pageIdlings
import com.videopager.vm.SharedEventViewModel
import com.videopager.vm.SharedEventViewModelFactory
import com.videopager.vm.VideoPagerViewModel
import kotlinx.coroutines.flow.*

class VideoPagerFragment(
    private val viewModelFactory: (SavedStateRegistryOwner) -> ViewModelProvider.Factory,
    private val appPlayerViewFactory: AppPlayerView.Factory,
    private val imageLoader: ImageLoader, private val shortsType: String
) : Fragment(R.layout.video_pager_fragment) {
    private val viewModel: VideoPagerViewModel by viewModels { viewModelFactory(this) }
    private val sharedEventViewModel: SharedEventViewModel by activityViewModels { SharedEventViewModelFactory }
    lateinit var binding: VideoPagerFragmentBinding
    private lateinit var pagerAdapter: PagerAdapter
    private lateinit var commentFragment: CommentsFragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.setVMContext(requireContext())

        binding = VideoPagerFragmentBinding.bind(view)
        // This single player view instance gets attached to the ViewHolder of the active ViewPager page
        val appPlayerView = appPlayerViewFactory.create(view.context)
        pagerAdapter = PagerAdapter(imageLoader)
        binding.viewPager.adapter = pagerAdapter
        binding.viewPager.isUserInputEnabled = false
        binding.viewPager.offscreenPageLimit = 1 // Preload neighbouring page image previews
        commentFragment = CommentsFragment()

        // Start point of Events, Flow, States
        viewModel.initApi(shortsType)


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
                }
            }

        val effects = viewModel.effects
            .onEach { effect ->
                when (effect) {
                    is SaveEffect -> pagerAdapter.refreshUI(0)
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
                        pagerAdapter.refreshUI(effect.position)
                    }
                    is FollowEffect -> pagerAdapter.refreshUI(effect.position)
                    is PageEffect -> pagerAdapter.renderEffect(
                        binding.viewPager.currentItem,
                        effect
                    )
                    is PlayerErrorEffect -> Snackbar.make(
                        binding.root,
                        effect.throwable.message ?: "Error",
                        Snackbar.LENGTH_LONG
                    ).show()
                    is GetVideoInfoEffect -> {
                        pagerAdapter.refreshUI(effect.position)
                    }
                    is GetYoutubeUriEffect ->{
                        sharedEventViewModel.cacheVideoData(effect.uri, effect.id)
                    }

                    is PostCommentEffect -> {
                        pagerAdapter.refreshUI(effect.position)
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

            getYoutubeUri().map {
                var nextYoutubeUriPage = 0
                nextYoutubeUriPage = if(currentItem < pagerAdapter.itemCount-1)  {
                    currentItem+1
                } else {
                    currentItem
                }
                val data = pagerAdapter.getVideoData(nextYoutubeUriPage)
                GetYoutubeUriEvent(data.type, nextYoutubeUriPage)
            },
            getYoutubeUri_2().map {
                var nextYoutubeUriPage = 0
                nextYoutubeUriPage = if(currentItem < pagerAdapter.itemCount-2)  {
                    currentItem+2
                } else {
                    currentItem
                }
                val data = pagerAdapter.getVideoData(nextYoutubeUriPage)
                GetYoutubeUriEvent_2(data.type, nextYoutubeUriPage)
            },

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
                    // TODO, make api request to follow/unfollow the channel
                    val currentItem = binding.viewPager.currentItem
                    val videoData = pagerAdapter.getVideoData(currentItem)
                    FollowClickEvent(videoData.id, currentItem)
                }
                ChannelClick -> {
                    val currentItem = binding.viewPager.currentItem
                    val videoData = pagerAdapter.getVideoData(currentItem)
                    // TODO, Redirect in Channel page
                    ChannelClickEvent
                }
                SaveClick -> {
                    // TODO, Not applicable for Phase-1, as discussed on 24th April
                    Toast.makeText(requireContext(), "In Phase - 2", Toast.LENGTH_SHORT).show()
                    SaveClickEvent("", 101010)
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
                    activity?.startActivity(Intent.createChooser(chooserIntent,"Share Via"))
                    ShareClickEvent
                }
                CommentClick -> {
                    // TODO, Open BottomSheet dialog, inside BottomSheet dialog make api request and show the content.

                    commentFragment.show(childFragmentManager, "comments")

                    val currentItem = binding.viewPager.currentItem
                    val videoData = pagerAdapter.getVideoData(currentItem)
                    CommentClickEvent(videoData.id, currentItem)
                }
                LikeClick -> {
                    // TODO, make api request to like/unlike the channel
                    val currentItem = binding.viewPager.currentItem
                    val videoData = pagerAdapter.getVideoData(currentItem)
                    LikeClickEvent(videoData.id, currentItem)
                }
                else -> NoFurtherEvent

            }

        }
    }


}
