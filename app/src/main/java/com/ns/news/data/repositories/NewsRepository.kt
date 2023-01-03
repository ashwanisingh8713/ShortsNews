package com.ns.news.data.repositories

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.ns.news.data.api.NewsApi
import com.ns.news.data.mappers.DataMapper
import com.ns.news.domain.Result
import com.ns.news.domain.repositories.ApiRepository
import com.ns.news.domain.requireValue
import com.news.utils.DispatchersProvider
import com.ns.news.data.db.NewsDb
import com.ns.pagingwithnetwork.reddit.repository.articleDb.ArticlePageKeyedRemoteMediator
import kotlinx.coroutines.withContext

class NewsRepository(
    private val dispatchersProvider: DispatchersProvider,
    private val mapper: DataMapper,
    private val apis: NewsApi,
    private val db: NewsDb
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
                .map { mapper.toDomain(it) }
                .map { it.requireValue() }
        }
    }

    /*override fun getArticleNdWidget(sectionId: String, url: String): Flow<PagingData<Cell>> {
        return Pager(
            config = PagingConfig(enablePlaceholders = true, pageSize = NETWORK_PAGE_SIZE),
            pagingSourceFactory = { ArticleNdWidgetPagingSource(apis, url) }
        ).flow
    }*/

    @OptIn(ExperimentalPagingApi::class)
    override fun getArticleNdWidget(sectionId: String, url: String) = Pager(
        config = PagingConfig(5),
        remoteMediator = ArticlePageKeyedRemoteMediator(db, newsApi = apis, sectionId, url, mapper)
    ) {
        db.cellItems().articleBySectionId(sectionId)
    }.flow

    companion object {
        private const val NETWORK_PAGE_SIZE = 10
    }


    


}