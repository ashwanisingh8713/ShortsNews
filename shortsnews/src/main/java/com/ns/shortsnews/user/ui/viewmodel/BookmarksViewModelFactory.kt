package com.ns.shortsnews.user.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ns.shortsnews.user.domain.usecase.bookmark.UserProfileBookmarksUseCase

class BookmarksViewModelFactory: ViewModelProvider.Factory {

    private lateinit var userProfileLikesListUseCase: UserProfileBookmarksUseCase

    fun inject(userProfileLikesListUseCase: UserProfileBookmarksUseCase){
        this.userProfileLikesListUseCase = userProfileLikesListUseCase
    }


    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return UserBookmarksViewModel( userProfileLikesListUseCase) as T
    }

}