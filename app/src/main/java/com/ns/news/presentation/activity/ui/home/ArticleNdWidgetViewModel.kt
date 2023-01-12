package com.ns.news.presentation.activity.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ns.news.data.db.Cell
import com.ns.news.data.db.ReadDao
import com.ns.news.data.db.TableRead
import com.ns.news.domain.repositories.ApiRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.cancellable

class ArticleNdWidgetViewModel(private val repository: ApiRepository, private val readDao: ReadDao) : ViewModel() {

    suspend fun getArticleNdWidget(sectionId: String, url: String): Flow<PagingData<Cell>> {
        val newResult: Flow<PagingData<Cell>> =
            repository.getArticleNdWidget(sectionId, url).cancellable().cachedIn(viewModelScope)
        return newResult
    }

    suspend fun isArticleRead(articleId: String) = readDao.getArticle(articleId)

}