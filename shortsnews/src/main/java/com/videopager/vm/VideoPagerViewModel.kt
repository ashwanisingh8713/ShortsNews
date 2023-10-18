package com.videopager.vm

import android.content.Context
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.player.models.VideoData
import com.player.players.AppPlayer
import com.ns.shortsnews.R
import com.ns.shortsnews.data.source.UserApiService
import com.ns.shortsnews.domain.cache.CachingLinkedHashMap
import com.videopager.data.VideoDataRepository
import com.videopager.data.VideoInfo
import com.videopager.data.VideoInfoData
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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.*


/**
 * Owns a stateful [ViewState.appPlayer] instance that will get created and torn down in parallel
 * with Activity lifecycle state changes.
 */
internal class VideoPagerViewModel(
    private val userApiService: UserApiService,
    private val repository: VideoDataRepository,
    private val appPlayerFactory: AppPlayer.Factory,
    private val handle: PlayerSavedStateHandle,
    initialState: ViewState,
    val categoryId: String, val videoFrom: String, val languages:String, private val selectedPlay:Int,
    private val loadedVideoData: List<VideoData>
) : MviViewModel<ViewEvent, ViewResult, ViewState, ViewEffect>(initialState) {



//    var page = 1
//    private set

//    private var pager: ViewPager2? = null
//    private fun setPager(pager: ViewPager2) {
//        this.pager = pager
//    }

    private var _videoProgressBarEvent= MutableSharedFlow<Int>()
    val videoProgressBar = _videoProgressBarEvent.asSharedFlow()

    fun videoProgressBarEvent(playBackState: Int) {
        viewModelScope.launch {
            Log.d("AshwaniPerformance", "VideoPagerViewModel playBackState :: $playBackState")
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
        if(loadedVideoData.isNotEmpty()) {
            processEvent(
                PreLoadedVideoDataEvent()
            )
        } else {
            processEvent(
                LoadVideoDataEvent(
                    categoryId = categoryId,
                    videoFrom = videoFrom,
                    page = 1,
                    perPage = perPage,
                    languages = languages

                )
            )
        }
    }

    override fun Flow<ViewEvent>.toResults(): Flow<ViewResult> {
        // MVI boilerplate
        return merge(
            filterIsInstance<LoadVideoDataEvent>().toLoadVideoDataResults(),
            filterIsInstance<PreLoadedVideoDataEvent>().toPreLoadedVideoDataResults(),
            filterIsInstance<PlayerLifecycleEvent>().toPlayerLifecycleResults(),
            filterIsInstance<TappedPlayerEvent>().toTappedPlayerResults(),
            filterIsInstance<OnPageSettledEvent>().toPageSettledResults(),
            filterIsInstance<PauseVideoEvent>().toPauseVideoResults(),
            filterIsInstance<CommentClickEvent>().toCommentClickResults(),
            filterIsInstance<LikeClickEvent>().toLikeClickResults(),
            filterIsInstance<PostClickCommentEvent>().toPostCommentResults(),
            filterIsInstance<VideoInfoEvent>().toVideoInfoResults(),
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
                page = event.page,
                perPage = perPage,
                languages = languages
            )
        }.map { videoData ->
            /*if (page == 1) {
                delay(1000)
            }*/
            val appPlayer = states.value.appPlayer

            // If the player exists, it should be updated with the latest video data that came in
            var pageVideoData: MutableList<VideoData> = mutableListOf()
            if (states.value.videoData != null) {
                // It should add all existing videos in newly created collection
                pageVideoData.addAll(states.value.videoData!!)
            }
            pageVideoData.addAll(videoData)

//            Log.i("AshwaniXYZ", "<LoadVideoDataEvent> Index :: ${index}")
            appPlayer?.setUpWith(pageVideoData)

            // Capture any updated index so UI page state can stay in sync. For example, a video
            // may have been added to the page before the currently active one. That means the
            // the current video/page index will have changed
//            val index = handle.get()?.currentMediaItemIndex ?: appPlayer?.currentPlayerState?.currentMediaItemIndex ?: 0
            var index = 0

            if(appPlayer?.player == null) {
                index = appPlayer?.player?.currentMediaItemIndex!!
                Log.i("AshwaniXYZ", "<LoadVideoDataEvent> appPlayer?.player is NOT NULL so index = $index")
            } else {
                index = appPlayer?.player?.currentMediaItemIndex ?: 0
                Log.i("AshwaniXYZ", "<LoadVideoDataEvent> appPlayer?.player is NULL so index = $index")
            }

//
            LoadVideoDataResult(pageVideoData, index)
        }
    }


    private fun Flow<PreLoadedVideoDataEvent>.toPreLoadedVideoDataResults(): Flow<ViewResult> {
        return map {
            if(states.value.appPlayer != null) {
                val appPlayer = requireNotNull(states.value.appPlayer)
                handle.set(appPlayer.currentPlayerState)
            }
            loadedVideoData
        }.map { videoData ->
//            delay(1000)
//            states.value.page = selectedPlay
            val appPlayer = states.value.appPlayer
            // If the player exists, it should be updated with the latest video data that came in
            var pageVideoData: MutableList<VideoData> = mutableListOf()
            pageVideoData.addAll(videoData)

//            page = videoData[videoData.size-1].page++

            val ss = handle.get()
            ss?.currentMediaItemIndex = selectedPlay
            ss?.currentMediaItemId = pageVideoData.get(selectedPlay).id

            appPlayer?.setUpWith(pageVideoData)
            // Capture any updated index so UI page state can stay in sync. For example, a video
            // may have been added to the page before the currently active one. That means the
            // the current video/page index will have changed
//            val index = appPlayer?.currentPlayerState?.currentMediaItemIndex ?: 0
//            val index = appPlayer?.player?.currentMediaItemIndex ?: 0
            Log.i("AshwaniXYZ", "<PreLoadedVideoDataEvent> Index :: ${selectedPlay}")
            LoadVideoDataResult(pageVideoData, selectedPlay)
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
                is PlayerLifecycleEvent.Start -> {
                    if (states.value.showPlayer) {
                        resumePlayer()
                    } else {
                        createPlayer()
                    }
                }
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
        Log.i("AshwaniXYZ", "createPlayer() ")
        check(states.value.appPlayer == null) { "Tried to create a player when one already exists" }

        val config = AppPlayer.Factory.Config(loopVideos = true)
        val appPlayer = appPlayerFactory.create(config, playerView!!)
        // If video data already exists then the player should have that video data set on it. This
        // can happen because the player has a lifecycle tied to Activity starting/stopping.

        states.value.videoData?.let { videoData -> appPlayer.setUpWith(videoData) }
        return merge(
            flowOf(CreatePlayerResult(appPlayer)),
            appPlayer.onPlayerRendering().map { OnPlayerRenderingResult },
            appPlayer.errors().map(::PlayerErrorResult),

            appPlayer.onMediaItemTransition().mapLatest {
                Log.i("videoProgress", "createPlayer() onMediaItemTransition :: $it")
                MediaItemTransitionResult(it)
            },
            appPlayer.onPlaybackStateChanged().mapLatest {
                Log.i("videoProgress", "createPlayer() onPlaybackStateChanged :: $it")
                if(it==3) { // Stat is Playing
                    appPlayer.play()
                    NoOpResult
                } else {
                    NoOpResult
                }
//                videoProgressBarEvent(it)

            }
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun resumePlayer(): Flow<ViewResult> {

        check(states.value.appPlayer == null) { "Tried to create a player when one already exists" }

        val config = AppPlayer.Factory.Config(loopVideos = true)
        val appPlayer = appPlayerFactory.create(config, playerView!!)
        // If video data already exists then the player should have that video data set on it. This
        // can happen because the player has a lifecycle tied to Activity starting/stopping.

        states.value.videoData?.let { videoData -> appPlayer.resumeSetupWith(videoData, handle.get()) }
        return merge(
            flowOf(CreatePlayerResult(appPlayer)),
            appPlayer.onPlayerRendering().map { OnPlayerRenderingResult },
            appPlayer.errors().map(::PlayerErrorResult),
//            appPlayer.onTracksChanged().mapLatest {
////                Toast.makeText(context, "Track is changed", Toast.LENGTH_SHORT).show()
//                NoOpResult
//            },
//            appPlayer.onTimelineChanged().mapLatest {
////                Toast.makeText(context, "Track is changed", Toast.LENGTH_SHORT).show()
//                NoOpResult
//            },
            appPlayer.onMediaItemTransition().mapLatest {
                Log.i("videoProgress", "resumePlayer() onMediaItemTransition :: $it")
                MediaItemTransitionResult(it)
            },
            appPlayer.onPlaybackStateChanged().mapLatest {
                Log.i("videoProgress", "resumePlayer() onPlaybackStateChanged :: $it")
                videoProgressBarEvent(it)
                NoOpResult
            }
        )
    }

    private fun tearDownPlayer(): Flow<ViewResult> {
        val appPlayer = requireNotNull(states.value.appPlayer)
        // Keep track of player state so that it can be restored across player recreations.
        handle.set(appPlayer.currentPlayerState)
        Log.i("AshwaniXYZ", "tearDownPlayer() appPlayer.currentPlayerState :: $appPlayer.currentPlayerState")
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
                Log.i("AshwaniXYZ", "OnPageSettledEvent :: IF")
                NoOpResult
            } else {
                val appPlayer = requireNotNull(states.value.appPlayer)
//                handle.set(appPlayer.currentPlayerState)
                // To Play directly at specific index
                appPlayer.playMediaAt(event.page)
                Log.i("AshwaniXYZ", "<OnPageSettledEvent> :: ELSE 1 :: ${event.page}")
                Log.i("AshwaniXYZ", "<OnPageSettledEvent> :: ELSE 2 :: ${appPlayer.currentPlayerState.currentMediaItemIndex}")
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
    private fun Flow<VideoInfoEvent>.toVideoInfoResults(): Flow<GetVideoInfoResult> {
        Log.i("VideoInfoOnSwipe", "<VideoInfoEvent>, Started")
        return flatMapLatest { event ->
            repository.getVideoInfo(event.videoId, event.position)
        }.mapLatest {
            val videoInfo = it.first
            if(videoInfo.id == "43694") {
                videoInfo.hasAd = true
            }
            Log.i("VideoInfoOnSwipe", "<VideoInfoEvent>, VideoId =  ${videoInfo.id}, ChannelId =  ${videoInfo.channel_id}")
            Pair(videoInfo, it.second)
        }.map {
            GetVideoInfoResult(it.second, it.first)
        }
    }


    @OptIn(DelicateCoroutinesApi::class)
    fun setUpWithPlayer() {
        GlobalScope.launch(Dispatchers.Main) {
            val appPlayer = states.value.appPlayer
            // If the player exists, it should be updated with the latest video data that came in
            appPlayer?.setUpWith(states.value.videoData!!)
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
            Log.i("VideoInfoOnSwipe", "<GetVideoInfoResult>, ChannelId =  ${result.response.id}")
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
                this.hasAd = result.response.hasAd
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

    companion object {
        const val perPage = 40
        val cacheHashSet: CachingLinkedHashMap<String, VideoInfoData> = CachingLinkedHashMap(50)
    }


    private var _videoInfoChanged= MutableSharedFlow<GetVideoInfoEffect>()
    val videoInfoChanged = _videoInfoChanged.asSharedFlow()

    fun videoInfoChanged(videoChanged: GetVideoInfoEffect) {
        viewModelScope.launch {
            _videoInfoChanged.emit(videoChanged)
        }
    }

    suspend fun invokeVideoInfo(videoId: String, position: Int) {

        cacheHashSet[videoId]?.let {
            videoInfoChanged(GetVideoInfoEffect(it, position))
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val videoInfo = userApiService.getVideoInfo(videoId)
                val data = videoInfo.data
                states.value.videoData?.get(position)?.apply {
                    this.comment_count = data.comment_count
                    this.like_count = data.like_count
                    this.saveCount = data.saved_count
                    this.following = data.following
                    this.saved = data.saved
                    this.liking = data.liked
                    this.channel_image = data.channel_image
                    this.channel_id = data.channel_id
                    this.title = data.title
                    this.hasAd = data.hasAd
                }
                Log.i("VideoInfoOnSwipe", "invokeVideoInfo() Success =  Send for Effect")
                Log.i("VideoInfoOnSwipe", "invokeVideoInfo() Success =  Send for Effect")
                videoInfoChanged(GetVideoInfoEffect(data, position))
                cacheHashSet[videoId] = data


            } catch (e: Exception) {
                Log.i("VideoInfoOnSwipe", "invokeVideoInfo() Error =  ${e.localizedMessage}")
            }
        }
    }

}
