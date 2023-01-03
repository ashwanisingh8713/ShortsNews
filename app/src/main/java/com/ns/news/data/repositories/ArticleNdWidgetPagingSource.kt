/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ns.news.data.repositories

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ns.news.data.api.NewsApi
import com.ns.news.data.mappers.DataMapper
import com.ns.news.domain.model.Cell

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
