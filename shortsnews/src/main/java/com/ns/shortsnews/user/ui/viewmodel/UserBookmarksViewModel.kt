package com.ns.shortsnews.user.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ns.shortsnews.user.domain.exception.ApiError
import com.ns.shortsnews.user.domain.models.VideoDataResponse
import com.ns.shortsnews.user.domain.usecase.base.UseCaseResponse
import com.ns.shortsnews.user.domain.usecase.videodata.VideoDataUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UserBookmarksViewModel(private val channelsDataUseCase: VideoDataUseCase): ViewModel() {

    // Profile
    private val _userBookmarksSuccessState = MutableStateFlow<VideoDataResponse?>(null)
    val BookmarksSuccessState: StateFlow<VideoDataResponse?> get() = _userBookmarksSuccessState

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> get() = _errorState

    private val _loadingState = MutableStateFlow(true)
    val loadingState: MutableStateFlow<Boolean> get() = _loadingState


    fun requestBookmarksApi(params: Pair<String, String>) {
        channelsDataUseCase.invoke(viewModelScope, params,
            object : UseCaseResponse<VideoDataResponse> {
                override fun onSuccess(type: VideoDataResponse) {
                    _userBookmarksSuccessState.value = type
                    _loadingState.value = false
                }

                override fun onError(apiError: ApiError) {
                    _errorState.value = apiError.getErrorMessage()
                    _loadingState.value = false
                }

                override fun onLoading(isLoading: Boolean) {
                    _loadingState.value = true
                }
            }
        )
    }
}