package com.videopager.models

import androidx.annotation.DrawableRes
import com.videopager.data.*

internal sealed class ViewEffect

internal sealed class PageEffect : ViewEffect()

internal data class AnimationEffect(@DrawableRes val drawable: Int) : PageEffect()
internal data class BookmarkEffect(val position: Int) : ViewEffect()
internal data class CommentEffect(val videoId:String, val comments:List<CommentData>, val position: Int) : ViewEffect()
internal data class LikeEffect(val position: Int) : ViewEffect()
internal data class FollowEffect(val position: Int, val channelId: Following) : ViewEffect()
internal data class GetVideoInfoEffect(val videoInfo: VideoInfoData, val position: Int) : ViewEffect()
internal object YoutubeUriErrorEffect : ViewEffect()

internal data class GetYoutubeUriEffect(val uri: String, val id: String) : ViewEffect()

internal data class PostCommentEffect(val data:PostCommentData, val position: Int): ViewEffect()

internal object ResetAnimationsEffect : PageEffect()

internal data class PlayerErrorEffect(val throwable: Throwable) : ViewEffect()
