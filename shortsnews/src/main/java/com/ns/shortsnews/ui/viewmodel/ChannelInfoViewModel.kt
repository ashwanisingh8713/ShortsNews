package com.ns.shortsnews.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ns.shortsnews.domain.exception.ApiError
import com.ns.shortsnews.domain.models.ChannelInfo
import com.ns.shortsnews.domain.usecase.base.UseCaseResponse
import com.ns.shortsnews.domain.usecase.channel.ChannelInfoUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow

class ChannelInfoViewModel(private val channelInfoUseCase: ChannelInfoUseCase): ViewModel() {

    // Profile
    private val _channelInfoSuccessState = MutableSharedFlow<ChannelInfo?>(replay = 0, extraBufferCapacity=1)
    val ChannelInfoSuccessState: SharedFlow<ChannelInfo?> get() = _channelInfoSuccessState

    private val _errorState = MutableSharedFlow<String?>(replay = 0, extraBufferCapacity=1)
    val errorState: SharedFlow<String?> get() = _errorState

    private val _loadingState = MutableStateFlow(true)
    val loadingState: MutableStateFlow<Boolean> get() = _loadingState

    fun requestChannelInfoApi(channelId:String) {
        channelInfoUseCase.invoke(viewModelScope, channelId,
            object : UseCaseResponse<ChannelInfo> {
                override fun onSuccess(type: ChannelInfo) {
                    _channelInfoSuccessState.tryEmit(type)
                }

                override fun onError(apiError: ApiError) {
                    _errorState.tryEmit(apiError.getErrorMessage())
                }

                override fun onLoading(isLoading: Boolean) {
                    _loadingState.value = true
                }
            }
        )
    }



}