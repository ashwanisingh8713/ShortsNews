package com.ns.news.presentation.onboarding

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.news.data.api.model.CategoryPageData
import com.ns.news.domain.repositories.ApiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.news.domain.Result

class TopicViewModel(private val repository: ApiRepository): ViewModel() {

    val viewState: StateFlow<CategoryPageData> get() = _viewState

    private val _viewState = MutableStateFlow(CategoryPageData())

    fun requestTopicData() {
        viewModelScope.launch {
            when (val result = repository.getCategoryPageData()) {
                is Result.Success -> handlePageData(result.value)
                is Result.Failure -> handleFailure(result.cause)
            }
        }

    }



    private fun handlePageData(categoryPageData: CategoryPageData) {
        _viewState.value = categoryPageData
    }

    private fun handleFailure(cause: Throwable) {
        Log.i("Ashwani", "Category / Topic API :: ${cause.message}")
    }

}