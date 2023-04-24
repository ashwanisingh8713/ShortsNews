package com.ns.shortsnews.user.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ns.shortsnews.user.domain.usecase.user.UserOtpValidationDataUseCases
import com.ns.shortsnews.user.domain.usecase.user.UserProfileDataUseCases
import com.ns.shortsnews.user.domain.usecase.user.UserRegistrationDataUseCases

class UserViewModelFactory : ViewModelProvider.Factory {
    private lateinit var userRegisterDataUseCases: UserRegistrationDataUseCases
    private lateinit var userOtpValidationDataUseCases: UserOtpValidationDataUseCases
    private lateinit var profileDataUseCases: UserProfileDataUseCases
    fun inject(userUseCases: UserRegistrationDataUseCases,
               userOtpValidationDataUseCases: UserOtpValidationDataUseCases,
               profileDataUseCases: UserProfileDataUseCases) {
        this.userRegisterDataUseCases = userUseCases
        this.userOtpValidationDataUseCases = userOtpValidationDataUseCases
        this.profileDataUseCases = profileDataUseCases
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return UserViewModel(userRegisterDataUseCases, userOtpValidationDataUseCases, profileDataUseCases) as T
    }

}