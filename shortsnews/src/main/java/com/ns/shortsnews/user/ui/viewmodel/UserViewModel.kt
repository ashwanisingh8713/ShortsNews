package com.ns.shortsnews.user.ui.viewmodel

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ns.shortsnews.user.data.mapper.UserOtp
import com.ns.shortsnews.user.data.mapper.UserRegistration
import com.ns.shortsnews.user.domain.exception.ApiError
import com.ns.shortsnews.user.domain.models.LanguageData
import com.ns.shortsnews.user.domain.models.LanguagesResult
import com.ns.shortsnews.user.domain.models.OTPResult
import com.ns.shortsnews.user.domain.models.RegistrationResult
import com.ns.shortsnews.user.domain.usecase.user.UserRegistrationDataUseCase
import com.ns.shortsnews.user.domain.usecase.base.UseCaseResponse
import com.ns.shortsnews.user.domain.usecase.language.LanguageDataUseCase
import com.ns.shortsnews.user.domain.usecase.user.UserOtpValidationDataUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel constructor(private val userRegistrationUseCases: UserRegistrationDataUseCase,
                    private val otpValidationDataUseCases: UserOtpValidationDataUseCase,
                                private val languageDataUseCase: LanguageDataUseCase) : ViewModel() {

    companion object {
        val LOGIN = "login"
        val OTP = "otp"
        val PROFILE = "profile"
        val OTP_POP = "otp_pop"
        val MAIN_ACTIVITY = "main_activity"
        val LANGUAGES = "languages"
    }


    private val _shareValueFlow = MutableSharedFlow<String?>()
    val sharedValueFlow: SharedFlow<String?> get() = _shareValueFlow

    private val _fragmentStateFlow = MutableSharedFlow<Bundle?>()
    val fragmentStateFlow: SharedFlow<Bundle?> get() = _fragmentStateFlow
    // Registration
    private val _registrationSuccessState = MutableStateFlow<UserRegistration?>(null)
    val registrationSuccessState: StateFlow<UserRegistration?> get() = _registrationSuccessState

    // Otp
    private val _otpSuccessState = MutableStateFlow<UserOtp?>(null)
    val otpSuccessState: StateFlow<UserOtp?> get() = _otpSuccessState
    //Language
    private val _languagesSuccessState = MutableStateFlow<List<LanguageData>>(emptyList())
    val LanguagesSuccessState: StateFlow<List<LanguageData>> get() = _languagesSuccessState

    private val _errorState = MutableStateFlow<String?>("NA")
    val errorState: StateFlow<String?> get() = _errorState

    private val _loadingState = MutableStateFlow(false)
    val loadingState: MutableStateFlow<Boolean> get() = _loadingState

    fun updateFragment(fragmentType:String, bundle: Bundle) {
        viewModelScope.launch{
            bundle.putString("fragmentType", fragmentType)
            _fragmentStateFlow.emit(bundle)
        }
    }


    fun requestRegistrationApi(requestBody: Map<String, String>) {
        userRegistrationUseCases.invoke(viewModelScope, requestBody,
            object : UseCaseResponse<RegistrationResult> {
                override fun onSuccess(result: RegistrationResult) {
                    // Mapping the data model class, which will be used by UIs.
                    val userRegistration = UserRegistration().mapper(status = result.status, msg = result.msg,
                    OTP_id = result.data!!.OTP_id, length = result.data.length, email = result.data.email)
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
                override fun onSuccess(result: OTPResult) {
                    val data = result.data
                    val otpValidation = UserOtp().mapper(status = result.status, msg = result.msg,
                        email = data!!.my_profile.email, access_token = data.access_token,
                        name = data.my_profile.name, first_time_user = data.first_time_user,
                        userProfileImage = data.my_profile.image, user_id = data.my_profile.user_id,
                    age = data.my_profile.age, location = data.my_profile.location)
                    _otpSuccessState.value = otpValidation
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

    fun requestLanguagesApi() {
        languageDataUseCase.invoke(viewModelScope,null,
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