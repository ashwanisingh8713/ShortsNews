package com.ns.shortsnews.user.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ns.shortsnews.user.domain.exception.ApiError
import com.ns.shortsnews.user.domain.models.UserChoiceResult
import com.ns.shortsnews.user.domain.usecase.base.UseCaseResponse
import com.ns.shortsnews.user.domain.usecase.user.UserChoiceDataUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UserChoiceViewModel(private val userChoiceDataUseCase: UserChoiceDataUseCase): ViewModel() {

    // Profile
    private val _userChoiceSuccessState = MutableStateFlow<UserChoiceResult?>(null)
    val UserChoiceSuccessState: StateFlow<UserChoiceResult?> get() = _userChoiceSuccessState

    private val _errorState = MutableStateFlow<String?>("NA")
    val errorState: StateFlow<String?> get() = _errorState

    private val _loadingState = MutableStateFlow(true)
    val loadingState: MutableStateFlow<Boolean> get() = _loadingState


    fun requestChoiceApi(requestBody: Map<String, String>) {
        userChoiceDataUseCase.invoke(viewModelScope, requestBody,
            object : UseCaseResponse<UserChoiceResult> {
                override fun onSuccess(type: UserChoiceResult) {
                    _userChoiceSuccessState.value = type
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