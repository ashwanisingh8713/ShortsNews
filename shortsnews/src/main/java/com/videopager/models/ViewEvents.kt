package com.videopager.models

internal sealed class ViewEvent

internal data class LoadVideoDataEvent(val categoryId: String, val videoFrom: String, val page: Int, val perPage: Int, val languages:String) : ViewEvent()
internal class PreLoadedVideoDataEvent : ViewEvent()

internal sealed class PlayerLifecycleEvent : ViewEvent() {
    object Start : PlayerLifecycleEvent()
    data class Stop(val isChangingConfigurations: Boolean) : PlayerLifecycleEvent()
    data class Destroy(val isChangingConfigurations: Boolean) : PlayerLifecycleEvent()
}

internal object TappedPlayerEvent : ViewEvent()
internal object ChannelClickEvent : ViewEvent()
internal data class BookmarkClickEvent(val videoId: String, val position: Int) : ViewEvent()
internal object ShareClickEvent : ViewEvent()
internal data class CommentClickEvent(val videoId: String, val position: Int) : ViewEvent()
internal data class LikeClickEvent(val videoId: String, val position: Int) : ViewEvent()

internal data class VideoInfoEvent(val videoId:String, val position:Int):ViewEvent()

internal data class PostClickCommentEvent(val videoId: String, val comment: String, val position:Int): ViewEvent()
internal object NoFurtherEvent : ViewEvent()

internal data class OnPageSettledEvent(val page: Int) : ViewEvent()

internal object PauseVideoEvent : ViewEvent()

internal data class FromNotificationInsertVideoEvent(val videoId: String, val type: String, val previewUrl:String, val videoUrl:String) : ViewEvent()
