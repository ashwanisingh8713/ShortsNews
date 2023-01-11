package com.ns.news.presentation.activity.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ns.news.data.db.Cell
import com.ns.news.data.db.ReadDao
import com.ns.news.data.db.TableRead
import com.ns.news.domain.repositories.ApiRepository
import kotlinx.coroutines.flow.Flow

class ArticleNdWidgetViewModel(private val repository: ApiRepository, private val readDao: ReadDao) : ViewModel() {

    private var articleNdWidgetResult: Flow<PagingData<Cell>>? = null

    suspend fun getArticleNdWidget(sectionId: String, url: String): Flow<PagingData<Cell>> {
        val newResult: Flow<PagingData<Cell>> =
            repository.getArticleNdWidget(sectionId, url).cachedIn(viewModelScope)
        articleNdWidgetResult = newResult
        return newResult
    }

    private var readArticle: Flow<TableRead>? = null

    suspend fun isArticleRead(articleId: String) = readDao.getArticle(articleId)

}