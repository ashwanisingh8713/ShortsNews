package com.videopager.models

import androidx.annotation.DrawableRes
import com.google.android.exoplayer2.MediaItem
import com.player.models.VideoData
import com.player.players.AppPlayer
import com.videopager.data.*

internal sealed class ViewResult

internal object NoOpResult : ViewResult()
internal data class FollowClickResult(val position: Int, val following: Following) : ViewResult()
internal data class CommentClickResult( val videoId:String, val comments: Comments, val position: Int) : ViewResult()
internal data class LikeClickResult(val position: Int): ViewResult()
internal data class BookmarkClickResult(val position: Int): ViewResult()
internal data class GetVideoInfoResult(val position: Int, val response: VideoInfoData): ViewResult()
internal data class GetYoutubeUriResult(val uri: String, val id: String): ViewResult()
internal data class GetYoutubeUriResult_2(val uri: String, val id: String): ViewResult()

internal data class PostCommentResult(val response:PostCommentData , val position:Int):ViewResult()

internal data class LoadVideoDataResult(val videoData: MutableList<VideoData>, val currentMediaItemIndex: Int) : ViewResult()
internal data class FromNotificationInsertVideoDataResult(val videoData: MutableList<VideoData>, val currentMediaItemIndex: Int) : ViewResult()

internal data class CreatePlayerResult(val appPlayer: AppPlayer) : ViewResult()

internal object TearDownPlayerResult : ViewResult()

internal data class TappedPlayerResult(@DrawableRes val drawable: Int) : ViewResult()

internal data class OnNewPageSettledResult(val page: Int) : ViewResult()

internal object OnPlayerRenderingResult : ViewResult()
internal object OnYoutubeUriErrorResult : ViewResult()

internal data class AttachPlayerToViewResult(val doAttach: Boolean) : ViewResult()

internal data class PlayerErrorResult(val throwable: Throwable) : ViewResult()
internal data class MediaItemTransitionResult(val mediaItem: MediaItem) : ViewResult()

//internal data class NotificationVideoPageSettleResult(val position: Int, val videoId: String): ViewResult()
