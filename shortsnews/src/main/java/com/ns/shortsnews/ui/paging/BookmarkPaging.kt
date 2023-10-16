package com.ns.shortsnews.ui.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ns.shortsnews.data.source.UserApiService
import com.ns.shortsnews.domain.models.Data
import com.player.models.VideoData
import kotlin.coroutines.cancellation.CancellationException

/**
 * Created by Ashwani Kumar Singh on 28,September,2023.
 */
class BookmarkPaging(private val userApiService: UserApiService): PagingSource<Int, VideoData>() {

    companion object {
        internal const val INITIAL_PAGE = 1
    }

    override fun getRefreshKey(state: PagingState<Int, VideoData>): Int? {
        // This method is called when invalidate is invoked on the PagingSource.
        // Here, you can return a key that represents the initial page to load when refreshing.
        // In this example, we return the initial page number.
        return INITIAL_PAGE
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, VideoData> {

        return try {

            val perPage = 5//params.loadSize, here I don't understand why it is taking 15 as loadSize
            val nextPageNumber = params.key ?: 1
            val response = userApiService.getBookmarksDataP(page=nextPageNumber, perPage=perPage)

            val videoData = response.data
                .mapIndexed { index, post ->
                    val width = post?.width
                    val height = post?.height
                    val aspectRatio = if (width != null && height != null) {
                        width.toFloat() / height.toFloat()
                    } else {
                        null
                    }
                    VideoData(
                        id = post.id,
                        mediaUri = post.videoUrl,
                        previewImageUri = post.preview!!,
                        aspectRatio = aspectRatio,
                        type = post.type,
                        video_url_mp4 = post.video_url_mp4,
                        page = response.page,
                        perPage = response.perPage,
                        liking = post.liked,
                    )
                }.filter {
                    it.mediaUri.isNotBlank()
                }
            Log.i("AshwaniXYZX", "BookmarkPaging Success ::  ParPage = $perPage,  Received Count =  ${videoData.size}, PageNumber = $nextPageNumber")

            LoadResult.Page(
                data = videoData,
                prevKey = null,
                nextKey = if (response.data.size == perPage) nextPageNumber + 1 else null
            )
        } catch (ce: CancellationException) {
            Log.i("AshwaniXYZX", "BookmarkPaging :: CancellationException ::  $ce")
            // You can ignore or log this exception
            LoadResult.Invalid()

        } catch (e: Exception) {
            Log.i("AshwaniXYZX", "BookmarkPaging :: Exception :: $e")
            LoadResult.Error(e)
        }
    }


}