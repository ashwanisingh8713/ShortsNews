package com.ns.shortsnews.user.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ns.shortsnews.user.domain.exception.ApiError
import com.ns.shortsnews.user.domain.models.ProfileResult
import com.ns.shortsnews.user.domain.usecase.base.UseCaseResponse
import com.ns.shortsnews.user.domain.usecase.user.UserProfileDataUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UserProfileViewModel(private val userChoiceDataUseCase: UserProfileDataUseCase): ViewModel() {

    // Profile
    private val _userProfileSuccessState = MutableStateFlow<ProfileResult?>(null)
    val UserProfileSuccessState: StateFlow<ProfileResult?> get() = _userProfileSuccessState

    private val _errorState = MutableStateFlow<String?>("NA")
    val errorState: StateFlow<String?> get() = _errorState

    private val _loadingState = MutableStateFlow(true)
    val loadingState: MutableStateFlow<Boolean> get() = _loadingState


    fun requestProfileApi() {
        userChoiceDataUseCase.invoke(viewModelScope, null,
            object : UseCaseResponse<ProfileResult> {
                override fun onSuccess(type: ProfileResult) {
                    _userProfileSuccessState.value = type
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