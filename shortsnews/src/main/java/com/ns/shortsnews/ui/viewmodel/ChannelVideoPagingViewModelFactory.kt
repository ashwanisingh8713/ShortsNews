package com.ns.shortsnews.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ns.shortsnews.domain.usecase.videodata.VideoDataUseCase

class ChannelVideoPagingViewModelFactory(private val channelId: String): ViewModelProvider.Factory {





    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ChannelVideoViewModel( channelId) as T
    }

}