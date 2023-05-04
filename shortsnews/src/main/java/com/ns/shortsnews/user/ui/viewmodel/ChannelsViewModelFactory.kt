package com.ns.shortsnews.user.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ns.shortsnews.user.domain.usecase.channel.ChannelsDataUseCase
import com.ns.shortsnews.user.domain.usecase.user.UserProfileDataUseCase

class ChannelsViewModelFactory: ViewModelProvider.Factory {

    private lateinit var channelsDataUseCase: ChannelsDataUseCase

    fun inject(channelsDataUseCase: ChannelsDataUseCase){
        this.channelsDataUseCase = channelsDataUseCase
    }


    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ChannelsViewModel( channelsDataUseCase) as T
    }

}