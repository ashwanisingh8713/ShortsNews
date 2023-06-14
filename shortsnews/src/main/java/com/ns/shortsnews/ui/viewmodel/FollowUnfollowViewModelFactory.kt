package com.ns.shortsnews.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ns.shortsnews.domain.usecase.channel.ChannelInfoUseCase
import com.ns.shortsnews.domain.usecase.followunfollow.FollowUnfollowUseCase

class FollowUnfollowViewModelFactory: ViewModelProvider.Factory {

    private lateinit var followUnfollowUseCse: FollowUnfollowUseCase

    fun inject(followUnfollowUseCase: FollowUnfollowUseCase) {
        this.followUnfollowUseCse = followUnfollowUseCase
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FollowUnfollowViewModel(followUnfollowUseCse) as T
    }

}