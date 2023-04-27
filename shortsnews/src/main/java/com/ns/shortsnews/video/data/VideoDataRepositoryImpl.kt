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

    override fun videoData(requestType: String): Flow<List<VideoData>> = flow {
        try {
            var rt = requestType
            Log.i("", "$rt")
            val response = api.getShortsVideos(rt)
            val youtubeUrl = Data(id = "y01", preview = "",
                videoUrl = "https://www.youtube.com/watch?v=01omBMDKkDs", title="", )

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
