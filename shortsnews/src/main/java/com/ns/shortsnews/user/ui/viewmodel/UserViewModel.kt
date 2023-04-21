package com.ns.shortsnews.user.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ns.shortsnews.user.data.models.*
import com.ns.shortsnews.user.domain.exception.ApiError
import com.ns.shortsnews.user.domain.usecase.UserUseCases
import com.ns.shortsnews.user.domain.usecase.base.UseCaseResponse
import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

class UserViewModel(private val userUseCases: UserUseCases) : ViewModel() {
    private val _successStateFlow = MutableSharedFlow<String?>()
    val successStateFlow: SharedFlow<String?> get() = _successStateFlow

    // Registration
    private val _registrationSuccessState = MutableStateFlow<User?>(null)
    val registrationSuccessState: StateFlow<User?> get() = _registrationSuccessState

    // Otp
    private val _otpSuccessState = MutableStateFlow<OtpData?>(null)
    val otpSuccessState: StateFlow<OtpData?> get() = _otpSuccessState

    // Profile
    private val _profileSuccessState = MutableStateFlow<ProfileData?>(null)
    val profileSuccessState: StateFlow<ProfileData?> get() = _profileSuccessState

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> get() = _errorState

    private val _loadingState = MutableStateFlow(true)
    val loadingState: MutableStateFlow<Boolean> get() = _loadingState

    private fun requestRegistrationApi(requestBody: Map<String, String>) {
        userUseCases.invoke(viewModelScope, requestBody, "",
            object : UseCaseResponse<BaseResult> {
                override fun onSuccess(type: BaseResult) {
                    val moshi: Moshi = Moshi.Builder().build()
                    val adapter: JsonAdapter<User> = moshi.adapter(User::class.java)
                    val user = adapter.fromJson(type.toString())
                    _registrationSuccessState.value = user
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

    private fun requestOtpValidationApi(requestBody: Map<String, String>) {
        userUseCases.invoke(viewModelScope, requestBody, "",
            object : UseCaseResponse<BaseResult> {
                override fun onSuccess(type: BaseResult) {
                    val moshi: Moshi = Moshi.Builder().build()
                    val adapter: JsonAdapter<OtpData> = moshi.adapter(OtpData::class.java)
                    val otpData = adapter.fromJson(type.toString())
                    _otpSuccessState.value = otpData
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

    private fun requestProfileApi(requestBody: Map<String, String>) {
        userUseCases.invoke(viewModelScope, requestBody, "",
            object : UseCaseResponse<BaseResult> {
                override fun onSuccess(type: BaseResult) {
                    val moshi: Moshi = Moshi.Builder().build()
                    val adapter: JsonAdapter<ProfileData> = moshi.adapter(ProfileData::class.java)
                    val profileData = adapter.fromJson(type.toString())
                    _profileSuccessState.value = profileData
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