package com.videopager.models

import androidx.annotation.DrawableRes

internal sealed class ViewEffect

internal sealed class PageEffect : ViewEffect()

internal data class AnimationEffect(@DrawableRes val drawable: Int) : PageEffect()
internal object SaveEffect : ViewEffect()
internal object CommentEffect : ViewEffect()
internal object LikeEffect : ViewEffect()
internal object FollowEffect : ViewEffect()
internal object GetVideoInfoEffect : ViewEffect()

internal object ResetAnimationsEffect : PageEffect()

internal data class PlayerErrorEffect(val throwable: Throwable) : ViewEffect()
