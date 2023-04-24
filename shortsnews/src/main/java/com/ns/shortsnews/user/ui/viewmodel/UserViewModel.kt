package com.ns.shortsnews.user.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ns.shortsnews.user.data.models.*
import com.ns.shortsnews.user.domain.exception.ApiError
import com.ns.shortsnews.user.domain.usecase.user.UserRegistrationDataUseCase
import com.ns.shortsnews.user.domain.usecase.base.UseCaseResponse
import com.ns.shortsnews.user.domain.usecase.user.UserOtpValidationDataUseCase
import com.ns.shortsnews.user.domain.usecase.user.UserProfileDataUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel(private val userRegistrationUseCases: UserRegistrationDataUseCase,
                    private val otpValidationDataUseCases: UserOtpValidationDataUseCase,
                    private val profileDataUseCases: UserProfileDataUseCase) : ViewModel() {

    companion object {
        val LOGIN = "login"
        val OTP = "otp"
        val PROFILE = "profile"
    }


    private val _shareValueFlow = MutableSharedFlow<String?>()
    val sharedValueFlow: SharedFlow<String?> get() = _shareValueFlow

    private val _fragmentStateFlow = MutableSharedFlow<String?>()
    val fragmentStateFlow: SharedFlow<String?> get() = _fragmentStateFlow
    // Registration
    private val _registrationSuccessState = MutableStateFlow<RegistrationResult?>(null)
    val registrationSuccessState: StateFlow<RegistrationResult?> get() = _registrationSuccessState

    // Otp
    private val _otpSuccessState = MutableStateFlow<OTPResult?>(null)
    val otpSuccessState: StateFlow<OTPResult?> get() = _otpSuccessState

    // Profile
    private val _profileSuccessState = MutableStateFlow<ProfileResult?>(null)
    val profileSuccessState: StateFlow<ProfileResult?> get() = _profileSuccessState

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> get() = _errorState

    private val _loadingState = MutableStateFlow(true)
    val loadingState: MutableStateFlow<Boolean> get() = _loadingState

    fun updateFragment(fragmentType:String) {
        viewModelScope.launch(){
            _fragmentStateFlow.emit(fragmentType)
        }
    }


    fun requestRegistrationApi(requestBody: Map<String, String>) {
        userRegistrationUseCases.invoke(viewModelScope, requestBody,
            object : UseCaseResponse<RegistrationResult> {
                override fun onSuccess(type: RegistrationResult) {
                    _registrationSuccessState.value = type
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

    fun requestOtpValidationApi(requestBody: Map<String, String>) {
        otpValidationDataUseCases.invoke(viewModelScope, requestBody,
            object : UseCaseResponse<OTPResult> {
                override fun onSuccess(type: OTPResult) {
                    _otpSuccessState.value = type
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

    fun requestProfileApi(requestBody: Map<String, String>) {
        profileDataUseCases.invoke(viewModelScope, requestBody,
            object : UseCaseResponse<ProfileResult> {
                override fun onSuccess(type: ProfileResult) {
                    _profileSuccessState.value = type
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