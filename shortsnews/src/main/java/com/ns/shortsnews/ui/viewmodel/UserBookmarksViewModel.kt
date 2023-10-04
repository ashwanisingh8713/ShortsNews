package com.ns.shortsnews.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ns.shortsnews.data.source.UserApiService
import com.ns.shortsnews.domain.exception.ApiError
import com.ns.shortsnews.domain.models.Data
import com.ns.shortsnews.domain.models.VideoDataResponse
import com.ns.shortsnews.domain.usecase.base.UseCaseResponse
import com.ns.shortsnews.domain.usecase.videodata.VideoDataUseCase
import com.ns.shortsnews.ui.paging.BookmarkPaging
import com.videopager.utils.CategoryConstants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import org.koin.core.Koin
import org.koin.java.KoinJavaComponent.getKoin


class UserBookmarksViewModel(private val channelsDataUseCase: VideoDataUseCase): ViewModel() {

    // Profile
    private val _videoDataSuccessState = MutableStateFlow<VideoDataResponse?>(null)
    val videoDataState: StateFlow<VideoDataResponse?> get() = _videoDataSuccessState

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> get() = _errorState

    private val _loadingState = MutableStateFlow(true)
    val loadingState: MutableStateFlow<Boolean> get() = _loadingState

    val bookmarks = Pager(PagingConfig(pageSize = 10)) {
        BookmarkPaging(userApiService = getKoin().get<UserApiService>())
    }.flow.cachedIn(viewModelScope).map { it }

    fun clearChannelVideoData() {
        _videoDataSuccessState.value = null
    }

    fun requestVideoData(params: Pair<String, String>) {
        channelsDataUseCase.invoke(viewModelScope, params,
            object : UseCaseResponse<VideoDataResponse> {
                override fun onSuccess(type: VideoDataResponse) {
                    _videoDataSuccessState.value = type
                }

                override fun onError(apiError: ApiError) {
                    _errorState.value = apiError.getErrorMessage()
                }

                override fun onLoading(isLoading: Boolean) {
                    _loadingState.value = isLoading
                }
            }
        )
    }
    fun clearVideoData() {
        _videoDataSuccessState.value = null
    }
}