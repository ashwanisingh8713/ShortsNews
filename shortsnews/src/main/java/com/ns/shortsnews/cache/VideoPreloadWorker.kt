package com.ns.shortsnews.cache

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.work.*
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.HttpDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.CacheWriter
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.ns.shortsnews.MainApplication
import kotlinx.coroutines.*
import java.util.UUID

class VideoPreloadWorker(private val context: Context, workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {

    private var videoCachingJob: Job? = null
    private lateinit var mHttpDataSourceFactory: HttpDataSource.Factory
    private lateinit var mDefaultDataSourceFactory: DefaultDataSourceFactory
    private lateinit var mCacheDataSource: CacheDataSource
    private val cache: SimpleCache = MainApplication.cache
    private lateinit var videoUrls: Array<String>
    private lateinit var videoIds: Array<String>
    private var onGoingCacheIndex = 0



    companion object {
        private const val TAG = "VideoPreload"
        const val VIDEO_URLs = "video_urls"
        const val VIDEO_IDs = "video_ids"
        private var WorkerRequestUid: UUID = UUID.randomUUID()

        fun schedulePreloadWork(videoUrls: Array<String>, ids: Array<String>) {
            if(ids.isEmpty() || videoUrls.isEmpty()) {
                return
            }
            cancelRunningWorkRequest(MainApplication.applicationContext())
            val workManager = WorkManager.getInstance(MainApplication.applicationContext())
            val videoPreloadWorker = buildWorkRequest(videoUrls, ids)
            val uniqueWorkName = "workName_${ids[0]}"
            workManager.beginUniqueWork(
                uniqueWorkName,
                ExistingWorkPolicy.KEEP,
                videoPreloadWorker
            ).enqueue()
        }

        fun cancelRunningWorkRequest(context: Context) {
            WorkManager.getInstance(context).cancelWorkById(WorkerRequestUid)
        }

        private fun buildWorkRequest(videoUrls: Array<String>, ids: Array<String>): OneTimeWorkRequest {
            Log.i(TAG, "buildWorkRequest")
            val data = Data.Builder()
                .putStringArray(VIDEO_IDs, ids)
                .putStringArray(VIDEO_URLs, videoUrls)
                .build()
            val workRequest = OneTimeWorkRequestBuilder<VideoPreloadWorker>().apply { setInputData(data) }
                .build()
            WorkerRequestUid = workRequest.id
            return workRequest
        }
    }



    override fun doWork(): Result {
        videoUrls = inputData.getStringArray(VIDEO_URLs)!!
        videoIds = inputData.getStringArray(VIDEO_IDs)!!
        try {
            Log.i(TAG, "")
            Log.i(TAG, "=================================================")

            mHttpDataSourceFactory = DefaultHttpDataSource.Factory()
                .setAllowCrossProtocolRedirects(true)

            mDefaultDataSourceFactory = DefaultDataSourceFactory(context, mHttpDataSourceFactory)

            mCacheDataSource = CacheDataSource.Factory()
                .setCache(cache)
                .setUpstreamDataSourceFactory(mHttpDataSourceFactory)
                .createDataSource()

            nextVideoCaching()

            return Result.success()

        } catch (e: Exception) {
            Log.i(TAG, "doWork() :: Error :: videoId, ${videoIds[onGoingCacheIndex]} :: ${e.message}")
            return Result.failure()
        }
    }

    private fun preCacheVideo(videoUrl: String, videoId: String) {
        Log.i(TAG, "Started Caching of :: VideoId :: $videoId")
        Log.i(TAG, "Started Caching of :: VideoUrl :: $videoUrl")

        val videoUri = Uri.parse(videoUrl)
        val dataSpec = DataSpec(videoUri)

        val progressListener = CacheWriter.ProgressListener { requestLength, bytesCached, _ ->
            var downloadPercentage: Double = (bytesCached * 100.0 / requestLength)

            when(downloadPercentage) {
                100.0-> {
                    nextVideoCaching()
                }
                10.0-> {
                    Log.i(TAG, "VideoId :: $videoId, downloadPercentage = $downloadPercentage")
                }
                30.0-> {
                    Log.i(TAG, "VideoId :: $videoId, downloadPercentage = $downloadPercentage")
                }
                70.0-> {
                    Log.i(TAG, "VideoId :: $videoId, downloadPercentage = $downloadPercentage")
                }
                90.0-> {
                    Log.i(TAG, "VideoId :: $videoId, downloadPercentage = $downloadPercentage")
                }
            }



            // Do Something
        }

        videoCachingJob = GlobalScope.launch(Dispatchers.IO) {
            cacheVideo(videoId, dataSpec, progressListener)
        }
    }

    private fun cacheVideo(videoId: String, mDataSpec: DataSpec, mProgressListener: CacheWriter.ProgressListener) {
        Log.i(TAG, "cacheVideo() :: videoId, $videoId ")
        runCatching {
            CacheWriter(
                mCacheDataSource,
                mDataSpec,
                null,
                mProgressListener,
            ).cache()
        }.onFailure {
            it.printStackTrace()
            Log.i(TAG, "cacheVideo() :: Error :: videoId = $videoId, ${it.message}")
            nextVideoCaching()
        }.onSuccess {

        }
    }

    private fun nextVideoCaching() {
        val size = videoUrls.size
        if(size == 0) {
            return
        }
        if(onGoingCacheIndex<size-1) {
            onGoingCacheIndex++
            preCacheVideo(videoUrls[onGoingCacheIndex], videoIds[onGoingCacheIndex])
        }
    }





}