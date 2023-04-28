package com.ns.shortsnews.video.data

import android.util.Log
import com.player.models.VideoData
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.videopager.data.VideoDataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.CancellationException

class VideoDataRepositoryImpl : VideoDataRepository {
    // Kamlesh(Changed Base Url)
    private val api = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create())
        .baseUrl("https://shorts.newsdx.io/ci/api/public/")
        .build()
        .create(VideoDataService::class.java)

    companion object{
        val youtube = "https://rr5---sn-5jucgv5qc5oq-cag6.googlevideo.com/videoplayback?expire=1682688559&ei=z3VLZKHTBfGL8QOm74bwDQ&ip=49.206.30.101&id=o-AH2GLTQSE-w2yIv3NLTTa2YWZv-RtSPW_PGXyb3quioj&itag=18&source=youtube&requiressl=yes&mh=ef&mm=31%2C29&mn=sn-5jucgv5qc5oq-cag6%2Csn-h557sn67&ms=au%2Crdu&mv=m&mvi=5&pl=21&initcwndbps=1742500&vprv=1&mime=video%2Fmp4&ns=m5wjV0T4oF2KQU3JbCLiuhQN&gir=yes&clen=4369439&ratebypass=yes&dur=58.858&lmt=1680372565658345&mt=1682666464&fvip=3&fexp=24007246&c=WEB&txp=5530434&n=1kt1znMewjq8tX4Kk&sparams=expire%2Cei%2Cip%2Cid%2Citag%2Csource%2Crequiressl%2Cvprv%2Cmime%2Cns%2Cgir%2Cclen%2Cratebypass%2Cdur%2Clmt&lsparams=mh%2Cmm%2Cmn%2Cms%2Cmv%2Cmvi%2Cpl%2Cinitcwndbps&lsig=AG3C_xAwRQIgWdyCMJtG0v0RFPh0JEeT8dJXtZUo9Hh2qQaCFd4xQeUCIQDp0I-AUOgekwR-Llw8jRtJBzCDu83WQ_SaBjI7-pDGEg%3D%3D&sig=AOq0QJ8wRQIhAPn_qWltkp3N8ppC7JlZmFeRdTovZEeB2GQjZ2PIzNtYAiBHwIkFsu1ZSGhzmzlRQB3WKZB9DMnLHJmumZMD-qrmFQ=="
    }

    override fun videoData(requestType: String): Flow<List<VideoData>> = flow {
        try {
            var rt = requestType
            Log.i("", "$rt")
            val response = api.getShortsVideos(rt)
            val youtubeUrl = Data(id = "y01",
                preview = "https://ndxv3.s3.ap-south-1.amazonaws.com/video-preview.png",
                videoUrl = "https://www.youtube.com/watch?v=01omBMDKkDs",
//                videoUrl = youtube,
                title="", )

//            response.data.add(0, youtubeUrl)
            response.data.add(0, youtubeUrl)

            val videoData = response.data
                .map { post ->
                    VideoData(
                        id = post.id.orEmpty(),
                        mediaUri = post.videoUrl,
                        previewImageUri = post.preview!!,
                        aspectRatio = null
                    )
                }
                    /*(Kamlesh)Video id and preview image is not available so commented*/
                ?.filter { videoData ->
                    /*videoData.id.isNotBlank()
                        && videoData.mediaUri.isNotBlank()
                        && videoData.previewImageUri.isNotBlank()*/
                    videoData.mediaUri.isNotBlank()
                }
                .orEmpty()

            emit(videoData)
        } catch (throwable: Throwable) {
            if (throwable is CancellationException) throw throwable
            Log.d("asdf", "Error", throwable)
        }
    }

    override fun like(videoId: String): Flow<String> = flow {
//        api.like(videoId)
        emit("Like Response")
    }

    override fun follow(videoId: String): Flow<String> = flow {
//        api.follow(videoId)
        emit("Follow Response")
    }

    override fun comment(videoId: String): Flow<String> = flow {
//        api.comment(videoId)
        emit("Comment Response")
    }

    override fun getVideoInfo(videoId: String): Flow<String> = flow {
        emit("Video Info Response")
    }

    private interface VideoDataService {
        @GET("videos")
        suspend fun getShortsVideos(@Query("category") category: String): VideoDataResponse
        @GET("videos")
        suspend fun like(videoId: String): String
        @GET("videos")
        suspend fun follow(videoId: String): String
        @GET("comment")
        suspend fun comment(videoId: String): String
        @GET("info")
        suspend fun getVideoInfo(videoId: String): String
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

}
