package com.ns.shortsnews.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ns.shortsnews.domain.exception.ApiError
import com.ns.shortsnews.domain.models.VideoDataResponse
import com.ns.shortsnews.domain.usecase.base.UseCaseResponse
import com.ns.shortsnews.domain.usecase.videodata.VideoDataUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UserBookmarksViewModel(private val channelsDataUseCase: VideoDataUseCase): ViewModel() {

    // Profile
    private val _videoDataSuccessState = MutableStateFlow<VideoDataResponse?>(null)
    val videoDataState: StateFlow<VideoDataResponse?> get() = _videoDataSuccessState

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> get() = _errorState

    private val _loadingState = MutableStateFlow(true)
    val loadingState: MutableStateFlow<Boolean> get() = _loadingState

    fun clearChannelVideoData() {
        _videoDataSuccessState.value = null
    }

    fun requestVideoData(params: Pair<String, String>) {
        channelsDataUseCase.invoke(viewModelScope, params,
            object : UseCaseResponse<VideoDataResponse> {
                override fun onSuccess(type: VideoDataResponse) {
                    _videoDataSuccessState.value = type
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
    fun clearVideoData(){
        _userBookmarksSuccessState.value = null
    }
}