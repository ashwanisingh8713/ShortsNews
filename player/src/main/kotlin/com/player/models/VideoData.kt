package com.player.models

data class VideoData(
    val id: String,
    val mediaUri: String,
    val previewImageUri: String,
    val aspectRatio: Float? = null,
    val channel_id: String = "",
    val title: String = "",
    val like_count:String = "",
    val comment_count:String = "",
    val following:Boolean = false,
    val channel_image:String = "",

)
