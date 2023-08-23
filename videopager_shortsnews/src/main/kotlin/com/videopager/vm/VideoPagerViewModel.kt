package com.videopager.vm

import android.content.Context
import android.util.Log
import androidx.lifecycle.viewModelScope
import at.huber.me.YouTubeUri
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.player.models.VideoData
import com.player.players.AppPlayer
import com.videopager.R
import com.videopager.data.VideoDataRepository
import com.videopager.models.*
import com.videopager.models.AnimationEffect
import com.videopager.models.AttachPlayerToViewResult
import com.videopager.models.CreatePlayerResult
import com.videopager.models.LoadVideoDataEvent
import com.videopager.models.LoadVideoDataResult
import com.videopager.models.OnNewPageSettledResult
import com.videopager.models.OnPageSettledEvent
import com.videopager.models.OnPlayerRenderingResult
import com.videopager.models.PauseVideoEvent
import com.videopager.models.PlayerErrorEffect
import com.videopager.models.PlayerErrorResult
import com.videopager.models.PlayerLifecycleEvent
import com.videopager.models.ResetAnimationsEffect
import com.videopager.models.TappedPlayerEvent
import com.videopager.models.TappedPlayerResult
import com.videopager.models.TearDownPlayerResult
import com.videopager.models.ViewEffect
import com.videopager.models.ViewEvent
import com.videopager.models.ViewResult
import com.videopager.ui.extensions.ViewState
import com.videopager.utils.CategoryConstants
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.*


/**
 * Owns a stateful [ViewState.appPlayer] instance that will get created and torn down in parallel
 * with Activity lifecycle state changes.
 */
internal class VideoPagerViewModel(
    private val repository: VideoDataRepository,
    private val appPlayerFactory: AppPlayer.Factory,
    private val handle: PlayerSavedStateHandle,
    initialState: ViewState,
    val categoryId: String, val videoFrom: String, val languages:String, private val selectedPlay:Int
) : MviViewModel<ViewEvent, ViewResult, ViewState, ViewEffect>(initialState) {

    companion object {
        const val perPage = 5
    }

    var page = 1
    private set

    private var _videoProgressBarEvent= MutableSharedFlow<Int>()
    val videoProgressBar = _videoProgressBarEvent.asSharedFlow()

    fun videoProgressBarEvent(playBackState: Int) {
        viewModelScope.launch {
            delay(1000)
            _videoProgressBarEvent.emit(playBackState)
        }
    }


    var context: Context? = null
    fun setVMContext(context: Context) {
        this.context = context
    }

    var playerView: StyledPlayerView? = null
    fun setPlayerVieww(playerView: StyledPlayerView?) {
        this.playerView = playerView
    }

    public override fun onStart() {
        processEvent(
            LoadVideoDataEvent(
                categoryId = categoryId,
                videoFrom = videoFrom,
                page = page,
                perPage = perPage,
                languages = languages

            )
        )
    }

    override fun Flow<ViewEvent>.toResults(): Flow<ViewResult> {
        // MVI boilerplate
        return merge(
            filterIsInstance<LoadVideoDataEvent>().toLoadVideoDataResults(),
//            filterIsInstance<InsertVideoEvent>().toInsertVideoDataResults(),
            filterIsInstance<PlayerLifecycleEvent>().toPlayerLifecycleResults(),
            filterIsInstance<TappedPlayerEvent>().toTappedPlayerResults(),
            filterIsInstance<OnPageSettledEvent>().toPageSettledResults(),
            filterIsInstance<PauseVideoEvent>().toPauseVideoResults(),
            filterIsInstance<FromNotificationInsertVideoEvent>().toFromNotificationVideoResults(),
            filterIsInstance<FollowClickEvent>().toFollowClickResults(),
            filterIsInstance<CommentClickEvent>().toCommentClickResults(),
            filterIsInstance<LikeClickEvent>().toLikeClickResults(),
            filterIsInstance<PostClickCommentEvent>().toPostCommentResults(),
            filterIsInstance<VideoInfoEvent>().toVideoInfoResults(),
            filterIsInstance<GetYoutubeUriEvent>().toYoutubeUriResults(),
            filterIsInstance<GetYoutubeUriEvent_2>().toYoutubeUriResults_2(),
            filterIsInstance<BookmarkClickEvent>().toSaveClickResult(),
//            filterIsInstance<NotificationVideoPlayerSettleEvent>().toNotificationClickResult(),
        )
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    private fun Flow<LoadVideoDataEvent>.toLoadVideoDataResults(): Flow<ViewResult> {
        return flatMapLatest { event ->
            repository.videoData(
                requiredId = event.categoryId,
                context = context!!,
                videoFrom = event.videoFrom,
                page = page,
                perPage = perPage,
                languages = languages
            )
        }.map { videoData ->
            if (page == 1) {
                delay(1000)
            }
            if(videoFrom == CategoryConstants.CHANNEL_VIDEO_DATA || videoFrom == CategoryConstants.BOOKMARK_VIDEO_DATA || videoFrom == CategoryConstants.NOTIFICATION_VIDEO_DATA) {
                states.value.page = selectedPlay
            }

            val appPlayer = states.value.appPlayer

            // If the player exists, it should be updated with the latest video data that came in
            var pageVideoData: MutableList<VideoData> = mutableListOf()
            if (states.value.videoData != null) {
                // It should add all existing videos in newly created collection
                pageVideoData.addAll(states.value.videoData!!)
            }
            pageVideoData.addAll(videoData)

            page++

            appPlayer?.setUpWith(pageVideoData, handle.get())
            // Capture any updated index so UI page state can stay in sync. For example, a video
            // may have been added to the page before the currently active one. That means the
            // the current video/page index will have changed
            val index = appPlayer?.currentPlayerState?.currentMediaItemIndex ?: 0
            LoadVideoDataResult(pageVideoData, index)
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
                is PlayerLifecycleEvent.Stop -> tearDownPlayer()
                is PlayerLifecycleEvent.Destroy -> tearDownPlayer()
            }
        }

        return merge(
            mapLatest { event -> AttachPlayerToViewResult(doAttach = event is PlayerLifecycleEvent.Start) },
            managePlayerInstance
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun createPlayer(): Flow<ViewResult> {

        check(states.value.appPlayer == null) { "Tried to create a player when one already exists" }

        val config = AppPlayer.Factory.Config(loopVideos = true)
        val appPlayer = appPlayerFactory.create(config, playerView!!)
        // If video data already exists then the player should have that video data set on it. This
        // can happen because the player has a lifecycle tied to Activity starting/stopping.
        states.value.videoData?.let { videoData -> appPlayer.setUpWith(videoData, handle.get()) }
        return merge(
            flowOf(CreatePlayerResult(appPlayer)),
            appPlayer.onPlayerRendering().map { OnPlayerRenderingResult },
            appPlayer.errors().mapLatest {
                if (it.message == "Source error") {
                    OnYoutubeUriErrorResult
                } else {
                    PlayerErrorResult(it)
                }
            },
//            appPlayer.onTracksChanged().mapLatest {
////                Toast.makeText(context, "Track is changed", Toast.LENGTH_SHORT).show()
//                NoOpResult
//            },
//            appPlayer.onTimelineChanged().mapLatest {
////                Toast.makeText(context, "Track is changed", Toast.LENGTH_SHORT).show()
//                NoOpResult
//            },
            appPlayer.onMediaItemTransition().mapLatest {
                MediaItemTransitionResult(it)
            },
            appPlayer.onPlaybackStateChanged().mapLatest {
                videoProgressBarEvent(it)
                NoOpResult
            }
        )
    }

    private fun tearDownPlayer(): Flow<ViewResult> {
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
        return flatMapLatest { event ->
            repository.follow(event.channelId, event.position)
        }.mapLatest { followResponse ->
            states.value.videoData?.get(followResponse.second)?.following =
                followResponse.first.data.following
            FollowClickResult(followResponse.second, following = followResponse.first)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun Flow<CommentClickEvent>.toCommentClickResults(): Flow<ViewResult> {
        return flatMapLatest { event ->
            states.value.videoData?.get(0)?.comment_count = "200k"
            repository.comment(event.videoId, event.position)
        }.mapLatest { commentResponse ->
            Triple(commentResponse.first, commentResponse.second, commentResponse.third)
        }.map {
            CommentClickResult(it.first, it.second, it.third)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun Flow<LikeClickEvent>.toLikeClickResults(): Flow<ViewResult> {
        return flatMapLatest { event ->
            repository.like(event.videoId, event.position)
        }.mapLatest { triple ->
            states.value.videoData?.get(triple.third)?.like_count = triple.first
            states.value.videoData?.get(triple.third)?.liking = triple.second
            triple.third
        }.map {
            LikeClickResult(it)
        }
    }

    private fun Flow<BookmarkClickEvent>.toSaveClickResult(): Flow<ViewResult> {
        return flatMapLatest { event ->
            repository.save(event.videoId, event.position)
        }.mapLatest { triple ->
            states.value.videoData?.get(triple.third)?.saveCount = triple.first
            states.value.videoData?.get(triple.third)?.saved = triple.second
            triple.third
        }.map {
            BookmarkClickResult(it)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun Flow<PostClickCommentEvent>.toPostCommentResults(): Flow<PostCommentResult> {
        return flatMapLatest { event ->
            repository.getPostComment(event.videoId, event.comment, event.position)
        }.mapLatest {
            Pair(it.first, it.second)
        }.map {
            PostCommentResult(it.first.data, it.second)
        }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    private fun Flow<OnPageSettledEvent>.toPageSettledResults(): Flow<ViewResult> {
        return mapLatest { event ->
            if(states.value.appPlayer == null) {
                NoOpResult
            } else {
                val appPlayer = requireNotNull(states.value.appPlayer)
                // To Play directly at specific index
                appPlayer.playMediaAt(event.page)
                OnNewPageSettledResult(page = event.page)
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun Flow<PauseVideoEvent>.toPauseVideoResults(): Flow<ViewResult> {
        return mapLatest {
            val appPlayer = requireNotNull(states.value.appPlayer)
            appPlayer.pause()

        }.mapLatest {
            NoOpResult
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun Flow<FromNotificationInsertVideoEvent>.toFromNotificationVideoResults(): Flow<ViewResult> {
        return map {event->
            if(states.value.appPlayer != null) {
                val appPlayer = requireNotNull(states.value.appPlayer)
                appPlayer.pause()
            }

            val videoData = VideoData(
                id = event.videoId, mediaUri = event.videoUrl, previewImageUri = event.previewUrl
            )
            var pageVideoData: MutableList<VideoData> = mutableListOf()
            // It should add all existing videos in newly created collection
            if (states.value.videoData != null) {
                // It should add all existing videos in newly created collection
                pageVideoData.addAll(states.value.videoData!!)
            }

            val appPlayer = states.value.appPlayer
            val index = appPlayer?.currentPlayerState?.currentMediaItemIndex ?: 0
            var upComingIndex = index
            if (upComingIndex !=0) {
                upComingIndex++
            }

            states.value.page = upComingIndex

            // Here, We are inserting new video data
            pageVideoData.add(upComingIndex, videoData)
            appPlayer?.setUpWith(pageVideoData, handle.get())
            FromNotificationInsertVideoDataResult(pageVideoData, upComingIndex )
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun Flow<VideoInfoEvent>.toVideoInfoResults(): Flow<GetVideoInfoResult> {
        return flatMapLatest { event ->
            repository.getVideoInfo(event.videoId, event.position)
        }.mapLatest {
            Pair(it.first, it.second)
        }.map {
            GetVideoInfoResult(it.second, it.first)
        }
    }

    private fun Flow<GetYoutubeUriEvent>.toYoutubeUriResults(): Flow<GetYoutubeUriResult> {
        return mapLatest { event ->
            getYoutubeUri(event.position)
        }.map {
            GetYoutubeUriResult(it.first, it.second)
        }
    }

    private fun Flow<GetYoutubeUriEvent_2>.toYoutubeUriResults_2(): Flow<GetYoutubeUriResult_2> {
        return mapLatest { event ->
            getYoutubeUri(event.position)
        }.map {
            GetYoutubeUriResult_2(it.first, it.second)
        }
    }


    private suspend fun getYoutubeUri(index: Int): Pair<String, String> =
        withContext(Dispatchers.IO) {
            var uri = ""
            var videoId = ""
            var requestedIndex = index
            var videoData = states.value.videoData?.get(requestedIndex)!!
            videoId = videoData.id
            uri = videoData.mediaUri
            if (videoData.type == "yt") {
                val youTubeUri = YouTubeUri(context)
                var ytFiles = youTubeUri.getStreamUrls(videoData.mediaUri)

                if (ytFiles != null && ytFiles.indexOfKey(YouTubeUri.iTag) >= 0) {
                    uri = ytFiles[YouTubeUri.iTag].url
                    videoData.mediaUri = uri
                    videoData.type = "converted"
                    setUpWithPlayer()
                }
            }
            return@withContext Pair(uri, videoId)


        }

    @OptIn(DelicateCoroutinesApi::class)
    fun setUpWithPlayer() {
        GlobalScope.launch(Dispatchers.Main) {
            val appPlayer = states.value.appPlayer
            // If the player exists, it should be updated with the latest video data that came in
            appPlayer?.setUpWith(states.value.videoData!!, handle.get())
        }
    }

    override fun ViewResult.reduce(state: ViewState): ViewState {
        // MVI reducer boilerplate
        return when (this) {
            is LoadVideoDataResult -> {
                state.copy(videoData = videoData, page = currentMediaItemIndex)
            }
            is FromNotificationInsertVideoDataResult -> {
                state.copy(videoData = videoData, page = currentMediaItemIndex)
            }
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
            filterIsInstance<MediaItemTransitionResult>().toMediaItemTransitionEffects(),
//            filterIsInstance<OnYoutubeUriErrorResult>().toYoutubeUriEffects(),
            filterIsInstance<FollowClickResult>().toFollowViewEffect(),
            filterIsInstance<LikeClickResult>().toLikeViewEffect(),
            filterIsInstance<CommentClickResult>().toCommentViewEffect(),
            filterIsInstance<GetVideoInfoResult>().toGetVideoInfoEffect(),
            filterIsInstance<PostCommentResult>().toPostCommentEffect(),
            filterIsInstance<GetYoutubeUriResult>().toGetYoutubeUriEffect(),
            filterIsInstance<GetYoutubeUriResult_2>().toGetYoutubeUriEffect_2(),
            filterIsInstance<BookmarkClickResult>().toSaveViewEffect(),

            )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun Flow<TappedPlayerResult>.toTappedPlayerEffects(): Flow<ViewEffect> {
        return mapLatest { result ->
            AnimationEffect(result.drawable)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun Flow<OnNewPageSettledResult>.toNewPageSettledEffects(): Flow<ViewEffect> {
        return mapLatest { ResetAnimationsEffect }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun Flow<LikeClickResult>.toLikeViewEffect(): Flow<ViewEffect> {
        return mapLatest { result -> LikeEffect(result.position) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun Flow<BookmarkClickResult>.toSaveViewEffect(): Flow<ViewEffect> {
        return mapLatest { result -> BookmarkEffect(result.position) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun Flow<FollowClickResult>.toFollowViewEffect(): Flow<ViewEffect> {
        return mapLatest { result -> FollowEffect(result.position, result.following) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun Flow<CommentClickResult>.toCommentViewEffect(): Flow<ViewEffect> {
        return mapLatest { result ->
            CommentEffect(
                result.videoId,
                result.comments.data,
                result.position
            )
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun Flow<PostCommentResult>.toPostCommentEffect(): Flow<ViewEffect> {
        return mapLatest { result ->
            states.value.videoData?.get(states.value.page)?.comment_count =
                result.response.comment_count
            PostCommentEffect(result.response, result.position)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun Flow<GetYoutubeUriResult>.toGetYoutubeUriEffect(): Flow<GetYoutubeUriEffect> {
        return mapLatest {
            GetYoutubeUriEffect(it.uri, it.id)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun Flow<GetYoutubeUriResult_2>.toGetYoutubeUriEffect_2(): Flow<GetYoutubeUriEffect> {
        return mapLatest {
            GetYoutubeUriEffect(it.uri, it.id)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun Flow<GetVideoInfoResult>.toGetVideoInfoEffect(): Flow<ViewEffect> {
        return mapLatest { result ->
            Log.i("kamlesh", "Video info data $result")
            states.value.videoData?.get(result.position)?.apply {
                this.comment_count = result.response.comment_count
                this.like_count = result.response.like_count
                this.saveCount = result.response.saved_count
                this.following = result.response.following
                this.saved = result.response.saved
                this.liking = result.response.liked
                this.channel_image = result.response.channel_image
                this.channel_id = result.response.channel_id
                this.title = result.response.title
            }
            GetVideoInfoEffect(result.response, result.position)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun Flow<PlayerErrorResult>.toPlayerErrorEffects(): Flow<ViewEffect> {
        return mapLatest { result -> PlayerErrorEffect(result.throwable) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun Flow<MediaItemTransitionResult>.toMediaItemTransitionEffects(): Flow<ViewEffect> {
        return mapLatest { result -> MediaItemTransitionEffect(result) }
    }

}
