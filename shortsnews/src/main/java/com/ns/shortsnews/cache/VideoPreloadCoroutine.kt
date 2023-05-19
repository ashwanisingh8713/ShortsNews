package com.ns.shortsnews.cache

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.work.*
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.CacheWriter
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.ns.shortsnews.MainApplication
import kotlinx.coroutines.*
import java.util.*

class VideoPreloadCoroutine(private val context: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters) {

    //    private lateinit var mHttpDataSourceFactory: HttpDataSource.Factory
//    private lateinit var mCacheDataSource: CacheDataSource
    private val cache: SimpleCache = MainApplication.cache
    private lateinit var videoUrls: Array<String>
    private lateinit var videoIds: Array<String>
    private var onGoingCacheIndex = -1


    companion object {
        private const val TAG = "VideoPreload"
        private var IS_PARALLEL_DOWNLOADING = false
        const val VIDEO_URLs = "video_urls"
        const val VIDEO_IDs = "video_ids"
        private var WorkerRequestUid: UUID = UUID.randomUUID()

        val bytes = CacheWriter.DEFAULT_BUFFER_SIZE_BYTES
        var byteArray = ByteArray(bytes)

        fun schedulePreloadWork(videoUrls: Array<String>, ids: Array<String>) {
            if (ids.isEmpty() || videoUrls.isEmpty()) {
                return
            }

            // Cancelling the already existing Jobs, before starting new Job
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
            val workRequest = OneTimeWorkRequestBuilder<VideoPreloadCoroutine>()
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


    override suspend fun doWork(): Result {
        videoUrls = inputData.getStringArray(VIDEO_URLs)!!
        videoIds = inputData.getStringArray(VIDEO_IDs)!!
        try {
            Log.i(TAG, "")
            Log.i(TAG, "=================================================")

            if (IS_PARALLEL_DOWNLOADING) { // Parallel
                var cacheList = withContext(Dispatchers.IO) {
                    var ll = mutableListOf<Job>()
                    videoUrls.forEachIndexed { index, element ->
                        ll.add(launch {
                            parallelCachingExecution(videoUrls[index], videoIds[index])
                        })
                    }
                    ll
                }
                CoroutineScope(Dispatchers.Default).launch {
                    cacheList.joinAll()
                }
            } else { // One By One
                oneByOneCaching()
            }

            return Result.success()

        } catch (e: Exception) {
            Log.i(TAG, "doWork() :: Error :: ${e.message}")
            return Result.failure()
        }
    }


    private fun parallelCachingExecution(
        videoUrl: String,
        videoId: String
    ) {
        var startedTime = System.currentTimeMillis()
        Log.i(TAG, "Started Caching of :: $videoId :: $videoUrl")
        val videoUri = Uri.parse(videoUrl)
        val dataSpec = DataSpec(videoUri)

        val mHttpDataSourceFactory = DefaultHttpDataSource.Factory()
            .setAllowCrossProtocolRedirects(true)

        val mCacheDataSource = CacheDataSource.Factory()
            .setCache(cache)
            .setUpstreamDataSourceFactory(mHttpDataSourceFactory)
            .createDataSource()

        val progressListener = CacheWriter.ProgressListener { requestLength, bytesCached, _ ->
            //var downloadPercentage: Double = (bytesCached * 100.0 / requestLength)
        }
        runCatching {
            CacheWriter(
                mCacheDataSource,
                dataSpec,
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
            Log.i(TAG, "Video id $videoId Downloaded in secs $seconds using byteArray of $bytes")
            Log.i(TAG, " ")
        }
    }


    private suspend fun oneByOneCachingExecution(videoUrl: String, videoId: String) {
        var startedTime = System.currentTimeMillis()

        withContext(Dispatchers.IO) {
            Log.i(TAG, "Started Caching of :: $videoId :: $videoUrl")
            val videoUri = Uri.parse(videoUrl)
            val dataSpec = DataSpec(videoUri)

            val mHttpDataSourceFactory = DefaultHttpDataSource.Factory()
                .setAllowCrossProtocolRedirects(true)

            val mCacheDataSource = CacheDataSource.Factory()
                .setCache(cache)
                .setUpstreamDataSourceFactory(mHttpDataSourceFactory)
                .createDataSource()

            val progressListener = CacheWriter.ProgressListener { requestLength, bytesCached, _ ->
                //var downloadPercentage: Double = (bytesCached * 100.0 / requestLength)
            }
            runCatching {
                CacheWriter(
                    mCacheDataSource,
                    dataSpec,
                    byteArray,
                    progressListener,
                ).cache()
            }.onFailure {
                Log.i(TAG, "Download Failed :: VideoId :: $videoId :: Error is- ${it.message}")
                Log.i(TAG, " ")
                oneByOneCaching()
            }.onSuccess {
                Log.i(TAG, "Download Completed :: VideoId :: $videoId")
                var milliseconds = System.currentTimeMillis() - startedTime
                val seconds = (milliseconds / 1000)
                Log.i(
                    TAG,"Video id $videoId Downloaded in secs $seconds using byteArray of $bytes"
                )
                Log.i(TAG, " ")
                oneByOneCaching()
            }
        }
    }

    private suspend fun oneByOneCaching() {
        val size = videoUrls.size
        if (size == 0) {
            return
        }
        if (onGoingCacheIndex < size-1) {
            onGoingCacheIndex++
            oneByOneCachingExecution(videoUrls[onGoingCacheIndex], videoIds[onGoingCacheIndex])
        }
    }


}