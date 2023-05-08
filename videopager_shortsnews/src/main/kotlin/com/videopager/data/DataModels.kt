package com.videopager.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)

data class Comments(
    @Json(name = "data")
    val data:List<CommentData>,
    val status:Boolean,
    val total:Int,
    val page:Int,
    val perPage:Int
)
@JsonClass(generateAdapter = true)
data class CommentData(
    val user_name: String,
    val comment:String,
    val created_at:String,
    val user_image:String,
)

@JsonClass(generateAdapter = true)
data class PostComment(
    val data: PostCommentData
)


@JsonClass(generateAdapter = true)
data class PostCommentData(
    val comment_count: String,
    val user_name: String,
    val comment:String,
    val created_at:String,
    val user_image:String,
)

@JsonClass(generateAdapter = true)
data class VideoInfo(
    val data:VideoInfoData,
    val status:Boolean
)

@JsonClass(generateAdapter = true)
data class VideoInfoData(
    val title:String = "",
    val like_count:String = "",
    val comment_count:String = "",
    val following:Boolean = false,
    val id:String = "",
    val channel_id:String = "",
    val channel_image:String = ""
)
@JsonClass(generateAdapter = true)
data class Following(
    val data:FollowingData,
    val status: Boolean,
    val msg:String

)
@JsonClass(generateAdapter = true)
data class FollowingData (
    val following:Boolean,
    val channel_id:String
        )





