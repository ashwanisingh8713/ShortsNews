package com.ns.news.presentation.activity

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.news.data.api.model.LanguagePageData
import com.news.data.api.model.SectionItem
import com.ns.news.domain.repositories.ApiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.ns.news.domain.Result

class SectionViewModel(private val repository: ApiRepository): ViewModel() {

    /*val viewState: StateFlow<MutableList<SectionItem>> get() = _viewState

    private val _viewState = MutableStateFlow(MutableList<SectionItem>())

    fun requestLanguageList() {
        viewModelScope.launch {
            when (val result = repository.getSections()) {
                is Result.Success -> handlePageData(result.value)
                is Result.Failure -> handleFailure(result.cause)
            }
        }

    }



    private fun handlePageData(languagePageData: LanguagePageData) {
        _viewState.value = languagePageData
    }

    private fun handleFailure(cause: Throwable) {
        Log.i("Ashwani", "Language API :: ${cause.message}")
    }*/

}