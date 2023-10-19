package com.ns.shortsnews.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ns.shortsnews.domain.usecase.language.LanguageDataUseCase
import com.ns.shortsnews.domain.usecase.user.UserOtpValidationDataUseCase
import com.ns.shortsnews.domain.usecase.user.UserRegistrationDataUseCase
import com.ns.shortsnews.domain.usecase.user.UserSelectionsDataUseCase

class OTPViewModelFactory : ViewModelProvider.Factory {
    private lateinit var otpValidationDataUseCases: UserOtpValidationDataUseCase
    private lateinit var userSelectionsDataUseCase: UserSelectionsDataUseCase

    fun inject(userUseCases: UserOtpValidationDataUseCase, userSelectionsDataUseCase: UserSelectionsDataUseCase

               ) {
        this.otpValidationDataUseCases = userUseCases
        this.userSelectionsDataUseCase = userSelectionsDataUseCase


    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return OTPViewModel(otpValidationDataUseCases, userSelectionsDataUseCase) as T
    }

}