package com.ns.shortsnews.video.data

import android.content.Context
import android.util.Log
import at.huber.me.YouTubeUri
import com.ns.shortsnews.BuildConfig
import com.ns.shortsnews.cache.VideoPreloadCoroutine
import com.ns.shortsnews.user.domain.models.Data
import com.ns.shortsnews.user.domain.models.LikeUnlike
import com.ns.shortsnews.user.domain.models.VideoDataResponse
import com.ns.shortsnews.utils.AppPreference
import com.player.models.VideoData
import com.videopager.data.*
import com.videopager.utils.CategoryConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
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

class VideoDataRepositoryImpl : VideoDataRepository {

    private val api = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create())
        .baseUrl(BuildConfig.BASE_URL)
        .client(OkHttpClient.Builder().addInterceptor{ chain ->
            AppPreference.userToken?.let { Log.i("auth", it) }
           if(AppPreference.userToken?.isNotEmpty() == true){
            val request = chain.request().newBuilder().addHeader("Authorization", "Bearer ${AppPreference.userToken}").build()
            chain.proceed(request)
               } else {
               val request = chain.request().newBuilder().build()
               chain.proceed(request)
           }
        }.build())
        .build()
        .create(VideoDataService::class.java)


    override suspend fun videoData(id: String, context: Context, videoFrom: String): Flow<MutableList<VideoData>> {
        var ll = mutableListOf<MutableList<VideoData>>()
        val llVid = withContext(Dispatchers.IO) {
            val response = when (videoFrom) {
                CategoryConstants.CHANNEL_VIDEO_DATA -> api.getChannelVideos(id)
                CategoryConstants.BOOKMARK_VIDEO_DATA -> api.getBookmarkVideos()
                CategoryConstants.DEFAULT_VIDEO_DATA -> api.getShortsVideos(id)
                    else -> api.getShortsVideos(id)
            }

            val youtubeUriConversionCount = 2
            val precachingAllowedCount = response.data.size
            var conversionCount = 0
            var videoUrls = Array(precachingAllowedCount){""}
            var videoIds = Array(precachingAllowedCount){""}

            val videoData = response.data
                .mapIndexed {  index, post ->
                    if (post.type!="yt" || youtubeUriConversionCount <= conversionCount) {
                        if(index < precachingAllowedCount) {
                            videoUrls[index]=post.videoUrl
                            videoIds[index]=post.id
                        }
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
                        var finalUri18 = ""
                        if (ytFiles != null && ytFiles.contains(YouTubeUri.iTag)) {
                            finalUri18 = ytFiles[YouTubeUri.iTag].url
                            conversionCount++
                        }
                        if(index < precachingAllowedCount) {
                            videoUrls[index]=finalUri18
                            videoIds[index]=post.id
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

            // Preload Video urls
//            VideoPreloadWorker.schedulePreloadWork(videoUrls, videoIds)
            VideoPreloadCoroutine.schedulePreloadWork(videoUrls, videoIds)

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

    override fun save(videoId: String, position: Int): Flow<Triple<String, Boolean, Int>>  = flow{
        try {
            val res = api.save(videoId)
            emit(Triple(res.data.saved_count, res.data.saved, position))
        } catch (ec :java.lang.Exception) {
            Log.i("kamels","$ec")
        }
    }


    override fun follow(channel_id: String, position: Int): Flow<Pair<Following, Int>> = flow {
        try {
            val data =  api.follow(channel_id)
            Log.i("getVideoInfo", "follow :: ${data.data.following} ")
            Log.i("getVideoInfo", "channel id :: ${data.data.channel_id} ")
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

        Log.i("getVideoInfo", "getVideoInfo :: $position")
        try {
            val data = api.getVideoInfo(videoId)
            if(data.status) {
                emit(Pair(data.data, position))
                Log.i("getVideoInfo", "getVideoInfo_id :: ${data.data.id}")
                Log.i("getVideoInfo", "getVideoInfo_following :: ${data.data.following}")
            } else {
                emit(Pair(VideoInfoData(), position))
                Log.i("getVideoInfo", "status :: ${data.status}")

            }

            Log.i("getVideoInfo", "Response :: ${data.status}")

        } catch (ec :java.lang.Exception) {
            Log.i("getVideoInfo", "Error :: ${ec.message}")
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

        @GET("videos")
        suspend fun getChannelVideos(@Query("channel") category: String): VideoDataResponse

        @GET("my-bookmarks")
        suspend fun getBookmarkVideos(): VideoDataResponse

        @GET("like-unlike-video/{video_id}")
        suspend fun like(@Path("video_id")videoId: String): LikeUnlike

        @GET("bookmark-video/{video_id}")
        suspend fun save(@Path("video_id")videoId: String): BookMarkResult
        @GET("follow-unfollow-channel/{channel_id}")
        suspend fun follow(@Path("channel_id")channel_id: String): Following
        @GET("get-comments/{video_id}")
        suspend fun comment(@Path("video_id") videoId: String): Comments
        @GET("video-info/{video_id}")
        suspend fun getVideoInfo(@Path("video_id")videoId: String): VideoInfo
        @POST("comment-video/{video_id}")
        suspend fun getPostComment(@Path("video_id") videoId:String, @Body comment:Map<String, String>): PostComment
    }


}
