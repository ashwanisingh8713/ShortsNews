package com.ns.shortsnews.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ns.shortsnews.domain.usecase.language.LanguageDataUseCase
import com.ns.shortsnews.domain.usecase.user.UserOtpValidationDataUseCase
import com.ns.shortsnews.domain.usecase.user.UserRegistrationDataUseCase
import com.ns.shortsnews.domain.usecase.user.UserSelectionsDataUseCase

class UserViewModelFactory : ViewModelProvider.Factory {
    private lateinit var userOtpValidationDataUseCases: UserOtpValidationDataUseCase
    private lateinit var languageDataUseCase: LanguageDataUseCase
    private lateinit var userSelectionsDataUseCase: UserSelectionsDataUseCase

    fun inject(
               userOtpValidationDataUseCases: UserOtpValidationDataUseCase,
               languageDataUseCase: LanguageDataUseCase,
               userSelectionsDataUseCase: UserSelectionsDataUseCase
               ) {
        this.userOtpValidationDataUseCases = userOtpValidationDataUseCases
        this.languageDataUseCase = languageDataUseCase
        this.userSelectionsDataUseCase = userSelectionsDataUseCase

    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return UserViewModel(userOtpValidationDataUseCases, languageDataUseCase, userSelectionsDataUseCase) as T
    }

}