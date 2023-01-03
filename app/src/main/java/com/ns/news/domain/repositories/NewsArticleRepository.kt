package com.ns.news.domain.repositories

import androidx.paging.PagingData
import com.ns.news.domain.model.Cell
import kotlinx.coroutines.flow.Flow

/**
 * Common interface shared by the different repository implementations.
 * Note: Typically an app would implement a repo once, either
 * network+db, or network-only
 */
interface NewsArticleRepository {
    fun getArticleNdWidget(sectionId: String, url: String): Flow<PagingData<Cell>>

    enum class Type {
        DB,
        IN_MEMORY_BY_PAGE
    }
}