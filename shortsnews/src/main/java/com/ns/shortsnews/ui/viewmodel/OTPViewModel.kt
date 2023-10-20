package com.ns.shortsnews.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ns.shortsnews.data.mapper.UserOtp
import com.ns.shortsnews.domain.exception.ApiError
import com.ns.shortsnews.domain.models.OTPResult
import com.ns.shortsnews.domain.models.UserSelectionResult
import com.ns.shortsnews.domain.models.UserSelectionsData
import com.ns.shortsnews.domain.usecase.base.UseCaseResponse
import com.ns.shortsnews.domain.usecase.user.UserOtpValidationDataUseCase
import com.ns.shortsnews.domain.usecase.user.UserSelectionsDataUseCase
import com.ns.shortsnews.utils.AppPreference
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

/**
 * Created by Ashwani Kumar Singh on 19,October,2023.
 */
class OTPViewModel(private val otpValidationDataUseCases: UserOtpValidationDataUseCase, private val userSelectionsDataUseCase: UserSelectionsDataUseCase): ViewModel() {

    // Otp
    private val _otpSuccessState = MutableSharedFlow<UserOtp?>(replay = 0, extraBufferCapacity=1)
    val otpSuccessState: SharedFlow<UserOtp?> get() = _otpSuccessState

    private val _errorState = MutableSharedFlow<String?>(replay = 0, extraBufferCapacity=1)
    val errorState: SharedFlow<String?> get() = _errorState

    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean> get() = _loadingState

    fun requestOtpValidationApi(requestBody: Map<String, String>) {
        otpValidationDataUseCases.invoke(viewModelScope, requestBody,
            object : UseCaseResponse<OTPResult> {
                override fun onSuccess(result: OTPResult) {
                    if(result.status.not()) {
                        _errorState.tryEmit(result.msg)
                    } else {
                        val data = result.data
                        val otpValidation = UserOtp().mapper(
                            status = result.status,
                            msg = result.msg,
                            email = data!!.my_profile.email,
                            access_token = data.access_token,
                            name = data.my_profile.name,
                            first_time_user = data.first_time_user,
                            userProfileImage = data.my_profile.image,
                            user_id = data.my_profile.user_id,
                            age = data.my_profile.age,
                            location = data.my_profile.location
                        )
                        Log.i("OTPSuccess", "OtpViewModel :: requestOtpValidationApi")
                        _otpSuccessState.tryEmit(otpValidation)
                    }
                }

                override fun onError(apiError: ApiError) {
                    _errorState.tryEmit(apiError.getErrorMessage())
                }

                override fun onLoading(isLoading: Boolean) {
                    _loadingState.update { isLoading}
                }
            }
        )
    }


    //User selections
    private val _userSelectionSuccessState = MutableSharedFlow<UserSelectionsData?>(replay = 0, extraBufferCapacity=1)
    val userSelectionSuccessState: SharedFlow<UserSelectionsData?> get() = _userSelectionSuccessState

    private val _userSelectionsErrorState = MutableSharedFlow<String?>(replay = 0, extraBufferCapacity=1)
    val userSelectionsErrorState: SharedFlow<String?> get() = _userSelectionsErrorState

    fun requestUserSelectionApi() {
        userSelectionsDataUseCase.invoke(
            viewModelScope,
            null,
            object : UseCaseResponse<UserSelectionResult> {
                override fun onSuccess(result: UserSelectionResult) {
                    if (result.status) {
                        if(result.data.languages.isNotEmpty()) {
                            AppPreference.isLanguageSelected = true
                        }
                        Log.i("OTPSuccess", "OtpViewModel :: requestUserSelectionApi")
                        _userSelectionSuccessState.tryEmit(result.data)
                    }
                    else {
                        _userSelectionsErrorState.tryEmit("Empty from api server")
                    }

                }

                override fun onError(apiError: ApiError) {
                    _userSelectionsErrorState.tryEmit(apiError.getErrorMessage())
                }

                override fun onLoading(isLoading: Boolean) {
                    _loadingState.value = isLoading
                }

            })
    }
}