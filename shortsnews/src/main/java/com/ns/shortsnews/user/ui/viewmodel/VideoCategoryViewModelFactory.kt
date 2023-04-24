package com.ns.shortsnews.user.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ns.shortsnews.user.domain.usecase.video_category.VideoCategoryUseCase

/**
 * Created by Ashwani Kumar Singh on 24,April,2023.
 */
class VideoCategoryViewModelFactory:ViewModelProvider.Factory {

    private lateinit var videoCategoryUseCase: VideoCategoryUseCase

    fun inject(videoCategoryUseCase: VideoCategoryUseCase) {
        this.videoCategoryUseCase = videoCategoryUseCase
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return VideoCategoryViewModel(videoCategoryUseCase) as T
    }

}