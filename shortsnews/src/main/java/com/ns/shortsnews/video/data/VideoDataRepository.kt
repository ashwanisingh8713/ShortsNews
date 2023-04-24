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

class VideoDataRepository : VideoDataRepository {
    // Kamlesh(Changed Base Url)
    private val api = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create())
        .baseUrl("https://shorts.newsdx.io/ci/api/public/")
        .build()
        .create(RedditService::class.java)

    override fun videoData(requestType: String): Flow<List<VideoData>> = flow {
        try {
            var rt = requestType
            Log.i("", "$rt")
            val response = api.getShortsVideos(rt)
            val videoData = response
                .data
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
                    videoData.id.isNotBlank()
                        && videoData.mediaUri.isNotBlank()
                        && videoData.previewImageUri.isNotBlank()
                }
                .orEmpty()

            emit(videoData)
        } catch (throwable: Throwable) {
            if (throwable is CancellationException) throw throwable
            Log.d("asdf", "Error", throwable)
        }
    }

    private interface RedditService {
        @GET("videos")
        suspend fun getShortsVideos(@Query("category") category: String): RedditResponse
    }

    @JsonClass(generateAdapter = true)
data class RedditResponse(
    @Json(name = "data")
    val `data`: List<Data>,
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
