package com.ns.shortsnews.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ns.shortsnews.domain.usecase.language.LanguageDataUseCase
import com.ns.shortsnews.domain.usecase.user.UserOtpValidationDataUseCase
import com.ns.shortsnews.domain.usecase.user.UserRegistrationDataUseCase

class UserViewModelFactory : ViewModelProvider.Factory {
    private lateinit var userRegisterDataUseCases: UserRegistrationDataUseCase
    private lateinit var userOtpValidationDataUseCases: UserOtpValidationDataUseCase
    private lateinit var languageDataUseCase: LanguageDataUseCase

    fun inject(userUseCases: UserRegistrationDataUseCase,
               userOtpValidationDataUseCases: UserOtpValidationDataUseCase,
               languageDataUseCase: LanguageDataUseCase
               ) {
        this.userRegisterDataUseCases = userUseCases
        this.userOtpValidationDataUseCases = userOtpValidationDataUseCases
        this.languageDataUseCase = languageDataUseCase

    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return UserViewModel(userRegisterDataUseCases, userOtpValidationDataUseCases, languageDataUseCase) as T
    }

}