package com.videopager.models

internal sealed class ViewEvent

internal object LoadVideoDataEvent : ViewEvent()

internal sealed class PlayerLifecycleEvent : ViewEvent() {
    object Start : PlayerLifecycleEvent()
    data class Stop(val isChangingConfigurations: Boolean) : PlayerLifecycleEvent()
    data class Destroy(val isChangingConfigurations: Boolean) : PlayerLifecycleEvent()
}

internal object TappedPlayerEvent : ViewEvent()
internal object ChannelClickEvent : ViewEvent()
internal object FollowClickEvent : ViewEvent()
internal object SaveClickEvent : ViewEvent()
internal object ShareClickEvent : ViewEvent()
internal object CommentClickEvent : ViewEvent()
internal object LikeClickEvent : ViewEvent()
internal object NoFurtherEvent : ViewEvent()

internal data class OnPageSettledEvent(val page: Int) : ViewEvent()

internal object PauseVideoEvent : ViewEvent()
