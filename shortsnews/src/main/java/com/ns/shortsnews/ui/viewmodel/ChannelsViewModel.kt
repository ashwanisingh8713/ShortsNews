package com.ns.shortsnews.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ns.shortsnews.domain.exception.ApiError
import com.ns.shortsnews.domain.models.ChannelsDataResult
import com.ns.shortsnews.domain.usecase.base.UseCaseResponse
import com.ns.shortsnews.domain.usecase.channel.ChannelsDataUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ChannelsViewModel(private val channelsDataUseCase: ChannelsDataUseCase): ViewModel() {

    // Profile
    private val _channelsSuccessState = MutableStateFlow<ChannelsDataResult?>(null)
    val ChannelsSuccessState: StateFlow<ChannelsDataResult?> get() = _channelsSuccessState

    private val _errorState = MutableStateFlow<String?>("NA")
    val errorState: StateFlow<String?> get() = _errorState

    private val _loadingState = MutableStateFlow(true)
    val loadingState: MutableStateFlow<Boolean> get() = _loadingState


    fun requestChannelListApi() {
        channelsDataUseCase.invoke(viewModelScope, null,
            object : UseCaseResponse<ChannelsDataResult> {
                override fun onSuccess(type: ChannelsDataResult) {
                    _channelsSuccessState.value = type
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
}