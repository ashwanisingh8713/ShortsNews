package com.ns.shortsnews.user.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ns.shortsnews.user.domain.usecase.channel.ChannelVideosDataUseCase
import com.ns.shortsnews.user.domain.usecase.channel.ChannelsDataUseCase
import com.ns.shortsnews.user.domain.usecase.user.UserProfileDataUseCase

class ChannelsVideosViewModelFactory: ViewModelProvider.Factory {

    private lateinit var channelsDataUseCase: ChannelVideosDataUseCase

    fun inject(channelsDataUseCase: ChannelVideosDataUseCase){
        this.channelsDataUseCase = channelsDataUseCase
    }


    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ChannelsVideoDataViewModel( channelsDataUseCase) as T
    }

}