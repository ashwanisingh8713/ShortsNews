package com.ns.shortsnews.ui.viewmodel

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ns.shortsnews.data.mapper.UserOtp
import com.ns.shortsnews.data.mapper.UserRegistration
import com.ns.shortsnews.domain.exception.ApiError
import com.ns.shortsnews.domain.models.LanguageData
import com.ns.shortsnews.domain.models.LanguagesResult
import com.ns.shortsnews.domain.models.OTPResult
import com.ns.shortsnews.domain.models.RegistrationResult
import com.ns.shortsnews.domain.models.UserSelectionResult
import com.ns.shortsnews.domain.models.UserSelectionsData
import com.ns.shortsnews.domain.usecase.user.UserRegistrationDataUseCase
import com.ns.shortsnews.domain.usecase.base.UseCaseResponse
import com.ns.shortsnews.domain.usecase.language.LanguageDataUseCase
import com.ns.shortsnews.domain.usecase.user.UserOtpValidationDataUseCase
import com.ns.shortsnews.domain.usecase.user.UserSelectionsDataUseCase
import com.ns.shortsnews.utils.AppPreference
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UserViewModel constructor(
    private val userRegistrationUseCases: UserRegistrationDataUseCase,
    private val otpValidationDataUseCases: UserOtpValidationDataUseCase,
    private val languageDataUseCase: LanguageDataUseCase,
    private val userSelectionsDataUseCase: UserSelectionsDataUseCase
) : ViewModel() {

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

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> get() = _errorState

    // OTP Error State
    private val _otpErrorState = MutableStateFlow<String?>(null)
    val otpErrorState: StateFlow<String?> get() = _otpErrorState

    private val _otpLoadingState = MutableStateFlow(false)
    val otpLoadingState: StateFlow<Boolean> get() = _otpLoadingState

    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean> get() = _loadingState

    //User selections
    private val _userSelectionSuccessState = MutableStateFlow<UserSelectionsData?>(null)
    val userSelectionSuccessState: StateFlow<UserSelectionsData?> get() = _userSelectionSuccessState


    fun updateFragment(fragmentType: String, bundle: Bundle) {
        viewModelScope.launch {
            bundle.putString("fragmentType", fragmentType)
            _fragmentStateFlow.emit(bundle)
        }
    }


    fun requestRegistrationApi(requestBody: Map<String, String>) {
        userRegistrationUseCases.invoke(viewModelScope, requestBody,
            object : UseCaseResponse<RegistrationResult> {
                override fun onSuccess(result: RegistrationResult) {
                    // Mapping the data model class, which will be used by UIs.
                    val userRegistration = UserRegistration().mapper(
                        status = result.status,
                        msg = result.msg,
                        OTP_id = result.data!!.OTP_id,
                        length = result.data.length,
                        email = result.data.email,
                        isUserRegistered = result.data.is_registered
                    )
                    _registrationSuccessState.value = userRegistration
                }

                override fun onError(apiError: ApiError) {
                    var errorMsg = _errorState.value
                    if(errorMsg == null || errorMsg == apiError.getErrorMessage()) {
                        errorMsg = "${apiError.getErrorMessage()} "
                    } else {
                        errorMsg = "${apiError.getErrorMessage()}"
                    }
                    _errorState.value = errorMsg
                }

                override fun onLoading(isLoading: Boolean) {
                    _loadingState.value = isLoading
                }
            }
        )
    }

    fun requestOtpValidationApi(requestBody: Map<String, String>) {
        otpValidationDataUseCases.invoke(viewModelScope, requestBody,
            object : UseCaseResponse<OTPResult> {
                override fun onSuccess(result: OTPResult) {
                    if(result.status == false) {
                        var msg = result.msg
                        if(_otpErrorState.value == result.msg) {
                            msg ="${result.msg} "
                        }
                        _otpErrorState.value = msg
                        return
                    }
                    val data = result.data
                    val otpValidation = UserOtp().mapper(
                        status = result.status, msg = result.msg,
                        email = data!!.my_profile.email, access_token = data.access_token,
                        name = data.my_profile.name, first_time_user = data.first_time_user,
                        userProfileImage = data.my_profile.image, user_id = data.my_profile.user_id,
                        age = data.my_profile.age, location = data.my_profile.location
                    )
                    _otpSuccessState.value = otpValidation
                }

                override fun onError(apiError: ApiError) {
                    var errorMsg = _errorState.value
                    if(errorMsg == null || errorMsg == apiError.getErrorMessage()) {
                        errorMsg = "${apiError.getErrorMessage()} "
                    } else {
                        errorMsg = "${apiError.getErrorMessage()}"
                    }
                    _errorState.value = errorMsg
                }

                override fun onLoading(isLoading: Boolean) {
                    _otpLoadingState.update { isLoading}
                }
            }
        )
    }

    fun requestLanguagesApi() {
        languageDataUseCase.invoke(viewModelScope, null,
            object : UseCaseResponse<LanguagesResult> {
                override fun onSuccess(type: LanguagesResult) {
                    if (type.status) {
                        val languageStr = mutableListOf<String>()
                        for (item in type.data) {
                            if (item.default_select) {
                                languageStr.add(item.id)
                            }
                        }
                        AppPreference.saveSelectedLanguagesToPreference(languageStr)
                        _languagesSuccessState.value = type.data
                    }
                    else _errorState.value = "Empty from api server"
                }

                override fun onError(apiError: ApiError) {
                    var errorMsg = _errorState.value
                    if(errorMsg == null || errorMsg == apiError.getErrorMessage()) {
                        errorMsg = "${apiError.getErrorMessage()} "
                    } else {
                        errorMsg = "${apiError.getErrorMessage()}"
                    }
                    _errorState.value = errorMsg
                }

                override fun onLoading(isLoading: Boolean) {
                    _loadingState.value = isLoading
                }
            }
        )
    }

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
                        _userSelectionSuccessState.value = result.data
                    }
                    else {
                        _otpErrorState.value = "Empty from api server"
                    }

                }

                override fun onError(apiError: ApiError) {
                    var errorMsg = _errorState.value
                    if(errorMsg == null || errorMsg == apiError.getErrorMessage()) {
                        errorMsg = "${apiError.getErrorMessage()} "
                    } else {
                        errorMsg = "${apiError.getErrorMessage()}"
                    }
                    _errorState.value = errorMsg
                }

                override fun onLoading(isLoading: Boolean) {
                    _loadingState.value = isLoading
                }

            })
    }
}