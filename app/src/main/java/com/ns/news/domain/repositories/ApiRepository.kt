package com.ns.news.domain.repositories

import androidx.paging.PagingData
import com.news.data.api.model.CategoryPageData
import com.ns.news.data.api.model.LanguagePageData
import com.ns.news.data.api.model.SectionItem
import com.ns.news.domain.Result
import com.ns.news.data.db.Cell
import com.ns.news.data.db.Section
import kotlinx.coroutines.flow.Flow


interface ApiRepository {
  suspend fun getCategoryPageData(): Result<CategoryPageData>
  suspend fun getLanguage(): Result<LanguagePageData>
  suspend fun getSectionsDirect(languageId: String): Result<List<SectionItem>>
  suspend fun getSectionsDB(languageId: String): Flow<PagingData<Section>>
  suspend fun getArticleNdWidget(sectionId: String, url: String): Flow<PagingData<Cell>>
}