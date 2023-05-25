package com.ns.shortsnews.user.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ns.shortsnews.user.data.mapper.UserVideoCategory
import com.ns.shortsnews.user.domain.exception.ApiError
import com.ns.shortsnews.user.domain.models.VideoCategoryResult
import com.ns.shortsnews.user.domain.usecase.base.UseCaseResponse
import com.ns.shortsnews.user.domain.usecase.video_category.VideoCategoryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Created by Ashwani Kumar Singh on 24,April,2023.
 */
class VideoCategoryViewModel(private val videoCategoryUseCase: VideoCategoryUseCase): ViewModel() {

    private val _videoCategorySuccessState = MutableStateFlow<UserVideoCategory?>(null)
    val videoCategorySuccessState: StateFlow<UserVideoCategory?> get() = _videoCategorySuccessState

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> get() = _errorState

    private val _loadingState = MutableStateFlow(true)
    val loadingState: MutableStateFlow<Boolean> get() = _loadingState

    fun loadVideoCategory() {
        videoCategoryUseCase.invoke(scope = viewModelScope, params = "", onResult = object : UseCaseResponse<VideoCategoryResult>{
            override fun onSuccess(result: VideoCategoryResult) {
                _loadingState.value = false
                val userVideoCategory = UserVideoCategory().mapper(status = result.status, videoCategories = result.data)
                _videoCategorySuccessState.value = userVideoCategory
            }

            override fun onError(apiError: ApiError) {
                _loadingState.value = false
                _errorState.value = "${apiError.getErrorMessage()}"
            }

            override fun onLoading(isLoading: Boolean) {
                _loadingState.value = false
            }

        })

    }

}