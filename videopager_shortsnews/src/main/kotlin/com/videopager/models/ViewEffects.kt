package com.videopager.models

import android.os.Bundle
import android.view.View
import androidx.annotation.DrawableRes
import com.videopager.data.CommentData
import com.videopager.data.PostComment
import com.videopager.data.PostCommentData

internal sealed class ViewEffect

internal sealed class PageEffect : ViewEffect()

internal data class AnimationEffect(@DrawableRes val drawable: Int) : PageEffect()
internal object SaveEffect : ViewEffect()
internal data class CommentEffect(val videoId:String, val comments:List<CommentData>, val position: Int) : ViewEffect()
internal data class LikeEffect(val position: Int) : ViewEffect()
internal data class FollowEffect(val position: Int) : ViewEffect()
internal object GetVideoInfoEffect : ViewEffect()
internal object YoutubeUriErrorEffect : ViewEffect()
//internal data class YoutubeUriErrorEffect(val position: String) : ViewEffect()

internal data class PostCommentEffect(val data:PostCommentData, val position: Int): ViewEffect()

internal object ResetAnimationsEffect : PageEffect()

internal data class PlayerErrorEffect(val throwable: Throwable) : ViewEffect()
