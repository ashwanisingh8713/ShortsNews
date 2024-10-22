package com.videopager.ui.extensions

/**
 * Created by Ashwani Kumar Singh on 25,April,2023.
 */
sealed class ClickEvent

object ChannelClick: ClickEvent()
object BookmarkClick: ClickEvent()
object ShareClick: ClickEvent()
object CommentClick: ClickEvent()
object LikeClick: ClickEvent()
object PlayPauseClick: ClickEvent()
object TrackInfoClick: ClickEvent()
object DoubleTapClick: ClickEvent()
