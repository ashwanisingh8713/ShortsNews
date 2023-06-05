package com.player.models

data class VideoData(
    var id: String,
    var mediaUri: String,
    var previewImageUri: String,
    val aspectRatio: Float? = null,
    var channel_id: String = "",
    var title: String = "",
    var like_count:String = "",
    var comment_count:String = "",
    var following:Boolean = false,
    var liking:Boolean = false,
    var channel_image:String = "",
    var type:String = "",
    var adTagUri:String = "",
    var saveCount:String = "",
    var saved:Boolean = false,
    var video_url_mp4:String = ""
)
