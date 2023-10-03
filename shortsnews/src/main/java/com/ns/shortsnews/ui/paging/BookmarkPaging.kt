package com.ns.shortsnews.ui.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ns.shortsnews.data.source.UserApiService
import com.ns.shortsnews.domain.models.Data
import kotlin.coroutines.cancellation.CancellationException

/**
 * Created by Ashwani Kumar Singh on 28,September,2023.
 */
class BookmarkPaging(private val userApiService: UserApiService): PagingSource<Int, Data>() {

    override fun getRefreshKey(state: PagingState<Int, Data>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Data> {
        return try {
            val perPage = 30
            val nextPageNumber = params.key ?: 1
            val response = userApiService.getBookmarksDataP(page=nextPageNumber, perPage=perPage)
            LoadResult.Page(
                data = response.data,
                prevKey = null,
                nextKey = if (response.data.size == perPage) nextPageNumber + 1 else null
            )
        } catch (ce: CancellationException) {
            // You can ignore or log this exception
            Log.i("AshwaniXYZ", "Error: ${ce.localizedMessage}")
            LoadResult.Invalid()
        } catch (e: Exception) {
            Log.i("AshwaniXYZ", "Error: ${e.localizedMessage}")
            LoadResult.Error(e)
        }
    }


}