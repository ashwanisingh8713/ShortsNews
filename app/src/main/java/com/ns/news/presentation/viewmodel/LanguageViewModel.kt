package com.ns.news.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.news.data.api.model.LanguagePageData
import com.ns.news.domain.repositories.ApiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.news.domain.Result

class LanguageViewModel(private val repository: ApiRepository): ViewModel() {

    val viewState: StateFlow<LanguagePageData> get() = _viewState
    // also
    // val viewState = _viewState.asStateFLow()

    private val _viewState = MutableStateFlow(LanguagePageData())

    fun requestLanguageList() {
        viewModelScope.launch {
            when (val result = repository.getLanguage()) {
                is Result.Success -> handleCoinList(result.value)
                is Result.Failure -> handleFailure(result.cause)
            }
        }

    }



    private fun handleCoinList(coins: LanguagePageData) {
        _viewState.value = coins
    }

    private fun handleFailure(cause: Throwable) {
        Log.i("Ashwani", "Language API :: ${cause.message}")
    }

}