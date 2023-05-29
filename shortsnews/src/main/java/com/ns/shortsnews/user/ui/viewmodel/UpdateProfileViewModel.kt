package com.ns.shortsnews.user.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ns.shortsnews.user.domain.exception.ApiError
import com.ns.shortsnews.user.domain.models.ProfileResult
import com.ns.shortsnews.user.domain.models.StatusResult
import com.ns.shortsnews.user.domain.usecase.base.UseCaseResponse
import com.ns.shortsnews.user.domain.usecase.updateuser.UpdateUserUseCase
import com.ns.shortsnews.user.domain.usecase.user.UserProfileDataUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.RequestBody

class UpdateProfileViewModel(private val updateUserUseCase: UpdateUserUseCase): ViewModel() {

    // Update Profile
    private val _updateProfileSuccessState = MutableStateFlow<StatusResult?>(null)
    val UpdateProfileSuccessState: MutableStateFlow<StatusResult?> get() = _updateProfileSuccessState

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> get() = _errorState

    private val _loadingState = MutableStateFlow(false)
    val loadingState: MutableStateFlow<Boolean> get() = _loadingState


    fun requestUpdateProfileApi(requestBody: RequestBody) {
        updateUserUseCase.invoke(viewModelScope, requestBody,
            object : UseCaseResponse<StatusResult> {
                override fun onSuccess(type: StatusResult) {
                    _updateProfileSuccessState.value = type
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