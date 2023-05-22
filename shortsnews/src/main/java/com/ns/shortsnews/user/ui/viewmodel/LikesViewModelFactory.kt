package com.ns.shortsnews.user.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ns.shortsnews.user.domain.usecase.bookmark.UserProfileLikesListUseCase

class LikesViewModelFactory: ViewModelProvider.Factory {

    private lateinit var userProfileLikesListUseCase: UserProfileLikesListUseCase

    fun inject(userProfileLikesListUseCase: UserProfileLikesListUseCase){
        this.userProfileLikesListUseCase = userProfileLikesListUseCase
    }


    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return UserLikesViewModel( userProfileLikesListUseCase) as T
    }

}