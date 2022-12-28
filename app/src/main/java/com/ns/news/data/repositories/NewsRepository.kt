package com.ns.news.data.repositories

import com.news.data.api.NewsApi
import com.news.data.api.mappers.SectionMapper
import com.news.domain.Result
import com.ns.news.domain.repositories.ApiRepository
import com.news.domain.requireValue
import com.news.utils.DispatchersProvider
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

    override suspend fun getLanguag(): Result<String> {
        TODO("Not yet implemented")
    }

    /*override suspend fun getLanguag() = withContext(dispatchersProvider.io()) {
        Result {
            apis.getLanguag()
        }
    }*/

    override suspend fun getSections() = withContext(dispatchersProvider.io()) {
        Result {
            apis.getSection().data.sections
                .orEmpty()
                .map { sectionMapper.toDomain(it) }
                .map { it.requireValue() }
        }
    }



}