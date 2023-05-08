package com.ns.shortsnews.video.data

import android.content.Context
import android.util.Log
import android.util.SparseArray
import at.huber.me.YouTubeUri
import at.huber.youtubeExtractor.VideoMeta
import at.huber.youtubeExtractor.YouTubeExtractor
import at.huber.youtubeExtractor.YtFile
import com.player.models.VideoData
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.videopager.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.CancellationException

class VideoDataRepositoryImpl : VideoDataRepository {
    // Kamlesh(Changed Base Url)
    private val token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjo0LCJ0aW1lIjoxNjgyNTc4MzI3fQ.hRPaXQa1L-LFMrS2TnPZKbW2kxVHYdoJR6PgTaGrZFM"

    private val api = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create())
        .baseUrl("https://shorts.newsdx.io/ci/api/public/")
        .client(OkHttpClient.Builder().addInterceptor{ chain ->
            val request = chain.request().newBuilder().addHeader("Authorization", "Bearer ${token}").build()
            chain.proceed(request)
        }.build())
        .build()
        .create(VideoDataService::class.java)


    override suspend fun videoData(requestType: String, context: Context): Flow<MutableList<VideoData>> {
        var ll = mutableListOf<MutableList<VideoData>>()

        val llVid = withContext(Dispatchers.IO) {

            val response = api.getShortsVideos(requestType)
            val youtubeUrl1 = Data(
                id = "16",
                preview = "https://ndxv3.s3.ap-south-1.amazonaws.com/video-preview.png",
                videoUrl = "https://youtube.com/watch?v=m1EvCw123HM",
                title = "",
                type = "yt"
            )

            val youtubeUrl2 = Data(
                id = "16",
                preview = "https://ndxv3.s3.ap-south-1.amazonaws.com/video-preview.png",
                videoUrl = "https://youtube.com/watch?v=jnPzy3BX-GA",
                title = "",
                type = "yt"
            )

//            response.data.add(0, youtubeUrl1)
            /*response.data.add(0, youtubeUrl1)
            response.data.add(2, youtubeUrl2)
            response.data.add(2, youtubeUrl2)
            response.data.add(2, youtubeUrl2)
            response.data.add(2, youtubeUrl2)
            response.data.add(2, youtubeUrl2)
            response.data.add(2, youtubeUrl2)
            response.data.add(2, youtubeUrl2)
            response.data.add(2, youtubeUrl2)
            response.data.add(2, youtubeUrl2)
            response.data.add(2, youtubeUrl2)
            response.data.add(2, youtubeUrl2)
            response.data.add(2, youtubeUrl2)
            response.data.add(2, youtubeUrl2)*/



            val allowedConversionCount = 2
            var conversionCount = 0

            val videoData = response.data
                .map { post ->
                    if (post.type!="yt" || allowedConversionCount <= conversionCount) {
                        VideoData(
                            id = post.id,
                            mediaUri = post.videoUrl,
                            previewImageUri = post.preview!!,
                            aspectRatio = null,
                            type = post.type
                        )
                    } else {
                        val youTubeUri = YouTubeUri(context)
                        var ytFiles = youTubeUri.getStreamUrls(post.videoUrl)
                        val iTag = 18
                        var finalUri18 = ""
                        if (ytFiles != null && ytFiles.contains(iTag)) {
                            finalUri18 = ytFiles[iTag].url
                            conversionCount++
                        }
                        VideoData(
                            id = post.id,
                            mediaUri = finalUri18,
                            previewImageUri = post.preview!!,
                            aspectRatio = null,
                            type = "yt"
                        )
                    }
                }.filter {
                    it.mediaUri.isNotBlank()
                }

            Log.i("Conv_TIME", "VideoDataRepositoryImpl")

            ll.add(videoData as MutableList)
            ll
        }

        return llVid.asFlow()
    }



    override fun like(videoId: String, position: Int): Flow<Triple<String, Boolean, Int>> = flow {
        try {
            val res = api.like(videoId)
            emit(Triple(res.data.like_count, res.data.liked, position))
        }  catch (ec :java.lang.Exception) {
            Log.i("kamels","$ec")
        }

    }

    override fun follow(channel_id: String, position: Int): Flow<Pair<Following, Int>> = flow {
        try {
            val data =  api.follow(channel_id)
            emit(Pair(data, position))
        } catch (ec :java.lang.Exception) {
            Log.i("kamels","$ec")
        }

    }

    override fun comment(videoId: String, position: Int): Flow<Triple<String, Comments, Int>> = flow {
        try {
            val data =  api.comment(videoId)
            emit(Triple(videoId, data, position))
        }  catch (ec :java.lang.Exception) {
            Log.i("kamels","$ec")
        }

    }

    override fun getVideoInfo(videoId: String, position: Int): Flow<Pair<VideoInfoData,Int>> = flow {

        try {
            val data = api.getVideoInfo(videoId)
            if(data.status) {
                emit(Pair(data.data, position))
            } else {
                emit(Pair(VideoInfoData(), position))
            }

        } catch (ec :java.lang.Exception) {
            Log.i("kamels","$ec")
            emit(Pair(VideoInfoData(), position))
        }

    }

    override fun getPostComment(videoId: String, comment: String, position: Int): Flow<Pair<PostComment, Int>> = flow {
        try {
            val body = mutableMapOf<String, String>()
            body["comment"] = comment
            val data = api.getPostComment(videoId, body)
            emit(Pair(data,position))
        } catch (ec :java.lang.Exception) {
            Log.i("kamels","$ec")
        }
    }


    private interface VideoDataService {
        @GET("videos")
        suspend fun getShortsVideos(@Query("category") category: String): VideoDataResponse
        @GET("like-unlike-video/{video_id}")
        suspend fun like(@Path("video_id")videoId: String): LikeUnlike
        @GET("follow-unfollow-channel/{channel_id}")
        suspend fun follow(@Path("channel_id")channel_id: String): Following
        @GET("get-comments/{video_id}")
        suspend fun comment(@Path("video_id") videoId: String): Comments
        @GET("video-info/{video_id}")
        suspend fun getVideoInfo(@Path("video_id")videoId: String): VideoInfo

        @POST("comment-video/{video_id}")
        suspend fun getPostComment(@Path("video_id") videoId:String, @Body comment:Map<String, String>): PostComment
    }

    @JsonClass(generateAdapter = true)
data class VideoDataResponse(
    @Json(name = "data")
    val `data`: MutableList<Data>,
    @Json(name = "status")
    val status: Boolean
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
    val type: String = ""
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

}
