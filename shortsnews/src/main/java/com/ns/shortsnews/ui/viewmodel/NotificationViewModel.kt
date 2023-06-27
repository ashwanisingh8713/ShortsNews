package com.ns.shortsnews.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ns.shortsnews.domain.exception.ApiError
import com.ns.shortsnews.domain.models.StatusResult
import com.ns.shortsnews.domain.usecase.base.UseCaseResponse
import com.ns.shortsnews.domain.usecase.notification.FCMTokenDataUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.RequestBody

class NotificationViewModel(private val notificationUseCase: FCMTokenDataUseCase):ViewModel() {

    // Send Firebase token
    private val _sendFcmTokenSuccessState = MutableStateFlow<StatusResult?>(null)
    val SendNotificationSuccessState: MutableStateFlow<StatusResult?> get() = _sendFcmTokenSuccessState

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> get() = _errorState

    fun requestSendFcmToken(data:Map<String, String>){
        notificationUseCase.invoke(viewModelScope,data,
        object : UseCaseResponse<StatusResult>
        {
            override fun onSuccess(type: StatusResult) {
                _sendFcmTokenSuccessState.value = type
            }

            override fun onError(apiError: ApiError) {
                _errorState.value = apiError.getErrorMessage()
            }

            override fun onLoading(isLoading: Boolean) {
                Log.i("","")
            }

        })
    }
}