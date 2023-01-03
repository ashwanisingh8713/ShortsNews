package com.ns.news.data.repositories

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.ns.news.data.api.NewsApi
import com.ns.news.data.mappers.DataMapper
import com.ns.news.domain.Result
import com.ns.news.domain.repositories.ApiRepository
import com.ns.news.domain.requireValue
import com.news.utils.DispatchersProvider
import com.ns.news.data.db.NewsDb
import com.ns.news.data.db.Section
import com.ns.news.data.mappers.ArticlePageKeyedRemoteMediator
import com.ns.news.data.mappers.SectionRemoteMediator
import kotlinx.coroutines.flow.Flow
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

    @OptIn(ExperimentalPagingApi::class)
    override suspend fun getSectionsDB(languageId: String) = Pager(
    config = PagingConfig(50),
    remoteMediator = SectionRemoteMediator(db, newsApi = apis, languageId, mapper)
    ) {
        db.sectionDao().getSections()
    }.flow

    override suspend fun getSectionsDirect(languageId: String) = withContext(dispatchersProvider.io()) {
        Result {
            apis.getSection(languageId).data.sections
                .orEmpty()
                .map { mapper.toDomain(it, "") }
        }
    }

    /*override fun getArticleNdWidget(sectionId: String, url: String): Flow<PagingData<Cell>> {
        return Pager(
            config = PagingConfig(enablePlaceholders = true, pageSize = NETWORK_PAGE_SIZE),
            pagingSourceFactory = { ArticleNdWidgetPagingSource(apis, url) }
        ).flow
    }*/

    @OptIn(ExperimentalPagingApi::class)
    override suspend fun getArticleNdWidget(sectionId: String, url: String) = Pager(
        config = PagingConfig(5),
        remoteMediator = ArticlePageKeyedRemoteMediator(db, newsApi = apis, sectionId, url, mapper)
    ) {
        db.cellItems().articleBySectionId(sectionId)
    }.flow

    companion object {
        private const val NETWORK_PAGE_SIZE = 10
    }


    


}