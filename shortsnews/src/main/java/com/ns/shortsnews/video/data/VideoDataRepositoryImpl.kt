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

    companion object{
        val youtube = "https://rr5---sn-5jucgv5qc5oq-cag6.googlevideo.com/videoplayback?expire=1682688559&ei=z3VLZKHTBfGL8QOm74bwDQ&ip=49.206.30.101&id=o-AH2GLTQSE-w2yIv3NLTTa2YWZv-RtSPW_PGXyb3quioj&itag=18&source=youtube&requiressl=yes&mh=ef&mm=31%2C29&mn=sn-5jucgv5qc5oq-cag6%2Csn-h557sn67&ms=au%2Crdu&mv=m&mvi=5&pl=21&initcwndbps=1742500&vprv=1&mime=video%2Fmp4&ns=m5wjV0T4oF2KQU3JbCLiuhQN&gir=yes&clen=4369439&ratebypass=yes&dur=58.858&lmt=1680372565658345&mt=1682666464&fvip=3&fexp=24007246&c=WEB&txp=5530434&n=1kt1znMewjq8tX4Kk&sparams=expire%2Cei%2Cip%2Cid%2Citag%2Csource%2Crequiressl%2Cvprv%2Cmime%2Cns%2Cgir%2Cclen%2Cratebypass%2Cdur%2Clmt&lsparams=mh%2Cmm%2Cmn%2Cms%2Cmv%2Cmvi%2Cpl%2Cinitcwndbps&lsig=AG3C_xAwRQIgWdyCMJtG0v0RFPh0JEeT8dJXtZUo9Hh2qQaCFd4xQeUCIQDp0I-AUOgekwR-Llw8jRtJBzCDu83WQ_SaBjI7-pDGEg%3D%3D&sig=AOq0QJ8wRQIhAPn_qWltkp3N8ppC7JlZmFeRdTovZEeB2GQjZ2PIzNtYAiBHwIkFsu1ZSGhzmzlRQB3WKZB9DMnLHJmumZMD-qrmFQ=="
    }

    override suspend fun videoData(requestType: String, context: Context): Flow<List<VideoData>> =
        withContext(Dispatchers.IO) {

            val response = api.getShortsVideos(requestType)
            val youtubeUrl1 = Data(id = "16",
                preview = "https://ndxv3.s3.ap-south-1.amazonaws.com/video-preview.png",
                videoUrl = "m1EvCw123HM",
                title="", )

            val youtubeUrl2 = Data(id = "16",
                preview = "https://ndxv3.s3.ap-south-1.amazonaws.com/video-preview.png",
                videoUrl = "jnPzy3BX-GA",
                title="", )

            response.data.add(0, youtubeUrl1)
            response.data.add(0, youtubeUrl2)


            val videoData = response.data
                .map { post ->

                    if(post.videoUrl.contains("https")) {
                        VideoData(
                            id = post.id.orEmpty(),
                            mediaUri = post.videoUrl,
                            previewImageUri = post.preview!!,
                            aspectRatio = null
                        )
                    } else {
                        val youTubeUri = YouTubeUri(context)
                        var ytFiles = youTubeUri.getStreamUrls(post.videoUrl)
                        val iTag = 18
                        var finalUri17 = ytFiles[17]
                        var finalUri5 = ytFiles[5]
                        var finalUri18 = ytFiles[iTag].url
                        Log.i("", "$finalUri5")
                        Log.i("", "$finalUri17")
                        Log.i("", "$finalUri18")
                        VideoData(
                            id = post.id.orEmpty(),
                            mediaUri = finalUri18,
                            previewImageUri = post.preview!!,
                            aspectRatio = null
                        )
                    }
                }

            var ll = mutableListOf<List<VideoData>>()
            ll.add(videoData)
            ll.asFlow()
        }


    /*override fun videoData(requestType: String, context: Context): Flow<List<VideoData>> = flow {
        try {
            var rt = requestType
            Log.i("", "$rt")
            val response = api.getShortsVideos(rt)
            val youtubeUrl = Data(id = "16",
                preview = "https://ndxv3.s3.ap-south-1.amazonaws.com/video-preview.png",
                videoUrl = "https://www.youtube.com/watch?v=01omBMDKkDs",
//                videoUrl = youtube,
                title="", )

//            response.data.add(0, youtubeUrl)
//            response.data.add(0, youtubeUrl)

            val youTubeUri = YouTubeUri(context)
            val ytFiles = youTubeUri.getStreamUrls("01omBMDKkDs")
            val videoMeta = youTubeUri.videoMeta

            Log.i("", "")

            val videoData = response.data
                .map { post ->
                    VideoData(
                        id = post.id.orEmpty(),
                        mediaUri = post.videoUrl,
                        previewImageUri = post.preview!!,
                        aspectRatio = null
                    )
                }
                    *//*(Kamlesh)Video id and preview image is not available so commented*//*
                ?.filter { videoData ->
                    *//*videoData.id.isNotBlank()
                        && videoData.mediaUri.isNotBlank()
                        && videoData.previewImageUri.isNotBlank()*//*
                    videoData.mediaUri.isNotBlank()
                }
                .orEmpty()

            emit(videoData)
        } catch (throwable: Throwable) {
            if (throwable is CancellationException) throw throwable
            Log.d("asdf", "Error", throwable)
        }
    }*/

    override fun like(videoId: String, position: Int): Flow<Triple<String, Boolean, Int>> = flow {
        val res = api.like(videoId)
        emit(Triple(res.data.like_count, res.data.liked, position))
    }

    override fun follow(channel_id: String, position: Int): Flow<Pair<Following, Int>> = flow {
       val data =  api.follow(channel_id)
        emit(Pair(data, position))
    }

    override fun comment(videoId: String, position: Int): Flow<Triple<String, Comments, Int>> = flow {
       val data =  api.comment(videoId)
        emit(Triple(videoId, data, position))
    }

    override fun getVideoInfo(videoId: String, position: Int): Flow<Pair<VideoInfo,Int>> = flow {
        val data = api.getVideoInfo(videoId)
        emit(Pair(data, position))
    }

    override fun getPostComment(videoId: String, comment: String, position: Int): Flow<Pair<PostComment, Int>> = flow {
        val body = mutableMapOf<String, String>()
        body["comment"] = comment
        val data = api.getPostComment(videoId, body)
        emit(Pair(data,position))
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
    val id:String?= "",
    @Json(name = "videoPreviewUrl")
    val preview:String?= ""
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
