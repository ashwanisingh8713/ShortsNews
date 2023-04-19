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
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.CancellationException

class RedditVideoDataRepository : VideoDataRepository {
    private val api = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create())
        .baseUrl("https://newsdx.io/")
        .build()
        .create(RedditService::class.java)

    override fun videoData(requestType: String): Flow<List<VideoData>> = flow {
        try {
            val response = api.tikTokCringe()
            val videoData = response
                .data
                .map { post ->
                    val video = post
        //                    val width = video?.width
        //                    val height = video?.height
        //                    val aspectRatio = if (width != null && height != null) {
        //                        width.toFloat() / height.toFloat()
        //                    } else {
        //                        null
        //                    }
                    VideoData(
                        id = post.id.orEmpty(),
                        mediaUri = video.videoUrl,
                        previewImageUri = "",
                        aspectRatio = null
                    )
                }
                ?.filter { videoData ->
//                    videoData.id.isNotBlank()
                        /*&&*/ videoData.mediaUri.isNotBlank()
                        /*&& videoData.previewImageUri.isNotBlank()*/
                }
                .orEmpty()

            emit(videoData)
        } catch (throwable: Throwable) {
            if (throwable is CancellationException) throw throwable
            Log.d("asdf", "Error", throwable)
        }
    }

    private interface RedditService {
        @GET("dev/shorts/api/get_shorts.php")
        suspend fun tikTokCringe(
//            @Path("sort") sort: String? = "top",
//            @Query("t") top: String? = "today"
        ): RedditResponse
    }

    @JsonClass(generateAdapter = true)
data class RedditResponse(
    @Json(name = "data")
    val `data`: List<Data>,
    @Json(name = "status")
    val status: Boolean
)

@JsonClass(generateAdapter = true)
data class Data(
    @Json(name = "comment_count")
    val commentCount: String,
    @Json(name = "following")
    val following: Boolean,
    @Json(name = "like_count")
    val likeCount: String,
    @Json(name = "title")
    val title: String,
    @Json(name = "video_url")
    val videoUrl: String,
    val id:String?= "",
    val preview:String?= ""
)

//    @JsonClass(generateAdapter = true)
//    internal data class RedditResponse(
//        val data: Data1?
//    ) {
//        @JsonClass(generateAdapter = true)
//        data class Data1(
//            @Json(name = "data")
//            val posts: List<Post>?
//        ) {
//            @JsonClass(generateAdapter = true)
//            data class Post(
//                val data: Data2?
//            ) {
//                @JsonClass(generateAdapter = true)
//                data class Data2(
//                    val id: String,
//                    @Json(name = "secure_media")
//                    val secureMedia: SecureMedia?,
//                    val preview: Preview?
//                ) {
//                    @JsonClass(generateAdapter = true)
//                    data class SecureMedia(
//                        @Json(name = "reddit_video")
//                        val video: Video?
//                    ) {
//                        @JsonClass(generateAdapter = true)
//                        data class Video(
//                            val width: Int?,
//                            val height: Int?,
//                            val duration: Int?,
//                            @Json(name = "hls_url")
//                            val hlsUrl: String?,
//                            @Json(name = "dash_url")
//                            val dashUrl: String?
//                        )
//                    }
//
//                    @JsonClass(generateAdapter = true)
//                    data class Preview(
//                        val images: List<Image>?
//                    ) {
//                        @JsonClass(generateAdapter = true)
//                        data class Image(
//                            val source: Source?
//                        ) {
//                            @JsonClass(generateAdapter = true)
//                            data class Source(
//                                val url: String?,
//                                val width: Int?,
//                                val height: Int?
//                            )
//                        }
//                    }
//                }
//            }
//        }
//    }
}
