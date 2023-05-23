package com.ns.shortsnews.user.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class ProfileSharedViewModel:ViewModel() {

    private var _cacheVideoUrl= MutableSharedFlow<Pair<String, String>>()
    val cacheVideoUrl = _cacheVideoUrl.asSharedFlow()

    fun cacheVideoData(uri: String, id: String) {
        viewModelScope.launch {
            _cacheVideoUrl.emit(Pair(uri, id))
        }
    }

    private var _toggelChannelPage= MutableSharedFlow<Pair<Boolean, String>>()
    val toggelChannelPage = _toggelChannelPage.asSharedFlow()

    fun toggelChannelPage(isOpen: Boolean, channelId: String) {
        viewModelScope.launch {
            _toggelChannelPage.emit(Pair(isOpen, channelId))
        }
    }








}