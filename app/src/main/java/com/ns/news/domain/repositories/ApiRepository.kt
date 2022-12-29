package com.ns.news.domain.repositories

import com.news.data.api.model.CategoryPageData
import com.ns.news.data.api.model.ArticleNdWidgetResponse
import com.ns.news.data.api.model.LanguagePageData
import com.ns.news.data.api.model.SectionItem
import com.ns.news.domain.Result


interface ApiRepository {
  suspend fun getCategoryPageData(): Result<CategoryPageData>
  suspend fun getLanguage(): Result<LanguagePageData>
  suspend fun getSections(): Result<List<SectionItem>>
  suspend fun getArticleNdWidget(url: String): Result<ArticleNdWidgetResponse>
}