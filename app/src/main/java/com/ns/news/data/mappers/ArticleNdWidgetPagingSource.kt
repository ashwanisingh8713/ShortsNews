package com.ns.news.data.mappers

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ns.news.data.api.NewsApi
import com.ns.news.data.db.Cell

private const val UNSPLASH_STARTING_PAGE_INDEX = 1

class ArticleNdWidgetPagingSource(
    private val apis: NewsApi,
    private val url: String,
    private val mapper: DataMapper,
) : PagingSource<Int, Cell>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Cell> {
        val page = params.key ?: UNSPLASH_STARTING_PAGE_INDEX
        return try {
            val response = apis.getArticleNdWidget(url+page)
            val cellsItems = response.data.cells
            var pag = page
            var prevKey = if (page == UNSPLASH_STARTING_PAGE_INDEX) null else page - 1
            var nextKey = if (page == response.data.total) null else page + 1

            Log.i("Ashwani", "######################################")
            Log.i("Ashwani", "Page :: $pag")
            Log.i("Ashwani", "PrevKey :: $prevKey")
            Log.i("Ashwani", "NextKey :: $pag")

            var cells = cellsItems
                .orEmpty()
                .map { mapper.toDomain(it) }

            LoadResult.Page(
                data = cells,
                prevKey = prevKey,
                nextKey = nextKey,
                itemsBefore = LoadResult.Page.COUNT_UNDEFINED,
                itemsAfter = LoadResult.Page.COUNT_UNDEFINED
            )
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Cell>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            // This loads starting from previous page, but since PagingConfig.initialLoadSize spans
            // multiple pages, the initial load will still load items centered around
            // anchorPosition. This also prevents needing to immediately launch prepend due to
            // prefetchDistance.
            state.closestPageToPosition(anchorPosition)?.prevKey
        }
    }
}
