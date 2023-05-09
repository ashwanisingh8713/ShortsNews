package com.ns.shortsnews.user.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ns.shortsnews.user.domain.exception.ApiError
import com.ns.shortsnews.user.domain.models.ChannelVideoData
import com.ns.shortsnews.user.domain.models.ChannelVideoDataResult
import com.ns.shortsnews.user.domain.usecase.base.UseCaseResponse
import com.ns.shortsnews.user.domain.usecase.channel.ChannelVideosDataUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ChannelsVideoDataViewModel(private val channelsDataUseCase: ChannelVideosDataUseCase): ViewModel() {

    // Profile
    private val _channelsVideoSuccessState = MutableStateFlow<List<ChannelVideoData>>(emptyList())
    val ChannelsVideoSuccessState: StateFlow<List<ChannelVideoData>> get() = _channelsVideoSuccessState

    private val _errorState = MutableStateFlow<String?>("NA")
    val errorState: StateFlow<String?> get() = _errorState

    private val _loadingState = MutableStateFlow(true)
    val loadingState: MutableStateFlow<Boolean> get() = _loadingState


    fun requestChannelVideosApi(channelId:String) {
        channelsDataUseCase.invoke(viewModelScope, channelId,
            object : UseCaseResponse<ChannelVideoDataResult> {
                override fun onSuccess(type: ChannelVideoDataResult) {
                    if(type.status)
                    _channelsVideoSuccessState.value = type.data
                    else _errorState.value = "Empty from api server"
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