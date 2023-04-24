package com.ns.shortsnews.user.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ns.shortsnews.user.data.mapper.UserRegistration
import com.ns.shortsnews.user.data.models.*
import com.ns.shortsnews.user.domain.exception.ApiError
import com.ns.shortsnews.user.domain.models.OTPResult
import com.ns.shortsnews.user.domain.models.ProfileResult
import com.ns.shortsnews.user.domain.models.RegistrationData
import com.ns.shortsnews.user.domain.models.RegistrationResult
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
    private val _registrationSuccessState = MutableStateFlow<UserRegistration?>(null)
    val registrationSuccessState: StateFlow<UserRegistration?> get() = _registrationSuccessState

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
                override fun onSuccess(result: RegistrationResult) {
                    // Mapping the data model class, which will be used by UIs.
                    val userRegistration = UserRegistration().mapper(status = result.status, msg = result.msg,
                    OTP_id = result.data.OTP_id, length = result.data.length, email = result.data.email)
                    _registrationSuccessState.value = userRegistration
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