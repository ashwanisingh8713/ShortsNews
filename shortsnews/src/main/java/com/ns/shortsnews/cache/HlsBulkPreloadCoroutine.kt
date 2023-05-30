package com.ns.shortsnews.cache

import android.content.Context
import android.util.Log
import androidx.work.*
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.offline.Downloader
import com.google.android.exoplayer2.source.hls.offline.HlsDownloader
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.CacheWriter
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.ns.shortsnews.MainApplication
import kotlinx.coroutines.*
import java.util.*

class HlsBulkPreloadCoroutine(private val context: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters) {

    private val cache: SimpleCache = MainApplication.cache
    private lateinit var videoUrls: Array<String>
    private lateinit var videoIds: Array<String>


    companion object {
        private const val TAG = "VideoPreload"
        const val VIDEO_URLs = "video_urls"
        const val VIDEO_IDs = "video_ids"
        private var ParallelJobRequestUid: UUID = UUID.randomUUID()


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
            WorkManager.getInstance(context).cancelWorkById(ParallelJobRequestUid)
            Log.i(TAG, "Cancelled Download request :: $ParallelJobRequestUid")
            Log.i(TAG, "----------------------------------------------------")
            Log.i(TAG, "")
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
            val workRequest = OneTimeWorkRequestBuilder<HlsBulkPreloadCoroutine>()
                .apply {
                    setInputData(data)
                    setConstraints(constraints)
                }
                .build()
            ParallelJobRequestUid = workRequest.id
            Log.i(TAG, "New Download Request is created :: $ParallelJobRequestUid")
            return workRequest
        }
    }


    override suspend fun doWork(): Result {
        videoUrls = inputData.getStringArray(VIDEO_URLs)!!
        videoIds = inputData.getStringArray(VIDEO_IDs)!!
        try {
            Log.i(TAG, "")
            Log.i(TAG, "=================================================")

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

            return Result.success()

        } catch (e: Exception) {
            Log.i(TAG, "DoWork() :: Error :: ${e.message}")
            Log.i(TAG, "##############################")
            return Result.failure()
        }
    }


    private fun parallelCachingExecution(
        videoUrl: String,
        videoId: String
    ) {
        Log.i(TAG, "Started Caching of :: $videoId :: $videoUrl")
        val dataSourceFactory = DefaultHttpDataSource.Factory()
            .setAllowCrossProtocolRedirects(true)

        val cacheDataSource = CacheDataSource.Factory()
            .setCache(cache)
            .setUpstreamDataSourceFactory(dataSourceFactory)

        val downloader =
            HlsDownloader(mediaItem(mediaUri = videoUrl, id = videoId), cacheDataSource)

        val hlsProgressListener =
            Downloader.ProgressListener { contentLength, bytesDownloaded, percentDownloaded ->
                if (percentDownloaded == 100.0f) {
                    Log.i(TAG, "$videoId :: contentLength :: $contentLength")
                    Log.i(TAG, "$videoId :: bytesDownloaded :: $bytesDownloaded")
                    Log.i(TAG, "$videoId :: percentDownloaded :: $percentDownloaded")
                    downloader.cancel()
                }
            }

        try {
            downloader.download(hlsProgressListener)
        } catch (e: Exception) {
            Log.i(TAG, "$videoId :: Error :: ${e.printStackTrace()}")
            Log.i(TAG, "##############################")
        }
    }


    private fun mediaItem(mediaUri: String, id: String): MediaItem {
        val mediaItemBuilder = MediaItem.Builder()
            .setMediaId(id)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(id)
                    .build()
            )
            .setUri(mediaUri)

        return mediaItemBuilder.build()
    }


}