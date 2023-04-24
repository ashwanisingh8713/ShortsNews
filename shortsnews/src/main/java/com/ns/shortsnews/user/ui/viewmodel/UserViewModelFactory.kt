package com.ns.shortsnews.user.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ns.shortsnews.user.domain.usecase.user.UserOtpValidationDataUseCase
import com.ns.shortsnews.user.domain.usecase.user.UserProfileDataUseCase
import com.ns.shortsnews.user.domain.usecase.user.UserRegistrationDataUseCase

class UserViewModelFactory : ViewModelProvider.Factory {
    private lateinit var userRegisterDataUseCases: UserRegistrationDataUseCase
    private lateinit var userOtpValidationDataUseCases: UserOtpValidationDataUseCase
    private lateinit var profileDataUseCases: UserProfileDataUseCase
    fun inject(userUseCases: UserRegistrationDataUseCase,
               userOtpValidationDataUseCases: UserOtpValidationDataUseCase,
               profileDataUseCases: UserProfileDataUseCase) {
        this.userRegisterDataUseCases = userUseCases
        this.userOtpValidationDataUseCases = userOtpValidationDataUseCases
        this.profileDataUseCases = profileDataUseCases
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return UserViewModel(userRegisterDataUseCases, userOtpValidationDataUseCases, profileDataUseCases) as T
    }

}