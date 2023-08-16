package com.ns.shortsnews.domain.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

/**
 * Created by Ashwani Kumar Singh on 21,April,2023.
 */



@JsonClass(generateAdapter = true)
data class ProfileResult(
    val data: ProfileData,
    val status:Boolean,
)

@JsonClass(generateAdapter = true)
@Parcelize
data class ProfileData(
    val user_id:String = "",
    val email:String ="",
    val name:String ="",
    val last_logged_in:String ="",
    val image:String ="",
    val age:String ="",
    val location:String =""
): Parcelable
@JsonClass(generateAdapter = true)
data class OTPResult(
    @Json(name = "data")
    val `data`: OtpValidationData? = null,
    val status: Boolean,
    val msg: String
)
@JsonClass(generateAdapter = true)
data class OtpValidationData(
    val access_token: String,
    val email: String = "",
    val name: String? = null,
    val first_time_user: Boolean,
    val my_profile: ProfileData
)

@JsonClass(generateAdapter = true)
data class UserChoiceResult(val name: String)
@JsonClass(generateAdapter = true)
data class RegistrationResult(
    val `data`: RegistrationData? = null,
    val status: Boolean,
    val msg: String
)
@JsonClass(generateAdapter = true)
data class RegistrationData(
    val OTP_id: Int,
    val length: Int,
    val email: String
)

@JsonClass(generateAdapter = true)
data class VideoCategoryResult(
    val data: List<VideoCategory>,
    val status: Boolean
)
@JsonClass(generateAdapter = true)
data class VideoCategory(
    val id: String,
    var name: String,
    var selected:Boolean = false,
    var icon: String = ""
)

@JsonClass(generateAdapter = true)
data class ChannelsDataResult(
    val data:List<ChannelListData>,
    val status:Boolean,
    val total:Int,
    val page:Int,
    val perPage:Int
)

@JsonClass(generateAdapter = true)
data class ChannelListData(
   val channel_id:String,
   val channel_image:String,
   val channelTitle:String
)

@JsonClass(generateAdapter = true)
data class ChannelInfo(
    val data:ChannelInfoData,
    val status:Boolean
)

@JsonClass(generateAdapter = true)
data class ChannelInfoData(
val channel_id:String = "",
val channel_image:String = "",
val channelTitle:String = "",
val follow_count:String = "",
val description:String = "",
val following:Boolean = false
)


@JsonClass(generateAdapter = true)
data class LanguagesResult(
    val status: Boolean,
    val data: List<LanguageData> = emptyList()
)

@JsonClass(generateAdapter = true)
data class LanguageData(
    val id:String = "",
    var name:String = "",
    var slug:String = "",
    var isSelected:Boolean = false,
    var icon: String = ""
)


@JsonClass(generateAdapter = true)
data class VideoDataResponse(
    @Json(name = "data")
    val data: MutableList<Data> = mutableListOf(),
    @Json(name = "status")
    val status: Boolean = false
)

/*(Kamlesh) Created new data class as per newsdx response */
@JsonClass(generateAdapter = true)
data class Data(
    @Json(name = "title")
    val title: String,
    @Json(name = "video_url")
    val videoUrl: String,
    @Json(name = "id")
    val id:String= "",
    @Json(name = "videoPreviewUrl")
    val preview:String?= "",
    val type: String = "",
    val channelTitle:String = "",
    val like_count:String = "",
    val video_url_mp4:String = "",
    val width:Int = 608,
    val height:Int = 1080,
    val liked:Boolean = false
)
/*Kamlesh(Data class for like/unlike)*/

@JsonClass(generateAdapter = true)
data class LikeUnlike(
    @Json(name = "data")
    val `data` : LikeUnlikeData,
    val status:Boolean,
    val msg:String
)

@JsonClass(generateAdapter = true)
data class LikeUnlikeData(
    val liked:Boolean,
    val like_count:String

)

@JsonClass(generateAdapter = true)
data class StatusResult(
    val data:List<String>? = emptyList(),
    val status: Boolean,
    val msg: String = ""
)
@Entity(tableName = "language")
data class LanguageTable(

    @PrimaryKey
    val id:String ="",

    val name:String = "",
    val slug:String = "",

    val selected:Boolean = false,
    val icon:String = ""
)
@Entity(tableName = "interests")
data class InterestsTable(
    @PrimaryKey
    val id: String ="",

    var name: String ="",
    var selected:Boolean = false,
    var icon: String = ""
)


