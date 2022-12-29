package com.ns.news.data.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.ns.news.data.api.NewsApi
import com.news.data.api.mappers.SectionMapper
import com.ns.news.domain.Result
import com.ns.news.domain.repositories.ApiRepository
import com.ns.news.domain.requireValue
import com.news.utils.DispatchersProvider
import com.ns.news.data.api.ArticleNdWidgetPagingSource
import com.ns.news.data.api.model.CellsItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class NewsRepository(
    private val dispatchersProvider: DispatchersProvider,
    private val sectionMapper: SectionMapper,
    private val apis: NewsApi
) : ApiRepository {

    override suspend fun getCategoryPageData() = withContext(dispatchersProvider.io()) {
        Result {
            apis.getCategory().data
        }
    }

    override suspend fun getLanguage() = withContext(dispatchersProvider.io()) {
        Result {
            apis.getLanguage().data
        }
    }

    override suspend fun getSections() = withContext(dispatchersProvider.io()) {
        Result {
            apis.getSection().data.sections
                .orEmpty()
                .map { sectionMapper.toDomain(it) }
                .map { it.requireValue() }
        }
    }

    override suspend fun getArticleNdWidget(url: String): Flow<PagingData<CellsItem>> {
        return Pager(
            config = PagingConfig(enablePlaceholders = true, pageSize = NETWORK_PAGE_SIZE),
            pagingSourceFactory = { ArticleNdWidgetPagingSource(apis, url) }
        ).flow
    }

    companion object {
        private const val NETWORK_PAGE_SIZE = 10
    }


    


}