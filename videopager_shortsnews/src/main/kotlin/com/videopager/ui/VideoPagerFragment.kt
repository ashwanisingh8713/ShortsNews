package com.videopager.ui

import android.os.Bundle
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
import com.videopager.R
import com.videopager.databinding.VideoPagerFragmentBinding
import com.videopager.models.*
import com.videopager.models.OnPageSettledEvent
import com.videopager.models.PageEffect
import com.videopager.models.PauseVideoEvent
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
    private lateinit var adapter: PagerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = VideoPagerFragmentBinding.bind(view)
        // This single player view instance gets attached to the ViewHolder of the active ViewPager page
        val appPlayerView = appPlayerViewFactory.create(view.context)
        adapter = PagerAdapter(imageLoader)
        binding.viewPager.adapter = adapter
        binding.viewPager.offscreenPageLimit = 1 // Preload neighbouring page image previews
        binding.viewPager.isUserInputEnabled = false

        // Start point of Events, Flow, States
        viewModel.initApi(shortsType)

        val states = viewModel.states
            .onEach { state ->
                // Await the list submission so that the adapter list is in sync with state.videoData
                adapter.awaitList(state.videoData)

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
                if (adapter.currentList.isNotEmpty()) {
                    // Set the player view on the active page. Note that ExoPlayer won't render
                    // any frames until the output view (here, appPlayerView) is on-screen
                    adapter.attachPlayerView(appPlayerView, state.page)

                    // If the player media is rendering frames, then show the player
                    if (state.showPlayer) {
                        adapter.showPlayerFor(state.page)
                        binding.viewPager.isUserInputEnabled = true
                        binding.progressBarVideoShorts.visibility = View.GONE
                    }
                }
            }

        val effects = viewModel.effects
            .onEach { effect ->
                when (effect) {
                    is SaveEffect -> adapter.refreshUI()
                    is CommentEffect -> adapter.refreshUI()
                    is LikeEffect -> adapter.refreshUI()
                    is FollowEffect -> adapter.refreshUI()
                    is PageEffect -> adapter.renderEffect(binding.viewPager.currentItem, effect)
                    is PlayerErrorEffect -> Snackbar.make(
                        binding.root,
                        effect.throwable.message ?: "Error",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }

        val events = merge(
            viewLifecycleOwner.lifecycle.viewEvents(),
            binding.viewPager.viewEvents(),
            adapter.clicksEvent(),
        ).onEach(viewModel::processEvent)

        merge(states, effects, events)
            .launchIn(viewLifecycleOwner.lifecycleScope)

    }

    private fun Lifecycle.viewEvents(): Flow<ViewEvent> {
        return events()
            .filter { event -> event == Lifecycle.Event.ON_START || event == Lifecycle.Event.ON_STOP
                    || event == Lifecycle.Event.ON_DESTROY}
            .map { event ->
                // Fragment starting or stopping is a signal to create or tear down the player, respectively.
                // The player should not be torn down across config changes, however.
                when (event) {
                    Lifecycle.Event.ON_START -> PlayerLifecycleEvent.Start
                    Lifecycle.Event.ON_STOP -> PlayerLifecycleEvent.Stop(requireActivity().isChangingConfigurations)
                    Lifecycle.Event.ON_DESTROY -> PlayerLifecycleEvent.Destroy(requireActivity().isChangingConfigurations)
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
                Toast.makeText(requireContext(), "$currentItem", Toast.LENGTH_SHORT).show()
                PauseVideoEvent
            }
        )
    }

    private fun PagerAdapter.clicksEvent(): Flow<ViewEvent> {
        return clicks().map {
            when(it.second) {
                PlayPauseClick -> TappedPlayerEvent
                FollowClick -> {
                    // TODO, make api request to follow/unfollow the channel
                    val currentItem = binding.viewPager.currentItem
                    val videoData = adapter.getVideoData(currentItem)
                    FollowClickEvent(videoData.id, currentItem)
                }
                ChannelClick -> {
                    val currentItem = binding.viewPager.currentItem
                    val videoData = adapter.getVideoData(currentItem)
                    // TODO, Redirect in Channel page
                    ChannelClickEvent
                }
                SaveClick -> {
                    // TODO, Not applicable for Phase-1, as discussed on 24th April
                    Toast.makeText(requireContext(), "In Phase - 2", Toast.LENGTH_SHORT).show()
                    SaveClickEvent("", 101010)
                }
                ShareClick -> {
                    // TODO, Sharing
                    ShareClickEvent
                }
                CommentClick -> {
                    // TODO, Open BottomSheet dialog, inside BottomSheet dialog make api request and show the content.
                    val currentItem = binding.viewPager.currentItem
                    val videoData = adapter.getVideoData(currentItem)
                    CommentClickEvent(videoData.id, currentItem)
                }
                LikeClick -> {
                    // TODO, make api request to like/unlike the channel
                    val currentItem = binding.viewPager.currentItem
                    val videoData = adapter.getVideoData(currentItem)
                    LikeClickEvent(videoData.id, currentItem)
                }
                else -> NoFurtherEvent

            }

        }
    }







}
