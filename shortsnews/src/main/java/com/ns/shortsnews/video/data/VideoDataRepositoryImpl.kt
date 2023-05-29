package com.ns.shortsnews.video.data

import android.content.Context
import android.util.Log
import at.huber.me.YouTubeUri
import com.ns.shortsnews.cache.HlsPreloadCoroutine
import com.ns.shortsnews.video.data.VideoDataNetService.videoDataApiService
import com.player.models.VideoData
import com.videopager.data.*
import com.videopager.utils.CategoryConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class VideoDataRepositoryImpl : VideoDataRepository {

    override suspend fun videoData(
        id: String,
        context: Context,
        videoFrom: String
    ): Flow<MutableList<VideoData>> {
        var ll = mutableListOf<MutableList<VideoData>>()

        val llVid = withContext(Dispatchers.IO) {
            val response = when (videoFrom) {
                CategoryConstants.CHANNEL_VIDEO_DATA -> videoDataApiService.getChannelVideos(id)
                CategoryConstants.BOOKMARK_VIDEO_DATA -> videoDataApiService.getBookmarkVideos()
                CategoryConstants.DEFAULT_VIDEO_DATA -> videoDataApiService.getShortsVideos(id)
                else -> videoDataApiService.getShortsVideos(id)
            }

            val youtubeUriConversionCount = 2
            val precachingAllowedCount = response.data.size
            var conversionCount = 0
            var videoUrls = Array(precachingAllowedCount) { "" }
            var videoIds = Array(precachingAllowedCount) { "" }

            val videoData = response.data
                .mapIndexed { index, post ->
                    if (post.type != "yt" || youtubeUriConversionCount <= conversionCount) {
                        if (index < precachingAllowedCount) {
                            videoUrls[index] = post.videoUrl
                            videoIds[index] = post.id
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
                        if (index < precachingAllowedCount) {
                            videoUrls[index] = finalUri18
                            videoIds[index] = post.id
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
//            HlsPreloadCoroutine.schedulePreloadWork(videoUrls, videoIds)

            Log.i("Conv_TIME", "VideoDataRepositoryImpl")

            ll.add(videoData as MutableList)
            ll
        }

        return llVid.asFlow()
    }


    override fun like(videoId: String, position: Int): Flow<Triple<String, Boolean, Int>> = flow {
        try {
            val res = videoDataApiService.like(videoId)
            emit(Triple(res.data.like_count, res.data.liked, position))
        } catch (ec: java.lang.Exception) {
            Log.i("kamels", "$ec")
        }

    }

    override fun save(videoId: String, position: Int): Flow<Triple<String, Boolean, Int>> = flow {
        try {
            val res = videoDataApiService.save(videoId)
            emit(Triple(res.data.saved_count, res.data.saved, position))
        } catch (ec: java.lang.Exception) {
            Log.i("kamels", "$ec")
        }
    }


    override fun follow(channel_id: String, position: Int): Flow<Pair<Following, Int>> = flow {
        try {
            val data = videoDataApiService.follow(channel_id)
            Log.i("getVideoInfo", "follow :: ${data.data.following} ")
            Log.i("getVideoInfo", "channel id :: ${data.data.channel_id} ")
            emit(Pair(data, position))
        } catch (ec: java.lang.Exception) {
            Log.i("kamels", "$ec")
        }

    }

    override fun comment(videoId: String, position: Int): Flow<Triple<String, Comments, Int>> =
        flow {
            try {
                val data = videoDataApiService.comment(videoId)
                emit(Triple(videoId, data, position))
            } catch (ec: java.lang.Exception) {
                Log.i("kamels", "$ec")
            }

        }

    override fun getVideoInfo(videoId: String, position: Int): Flow<Pair<VideoInfoData, Int>> =
        flow {

            Log.i("getVideoInfo", "getVideoInfo :: $position")
            try {
                val data = videoDataApiService.getVideoInfo(videoId)
                if (data.status) {
                    emit(Pair(data.data, position))
                    Log.i("getVideoInfo", "getVideoInfo_id :: ${data.data.id}")
                    Log.i("getVideoInfo", "getVideoInfo_following :: ${data.data.following}")
                } else {
                    emit(Pair(VideoInfoData(), position))
                    Log.i("getVideoInfo", "status :: ${data.status}")

                }

                Log.i("getVideoInfo", "Response :: ${data.status}")

            } catch (ec: java.lang.Exception) {
                Log.i("getVideoInfo", "Error :: ${ec.message}")
                emit(Pair(VideoInfoData(), position))
            }

        }

    override fun getPostComment(
        videoId: String,
        comment: String,
        position: Int
    ): Flow<Pair<PostComment, Int>> = flow {
        try {
            val body = mutableMapOf<String, String>()
            body["comment"] = comment
            val data = videoDataApiService.getPostComment(videoId, body)
            emit(Pair(data, position))
        } catch (ec: java.lang.Exception) {
            Log.i("kamels", "$ec")
        }
    }





}
