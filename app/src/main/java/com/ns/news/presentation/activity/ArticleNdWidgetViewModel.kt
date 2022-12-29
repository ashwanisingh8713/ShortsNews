package com.ns.news.presentation.activity

import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ns.news.data.api.model.CellsItem
import com.ns.news.domain.repositories.ApiRepository
import kotlinx.coroutines.flow.Flow

class ArticleNdWidgetViewModel(private val repository: ApiRepository) : ViewModel() {

    private var currentQueryValue: String? = null
    private var currentSearchResult: Flow<PagingData<CellsItem>>? = null

    suspend fun getArticleNdWidget(queryString: String): Flow<PagingData<CellsItem>> {
        currentQueryValue = queryString
        val newResult: Flow<PagingData<CellsItem>> =
            repository.getArticleNdWidget(queryString)//.cachedIn(viewModelScope)
        currentSearchResult = newResult
        return newResult
    }

}