package com.player.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class VideoData(
    var id: String,
    var mediaUri: String="",
    var previewImageUri: String="",
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
    var video_url_mp4:String = "",
    var follow_count:String = "",
    var page: Int = 1,
    var perPage: Int = 1,
    var hasAd: Boolean = false
): Parcelable {
    override fun equals(other: Any?): Boolean {
        return if (other is VideoData) {
            other.id == id
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        super.hashCode()
        return id.hashCode()
    }
}
