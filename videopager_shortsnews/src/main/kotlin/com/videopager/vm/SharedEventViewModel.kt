package com.videopager.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class SharedEventViewModel:ViewModel() {

    private var _requestedApi= MutableSharedFlow<String>()
    val requestedApi = _requestedApi.asSharedFlow()

    fun requestApi(query: String) {
        viewModelScope.launch { _requestedApi.emit(query) }
    }

}