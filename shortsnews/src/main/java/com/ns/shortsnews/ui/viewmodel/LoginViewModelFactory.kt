package com.ns.shortsnews.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ns.shortsnews.domain.usecase.language.LanguageDataUseCase
import com.ns.shortsnews.domain.usecase.user.UserOtpValidationDataUseCase
import com.ns.shortsnews.domain.usecase.user.UserRegistrationDataUseCase
import com.ns.shortsnews.domain.usecase.user.UserSelectionsDataUseCase

class LoginViewModelFactory : ViewModelProvider.Factory {
    private lateinit var userRegisterDataUseCases: UserRegistrationDataUseCase

    fun inject(userUseCases: UserRegistrationDataUseCase

               ) {
        this.userRegisterDataUseCases = userUseCases


    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LoginViewModel(userRegisterDataUseCases) as T
    }

}