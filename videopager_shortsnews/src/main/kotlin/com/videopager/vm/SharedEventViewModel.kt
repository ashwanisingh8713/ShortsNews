package com.videopager.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.player.models.VideoData
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class SharedEventViewModel:ViewModel() {

    private var _cacheVideoUrl= MutableSharedFlow<Pair<String, String>>()
    val cacheVideoUrl = _cacheVideoUrl.asSharedFlow()

    fun cacheVideoData(uri: String, id: String) {
        viewModelScope.launch {
            _cacheVideoUrl.emit(Pair(uri, id))
        }
    }

}