package com.ns.shortsnews.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ns.shortsnews.domain.usecase.videodata.VideoDataUseCase

class BookmarksViewModelFactory: ViewModelProvider.Factory {

    private lateinit var userBookmarksListUseCase: VideoDataUseCase

    fun inject(userProfileBookmarksUseCase: VideoDataUseCase){
        this.userBookmarksListUseCase = userProfileBookmarksUseCase
    }


    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return UserBookmarksViewModel( userBookmarksListUseCase) as T
    }

}