package com.ns.shortsnews.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ns.shortsnews.data.mapper.UserRegistration
import com.ns.shortsnews.domain.exception.ApiError
import com.ns.shortsnews.domain.models.RegistrationResult
import com.ns.shortsnews.domain.usecase.base.UseCaseResponse
import com.ns.shortsnews.domain.usecase.user.UserRegistrationDataUseCase
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Created by Ashwani Kumar Singh on 18,October,2023.
 */
class LoginViewModel(private val userRegistrationUseCases: UserRegistrationDataUseCase):ViewModel() {

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> get() = _errorState

    // Registration
    private val _registrationSuccessState = MutableSharedFlow<UserRegistration?>(replay = 1, onBufferOverflow = BufferOverflow.DROP_LATEST)
    val registrationSuccessState: SharedFlow<UserRegistration?> get() = _registrationSuccessState

    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean> get() = _loadingState

    fun clearRegistrationState() {
        _registrationSuccessState.tryEmit(null)
    }



    fun requestRegistrationApi(requestBody: Map<String, String>) {
        userRegistrationUseCases.invoke(viewModelScope, requestBody,
            object : UseCaseResponse<RegistrationResult> {
                override fun onSuccess(result: RegistrationResult) {
                    if(result.status == false) {
                        var msg = result.msg
                        if(_errorState.value == result.msg) {
                            msg ="${result.msg} "
                        } else {
                            msg = "${result.msg}"
                        }
                        _errorState.value = msg
                        return
                    } else {
                        // Mapping the data model class, which will be used by UIs.
                        val userRegistration = UserRegistration().mapper(
                            status = result.status,
                            msg = result.msg,
                            OTP_id = result.data!!.OTP_id,
                            length = result.data.length,
                            email = result.data.email,
                            isUserRegistered = result.data.is_registered
                        )
                        _registrationSuccessState.tryEmit(userRegistration)
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
            }
        )
    }
}