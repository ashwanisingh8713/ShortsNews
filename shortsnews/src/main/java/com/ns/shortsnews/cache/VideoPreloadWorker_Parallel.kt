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
import java.util.*

class VideoPreloadWorker_Parallel(private val context: Context, workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {

    private lateinit var mHttpDataSourceFactory: HttpDataSource.Factory
    private lateinit var mDefaultDataSourceFactory: DefaultDataSourceFactory
    private lateinit var mCacheDataSource: CacheDataSource
    private val cache: SimpleCache = MainApplication.cache
    private lateinit var videoUrls: Array<String>
    private lateinit var videoIds: Array<String>
    private var onGoingCacheIndex = 0
    private var onGoingVideoId = ""
    private var onGoingVideoUrl = ""
    private var startedTime:Long = 0L


    companion object {
        private const val TAG = "VideoPreload"
        const val VIDEO_URLs = "video_urls"
        const val VIDEO_IDs = "video_ids"
        private var WorkerRequestUid: UUID = UUID.randomUUID()
        private var IS_PARALLEL_DOWNLOADING = true

        val bytes = CacheWriter.DEFAULT_BUFFER_SIZE_BYTES
        var byteArray = ByteArray(bytes)

        fun schedulePreloadWork(videoUrls: Array<String>, ids: Array<String>) {
            if (ids.isEmpty() || videoUrls.isEmpty()) {
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

        private fun cancelRunningWorkRequest(context: Context) {
            WorkManager.getInstance(context).cancelWorkById(WorkerRequestUid)
            Log.i(TAG, "Cancelled Download request :: $WorkerRequestUid")
            Log.i(TAG, "----------------------------------------------------")
        }

        private fun buildWorkRequest(
            videoUrls: Array<String>,
            ids: Array<String>
        ): OneTimeWorkRequest {

            val constraintsBuilder: Constraints.Builder = Constraints.Builder()
            constraintsBuilder.setRequiredNetworkType(NetworkType.CONNECTED)
            val constraints = constraintsBuilder.build()

            val data = Data.Builder()
                .putStringArray(VIDEO_IDs, ids)
                .putStringArray(VIDEO_URLs, videoUrls)
                .build()
            val workRequest = OneTimeWorkRequestBuilder<VideoPreloadWorker_Parallel>()
                .apply {
                    setInputData(data)
                    setConstraints(constraints)
                }
                .build()
            WorkerRequestUid = workRequest.id
            Log.i(TAG, "New Download Request is created :: $WorkerRequestUid")
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

            if(IS_PARALLEL_DOWNLOADING) {
                videoUrls.forEachIndexed { index, element ->
                    PARALLEL_preCacheVideo(videoUrl = videoUrls[index], videoId = videoIds[index])
                }
            } else {
                nextVideoCaching()
            }

            return Result.success()

        } catch (e: Exception) {
            Log.i(TAG, "doWork() :: Error :: videoId, $onGoingVideoId :: ${e.message}")
            return Result.failure()
        }
    }

    private fun PARALLEL_preCacheVideo(videoUrl: String, videoId: String) {
        Log.i(TAG, "Started Caching of :: $videoId :: $videoUrl")
        startedTime = System.currentTimeMillis()

        var videoCachingJob = GlobalScope.launch(Dispatchers.IO) {
            val videoUri = Uri.parse(videoUrl)
            val dataSpec = DataSpec(videoUri)
            PARALLEL_cacheVideo(videoId, dataSpec)
        }

    }

    private fun PARALLEL_cacheVideo(
        videoId: String,
        mDataSpec: DataSpec,
    ) {
        val progressListener = CacheWriter.ProgressListener { requestLength, bytesCached, _ ->
            //var downloadPercentage: Double = (bytesCached * 100.0 / requestLength)
        }
        runCatching {
            CacheWriter(
                mCacheDataSource,
                mDataSpec,
                byteArray,
                progressListener,
            ).cache()
        }.onFailure {
            Log.i(TAG, "Download Failed :: VideoId :: $videoId :: Error is- ${it.message}")
            Log.i(TAG, " ")
        }.onSuccess {
            Log.i(TAG, "Download Completed :: VideoId :: $videoId")
            var milliseconds = System.currentTimeMillis() - startedTime
            val seconds = (milliseconds / 1000)
            Log.i("kamlesh_time", "Video id $videoId Downloaded in secs $seconds using byteArray of $bytes")
            Log.i(TAG, " ")
        }
    }



    private fun preCacheVideo(videoUrl: String, videoId: String) {
        Log.i(TAG, "Started Caching of :: $videoId :: $videoUrl")
        startedTime = System.currentTimeMillis()

        val videoUri = Uri.parse(videoUrl)
        val dataSpec = DataSpec(videoUri)

        var videoCachingJob = GlobalScope.launch(Dispatchers.IO) {
            cacheVideo(videoId, dataSpec)
        }

    }

    private fun cacheVideo(
        videoId: String,
        mDataSpec: DataSpec,
    ) {
        val progressListener = CacheWriter.ProgressListener { requestLength, bytesCached, _ ->
            //var downloadPercentage: Double = (bytesCached * 100.0 / requestLength)
        }
        runCatching {
            CacheWriter(
                mCacheDataSource,
                mDataSpec,
                byteArray,
                progressListener,
            ).cache()
        }.onFailure {
            Log.i(TAG, "Download Failed :: VideoId :: $videoId :: Error is- ${it.message}")
            Log.i(TAG, " ")
            nextVideoCaching()
        }.onSuccess {
            Log.i(TAG, "Download Completed :: VideoId :: $videoId")
            var milliseconds = System.currentTimeMillis() - startedTime
            val seconds = (milliseconds / 1000)
            Log.i("kamlesh_time", "Video id $videoId Downloaded in secs $seconds using byteArray of $bytes")
            Log.i(TAG, " ")
            nextVideoCaching()
        }
    }

    private fun nextVideoCaching() {
        val size = videoUrls.size
        if (size == 0) {
            return
        }
        if (onGoingCacheIndex < size) {
            onGoingVideoUrl = videoUrls[onGoingCacheIndex]
            onGoingVideoId = videoIds[onGoingCacheIndex]
            preCacheVideo(onGoingVideoUrl, onGoingVideoId)
            onGoingCacheIndex++
        }
    }


}