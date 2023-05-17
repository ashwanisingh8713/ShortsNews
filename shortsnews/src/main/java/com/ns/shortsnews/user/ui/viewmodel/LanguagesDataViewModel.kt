package com.ns.shortsnews.user.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ns.shortsnews.user.domain.exception.ApiError
import com.ns.shortsnews.user.domain.models.ChannelVideoData
import com.ns.shortsnews.user.domain.models.ChannelVideoDataResult
import com.ns.shortsnews.user.domain.models.LanguageData
import com.ns.shortsnews.user.domain.models.LanguagesResult
import com.ns.shortsnews.user.domain.usecase.base.UseCaseResponse
import com.ns.shortsnews.user.domain.usecase.channel.ChannelVideosDataUseCase
import com.ns.shortsnews.user.domain.usecase.language.LanguageDataUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LanguagesDataViewModel(private val channelsDataUseCase: LanguageDataUseCase): ViewModel() {

    // Profile
    private val _languagesSuccessState = MutableStateFlow<List<LanguageData>>(emptyList())
    val LanguagesSuccessState: StateFlow<List<LanguageData>> get() = _languagesSuccessState

    private val _errorState = MutableStateFlow<String?>("NA")
    val errorState: StateFlow<String?> get() = _errorState

    private val _loadingState = MutableStateFlow(true)
    val loadingState: MutableStateFlow<Boolean> get() = _loadingState


    fun requestLanguagesApi() {
        channelsDataUseCase.invoke(viewModelScope,null,
            object : UseCaseResponse<LanguagesResult> {
                override fun onSuccess(type: LanguagesResult) {
                    if(type.status)
                    _languagesSuccessState.value = type.data
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