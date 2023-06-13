package com.ns.shortsnews.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ns.shortsnews.domain.usecase.channel.ChannelInfoUseCase

class ChannelInfoViewModelFactory: ViewModelProvider.Factory {

    private lateinit var channelInfoUseCase: ChannelInfoUseCase

    fun inject(channelInfoUseCase: ChannelInfoUseCase) {
        this.channelInfoUseCase = channelInfoUseCase
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ChannelInfoViewModel(channelInfoUseCase) as T
    }

}