package com.videopager.vm

import android.content.Context
import android.util.Log
import android.util.SparseArray
import at.huber.youtubeExtractor.VideoMeta
import at.huber.youtubeExtractor.YouTubeExtractor
import at.huber.youtubeExtractor.YtFile
import com.github.kotvertolet.youtubejextractor.JExtractorCallback
import com.github.kotvertolet.youtubejextractor.YoutubeJExtractor
import com.github.kotvertolet.youtubejextractor.exception.YoutubeRequestException
import com.github.kotvertolet.youtubejextractor.models.newModels.VideoPlayerConfig
import com.player.players.AppPlayer
import com.videopager.R
import com.videopager.data.VideoDataRepository
import com.videopager.models.*
import com.videopager.ui.extensions.ViewState
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*


/**
 * Owns a stateful [ViewState.appPlayer] instance that will get created and torn down in parallel
 * with Activity lifecycle state changes.
 */
internal class VideoPagerViewModel(
    private val repository: VideoDataRepository,
    private val appPlayerFactory: AppPlayer.Factory,
    private val handle: PlayerSavedStateHandle,
    initialState: ViewState
) : MviViewModel<ViewEvent, ViewResult, ViewState, ViewEffect>(initialState) {

    var context: Context? = null
    fun setVMContext(context: Context) {
        this.context = context
    }

    override fun onStart() {
        processEvent(LoadVideoDataEvent)
    }

    override fun Flow<ViewEvent>.toResults(requestType: String): Flow<ViewResult> {
        // MVI boilerplate
        return merge(
            filterIsInstance<LoadVideoDataEvent>().toLoadVideoDataResults(requestType),
            filterIsInstance<PlayerLifecycleEvent>().toPlayerLifecycleResults(),
            filterIsInstance<TappedPlayerEvent>().toTappedPlayerResults(),
            filterIsInstance<OnPageSettledEvent>().toPageSettledResults(),
            filterIsInstance<PauseVideoEvent>().toPauseVideoResults(),
            filterIsInstance<FollowClickEvent>().toFollowClickResults(),
            filterIsInstance<CommentClickEvent>().toCommentClickResults(),
            filterIsInstance<LikeClickEvent>().toLikeClickResults()
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun Flow<LoadVideoDataEvent>.toLoadVideoDataResults(requestType: String): Flow<ViewResult> {
        return flatMapLatest { repository.videoData(requestType) }
            .map { videoData ->
                val appPlayer = states.value.appPlayer
                // If the player exists, it should be updated with the latest video data that came in
                appPlayer?.setUpWith(videoData, handle.get())
                // Capture any updated index so UI page state can stay in sync. For example, a video
                // may have been added to the page before the currently active one. That means the
                // the current video/page index will have changed
                val index = appPlayer?.currentPlayerState?.currentMediaItemIndex ?: 0
                LoadVideoDataResult(videoData, index)
            }
    }

    /**
     * This is a single flow instead of two distinct ones (e.g. one for starting, one for stopping)
     * so that when the PlayerLifecycleEvent type changes from upstream, the flow initiated by the
     * previous type gets unsubscribed from (see: [flatMapLatest]). This is necessary to cancel flows
     * tied to the AppPlayer instance, e.g. [AppPlayer.onPlayerRendering], when the player is being
     * torn down.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun Flow<PlayerLifecycleEvent>.toPlayerLifecycleResults(): Flow<ViewResult> {
        val managePlayerInstance = filterNot { event ->
            // Don't need to create a player when one already exists. This can happen
            // after a configuration change
            states.value.appPlayer != null && event is PlayerLifecycleEvent.Start
                // Don't tear down the player across configuration changes
                || event is PlayerLifecycleEvent.Stop && event.isChangingConfigurations
        }.flatMapLatest { event ->
            when (event) {
                is PlayerLifecycleEvent.Start -> createPlayer()
                is PlayerLifecycleEvent.Stop -> pausePlayer()
                is PlayerLifecycleEvent.Destroy -> tearDownPlayer()
            }
        }

        return merge(
            mapLatest { event -> AttachPlayerToViewResult(doAttach = event is PlayerLifecycleEvent.Start) },
            managePlayerInstance
        )
    }

    private suspend fun createPlayer(): Flow<ViewResult> {
        check(states.value.appPlayer == null) { "Tried to create a player when one already exists" }
        val config = AppPlayer.Factory.Config(loopVideos = true)
        val appPlayer = appPlayerFactory.create(config)
        // If video data already exists then the player should have that video data set on it. This
        // can happen because the player has a lifecycle tied to Activity starting/stopping.
        states.value.videoData?.let { videoData -> appPlayer.setUpWith(videoData, handle.get()) }
        return merge(
            flowOf(CreatePlayerResult(appPlayer)),
            appPlayer.onPlayerRendering().map { OnPlayerRenderingResult },
            appPlayer.errors().mapLatest {
                if(it.message == "Source error") {
                    OnYoutubeUriErrorResult
//                    OnPlayerRenderingResult
                } else {
                    PlayerErrorResult(it)
                }
            }
        )
    }

     fun tearDownPlayer(): Flow<ViewResult> {
        val appPlayer = requireNotNull(states.value.appPlayer)
        // Keep track of player state so that it can be restored across player recreations.
        handle.set(appPlayer.currentPlayerState)
        // Videos are a heavy resource, so tear player down when the app is not in the foreground.
        appPlayer.release()
        return flowOf(TearDownPlayerResult)
    }

    // Ashwani
    fun pausePlayer(): Flow<ViewResult> {
        val appPlayer = requireNotNull(states.value.appPlayer)
        // Keep track of player state so that it can be restored across player recreations.
        handle.set(appPlayer.currentPlayerState)
        // Videos are a heavy resource, so tear player down when the app is not in the foreground.
        appPlayer.pause()
        return flowOf(TappedPlayerResult(R.drawable.pause))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun Flow<TappedPlayerEvent>.toTappedPlayerResults(): Flow<ViewResult> {
        return mapLatest {
            val appPlayer = requireNotNull(states.value.appPlayer)
            val drawable = if (appPlayer.currentPlayerState.isPlaying) {
                appPlayer.pause()
                R.drawable.pause
            } else {
                appPlayer.play()
                R.drawable.play
            }

            TappedPlayerResult(drawable)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun Flow<FollowClickEvent>.toFollowClickResults(): Flow<ViewResult> {
        return flatMapLatest{ event ->
            states.value.videoData?.get(0)?.comment_count  = "dfdf"
            repository.follow(event.videoId)
        }.mapLatest { followResponse ->
            FollowClickResult(0)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun Flow<CommentClickEvent>.toCommentClickResults(): Flow<ViewResult> {
        return flatMapLatest{ event ->
            states.value.videoData?.get(0)?.comment_count  = "200k"
            repository.comment(event.videoId)
        }.mapLatest { commentResponse ->
            CommentClickResult(0)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun Flow<LikeClickEvent>.toLikeClickResults(): Flow<ViewResult> {
        return flatMapLatest{ event ->
            states.value.videoData?.get(0)?.like_count  = "100k"
            repository.like(event.videoId)
        }.mapLatest { likeResponse ->
            LikeClickResult(0)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun Flow<OnPageSettledEvent>.toPageSettledResults(): Flow<ViewResult> {
        return mapLatest { event ->
            val appPlayer = requireNotNull(states.value.appPlayer)
            appPlayer.playMediaAt(event.page)
            OnNewPageSettledResult(page = event.page)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun Flow<PauseVideoEvent>.toPauseVideoResults(): Flow<ViewResult> {
        return mapLatest {
            val appPlayer = requireNotNull(states.value.appPlayer)
            appPlayer.pause()
        }.flatMapLatest {
            repository.getVideoInfo("")
        }.mapLatest {
            GetVideoInfoResult(0, "")
        }
    }

    override fun ViewResult.reduce(state: ViewState): ViewState {
        // MVI reducer boilerplate
        return when (this) {
            is LoadVideoDataResult -> state.copy(videoData = videoData, page = currentMediaItemIndex)
            is CreatePlayerResult -> state.copy(appPlayer = appPlayer)
            is TearDownPlayerResult -> state.copy(appPlayer = null)
            is OnNewPageSettledResult -> state.copy(page = page, showPlayer = false)
            is OnPlayerRenderingResult -> state.copy(showPlayer = true, youtubeUriError = false)
            is OnYoutubeUriErrorResult -> state.copy(showPlayer = true, youtubeUriError = true)
            is AttachPlayerToViewResult -> state.copy(attachPlayer = doAttach)
            else -> state
        }
    }

    override fun Flow<ViewResult>.toEffects(): Flow<ViewEffect> {
        return merge(
            filterIsInstance<TappedPlayerResult>().toTappedPlayerEffects(),
            filterIsInstance<OnNewPageSettledResult>().toNewPageSettledEffects(),
            filterIsInstance<PlayerErrorResult>().toPlayerErrorEffects(),
            filterIsInstance<OnYoutubeUriErrorResult>().toYoutubeUriEffects(),
            filterIsInstance<FollowClickResult>().toFollowViewEffect(),
            filterIsInstance<CommentClickResult>().toCommentViewEffect(),
            filterIsInstance<GetVideoInfoResult>().toGetVideoInfoEffect()
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun Flow<TappedPlayerResult>.toTappedPlayerEffects(): Flow<ViewEffect> {
        return mapLatest {
                result -> AnimationEffect(result.drawable)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun Flow<OnNewPageSettledResult>.toNewPageSettledEffects(): Flow<ViewEffect> {
        return mapLatest { ResetAnimationsEffect }
    }
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun Flow<FollowClickResult>.toFollowViewEffect(): Flow<ViewEffect> {
        return mapLatest { FollowEffect }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun Flow<CommentClickResult>.toCommentViewEffect(): Flow<ViewEffect> {
        return mapLatest { CommentEffect }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun Flow<GetVideoInfoResult>.toGetVideoInfoEffect(): Flow<ViewEffect> {
        return mapLatest {
            GetVideoInfoEffect }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun Flow<PlayerErrorResult>.toPlayerErrorEffects(): Flow<ViewEffect> {
        return mapLatest { result -> PlayerErrorEffect(result.throwable) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun Flow<OnYoutubeUriErrorResult>.toYoutubeUriEffects(): Flow<ViewEffect> {
//        return mapLatest {YoutubeUriErrorEffect }
        return youtubeExtractor_().mapLatest {
            val appPlayer = states.value.appPlayer
            // If the player exists, it should be updated with the latest video data that came in
            states.value.videoData?.let {
                appPlayer?.setUpWith(it, handle.get())
            }
            // Capture any updated index so UI page state can stay in sync. For example, a video
            // may have been added to the page before the currently active one. That means the
            // the current video/page index will have changed
//            val index = appPlayer?.currentPlayerState?.currentMediaItemIndex ?: 0
//            states.value.videoData?.let { LoadVideoDataResult(it, index) }
            YoutubeUriErrorEffect
        }
    }

    private fun youtubeExtractor():Flow<String> = callbackFlow {
        GlobalScope.launch(Dispatchers.IO) {
            delay(3000)
            val youtubeJExtractor = YoutubeJExtractor()
            var listener = object : JExtractorCallback {

                override fun onSuccess(videoData: VideoPlayerConfig?) {
                    val finalUri = videoData?.streamingData?.muxedStreams?.get(0)?.url!!
                    val indexx = states.value.page
                    states.value.videoData?.get(indexx)?.mediaUri = finalUri
                    trySend("")
                }

                override fun onNetworkException(e: YoutubeRequestException) {
                    // may be a connection problem, ask user to check his internet connection
                    Log.i("", "")
                }

                override fun onError(exception: Exception) {
                    // some serious problem occured, just show some error message
                    Log.i("", "")
                }
            }
//            val youtubeUri = states.value.videoData?.get(states.value.page)?.mediaUri

            youtubeJExtractor.extract("CuhoU0pmip8", listener)


        }
        awaitClose {  }

    }


    fun youtubeExtractor_():Flow<String> = callbackFlow {
        GlobalScope.launch(Dispatchers.IO) {
            delay(3000)
            val listener = object : YouTubeExtractor(context!!) {
                override fun onExtractionComplete(
                    ytFiles: SparseArray<YtFile>?,
                    vMeta: VideoMeta?
                ) {
                    if (ytFiles != null) {
                        val itag = 18
                        val finalUri = ytFiles[itag].url

                        val indexx = states.value.page
                        states.value.videoData?.get(indexx)?.mediaUri = finalUri
                        trySend("")
                    }
                }
            }.extract(states.value.videoData?.get(states.value.page)?.mediaUri)
        }
        awaitClose {  }
    }



}
