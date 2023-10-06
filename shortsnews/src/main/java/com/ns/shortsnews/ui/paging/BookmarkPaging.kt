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

    override fun getRefreshKey(state: PagingState<Int, VideoData>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, VideoData> {
        return try {
            val perPage = 5
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
                        perPage = response.perPage
                    )
                }.filter {
                    it.mediaUri.isNotBlank()
                }

            LoadResult.Page(
                data = videoData,
                prevKey = null,
                nextKey = if (response.data.size == perPage) nextPageNumber + 1 else null
            )
        } catch (ce: CancellationException) {
            // You can ignore or log this exception
            LoadResult.Invalid()
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }


}