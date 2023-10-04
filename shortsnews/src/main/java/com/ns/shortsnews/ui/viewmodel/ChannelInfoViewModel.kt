package com.ns.shortsnews.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.ns.shortsnews.data.source.UserApiService
import com.ns.shortsnews.domain.exception.ApiError
import com.ns.shortsnews.domain.models.ChannelInfo
import com.ns.shortsnews.domain.usecase.base.UseCaseResponse
import com.ns.shortsnews.domain.usecase.channel.ChannelInfoUseCase
import com.ns.shortsnews.ui.paging.BookmarkPaging
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import org.koin.java.KoinJavaComponent

class ChannelInfoViewModel(private val channelInfoUseCase: ChannelInfoUseCase): ViewModel() {

    // Profile
    private val _channelInfoSuccessState = MutableStateFlow<ChannelInfo?>(null)
    val ChannelInfoSuccessState: StateFlow<ChannelInfo?> get() = _channelInfoSuccessState

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> get() = _errorState

    private val _loadingState = MutableStateFlow(true)
    val loadingState: MutableStateFlow<Boolean> get() = _loadingState

    fun requestChannelInfoApi(channelId:String) {
        channelInfoUseCase.invoke(viewModelScope, channelId,
            object : UseCaseResponse<ChannelInfo> {
                override fun onSuccess(type: ChannelInfo) {
                    _channelInfoSuccessState.value = type
                    _loadingState.value = false
                }

                override fun onError(apiError: ApiError) {
//                    _errorState.value = apiError.getErrorMessage()
//                    _loadingState.value = false
                }

                override fun onLoading(isLoading: Boolean) {
//                    _loadingState.value = true
                }
            }
        )
    }

    fun clearChannelInfo(){
        _channelInfoSuccessState.value = null
    }

}