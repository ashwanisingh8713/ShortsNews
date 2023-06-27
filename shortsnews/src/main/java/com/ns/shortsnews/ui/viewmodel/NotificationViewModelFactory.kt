package com.ns.shortsnews.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ns.shortsnews.domain.usecase.notification.FCMTokenDataUseCase

class NotificationViewModelFactory:ViewModelProvider.Factory {
    private lateinit var notificationFCMTokenDataUseCase: FCMTokenDataUseCase

    fun inject(notificationFCMTokenDataUseCase: FCMTokenDataUseCase) {
        this.notificationFCMTokenDataUseCase = notificationFCMTokenDataUseCase
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NotificationViewModel(notificationFCMTokenDataUseCase) as T
    }
}