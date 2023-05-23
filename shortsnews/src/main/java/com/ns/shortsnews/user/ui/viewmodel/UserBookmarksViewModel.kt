package com.ns.shortsnews.user.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ns.shortsnews.user.domain.exception.ApiError
import com.ns.shortsnews.user.domain.usecase.base.UseCaseResponse
import com.ns.shortsnews.user.domain.usecase.bookmark.UserProfileBookmarksUseCase
import com.ns.shortsnews.video.data.VideoDataRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UserBookmarksViewModel(private val channelsDataUseCase: UserProfileBookmarksUseCase): ViewModel() {

    // Profile
    private val _userBookmarksSuccessState = MutableStateFlow<VideoDataRepositoryImpl.VideoDataResponse?>(null)
    val BookmarksSuccessState: StateFlow<VideoDataRepositoryImpl.VideoDataResponse?> get() = _userBookmarksSuccessState

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> get() = _errorState

    private val _loadingState = MutableStateFlow(true)
    val loadingState: MutableStateFlow<Boolean> get() = _loadingState


    fun requestBookmarksApi() {
        channelsDataUseCase.invoke(viewModelScope, null,
            object : UseCaseResponse<VideoDataRepositoryImpl.VideoDataResponse> {
                override fun onSuccess(type: VideoDataRepositoryImpl.VideoDataResponse) {
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