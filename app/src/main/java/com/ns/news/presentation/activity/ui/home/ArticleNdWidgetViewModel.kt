package com.ns.news.presentation.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ns.news.data.api.model.CellsItem
import com.ns.news.domain.model.Cell
import com.ns.news.domain.repositories.ApiRepository
import kotlinx.coroutines.flow.Flow

class ArticleNdWidgetViewModel(private val repository: ApiRepository) : ViewModel() {

    private var articleNdWidgetResult: Flow<PagingData<Cell>>? = null

    suspend fun getArticleNdWidget(sectionId: String): Flow<PagingData<Cell>> {
        val newResult: Flow<PagingData<Cell>> =
            repository.getArticleNdWidget(sectionId).cachedIn(viewModelScope)
        articleNdWidgetResult = newResult
        return newResult
    }

}