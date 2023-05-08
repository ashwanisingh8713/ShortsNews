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
internal data class FollowClickEvent(val channelId: String, val position: Int) : ViewEvent()
internal data class SaveClickEvent(val videoId: String, val position: Int) : ViewEvent()
internal object ShareClickEvent : ViewEvent()
internal data class CommentClickEvent(val videoId: String, val position: Int) : ViewEvent()
internal data class LikeClickEvent(val videoId: String, val position: Int) : ViewEvent()

internal data class VideoInfoEvent(val videoId:String, val position:Int):ViewEvent()
internal data class GetYoutubeUriEvent(val type: String, val position:Int):ViewEvent()

internal data class PostClickCommentEvent(val videoId: String, val comment: String, val position:Int): ViewEvent()
internal object NoFurtherEvent : ViewEvent()

internal data class OnPageSettledEvent(val page: Int) : ViewEvent()

internal object PauseVideoEvent : ViewEvent()
internal object YoutubeUriExtractionEvent : ViewEvent()
