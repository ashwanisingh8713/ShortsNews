package com.ns.news.data.repositories

import com.ns.news.data.api.NewsApi
import com.news.data.api.mappers.SectionMapper
import com.ns.news.domain.Result
import com.ns.news.domain.repositories.ApiRepository
import com.ns.news.domain.requireValue
import com.news.utils.DispatchersProvider
import com.ns.news.data.api.model.ArticleNdWidgetResponse
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

    override suspend fun getArticleNdWidget(url: String) = withContext(dispatchersProvider.io()) {
        Result {
            apis.getArticleNdWidget(url)
        }
    }


}