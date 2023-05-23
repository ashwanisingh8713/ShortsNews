package com.ns.shortsnews.user.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ns.shortsnews.user.domain.usecase.bookmark.UserProfileBookmarksUseCase

class BookmarksViewModelFactory: ViewModelProvider.Factory {

    private lateinit var userBookmarksListUseCase: UserProfileBookmarksUseCase

    fun inject(userProfileBookmarksUseCase: UserProfileBookmarksUseCase){
        this.userBookmarksListUseCase = userProfileBookmarksUseCase
    }


    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return UserBookmarksViewModel( userBookmarksListUseCase) as T
    }

}