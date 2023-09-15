package com.ns.shortsnews.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ns.shortsnews.domain.usecase.video_category.UpdateVideoCategoriesUseCase
import com.ns.shortsnews.domain.usecase.video_category.VideoCategoryUseCase

/**
 * Created by Ashwani Kumar Singh on 24,April,2023.
 */
class VideoCategoryViewModelFactory:ViewModelProvider.Factory {

    private lateinit var videoCategoryUseCase: VideoCategoryUseCase
    private lateinit var updateVideoCategoriesUseCase: UpdateVideoCategoriesUseCase

    fun inject(videoCategoryUseCase: VideoCategoryUseCase, updateVideoCategoriesUseCase: UpdateVideoCategoriesUseCase) {
        this.videoCategoryUseCase = videoCategoryUseCase
        this.updateVideoCategoriesUseCase = updateVideoCategoriesUseCase

    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return VideoCategoryViewModel(videoCategoryUseCase, updateVideoCategoriesUseCase) as T
    }

}