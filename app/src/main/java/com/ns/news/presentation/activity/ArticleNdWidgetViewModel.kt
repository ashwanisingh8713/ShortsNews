package com.ns.news.presentation.activity

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ns.news.data.api.model.ArticleNdWidgetResponse
import com.ns.news.data.api.model.CellsItem
import com.ns.news.domain.Result
import com.ns.news.domain.model.ArticleNdWidgetDataState
import com.ns.news.domain.repositories.ApiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ArticleNdWidgetViewModel(private val repository: ApiRepository) : ViewModel() {

    val viewState: StateFlow<ArticleNdWidgetDataState> get() = _viewState
    private val _viewState = MutableStateFlow(ArticleNdWidgetDataState())

    fun requestArticleNdWidget(url: String) {
        viewModelScope.launch {
            when (val result = repository.getArticleNdWidget(url)) {
                is Result.Success -> handlePageData(result.value)
                is Result.Failure -> handleFailure(result.cause)
            }
        }
    }

    private fun handlePageData(response: ArticleNdWidgetResponse) {
        val awData = response.data
        val page = awData.page
        val total = awData.total
        _viewState.value = ArticleNdWidgetDataState(loading = false, cellItems = awData.cells, page = page)
    }

    private fun handleFailure(cause: Throwable) {
        Log.e("Ashwani", "ArticleNdWidget API :: ${cause.message}")
    }

}