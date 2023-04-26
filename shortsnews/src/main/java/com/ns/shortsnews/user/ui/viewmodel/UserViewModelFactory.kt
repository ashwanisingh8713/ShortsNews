package com.ns.shortsnews.user.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ns.shortsnews.user.domain.usecase.user.UserOtpValidationDataUseCase
import com.ns.shortsnews.user.domain.usecase.user.UserRegistrationDataUseCase

class UserViewModelFactory : ViewModelProvider.Factory {
    private lateinit var userRegisterDataUseCases: UserRegistrationDataUseCase
    private lateinit var userOtpValidationDataUseCases: UserOtpValidationDataUseCase

    fun inject(userUseCases: UserRegistrationDataUseCase,
               userOtpValidationDataUseCases: UserOtpValidationDataUseCase,
               ) {
        this.userRegisterDataUseCases = userUseCases
        this.userOtpValidationDataUseCases = userOtpValidationDataUseCases

    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return UserViewModel(userRegisterDataUseCases, userOtpValidationDataUseCases) as T
    }

}