package com.ns.shortsnews.user.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ns.shortsnews.user.domain.usecase.language.LanguageDataUseCase
import com.ns.shortsnews.user.domain.usecase.user.UserProfileDataUseCase

class LanguagesViewModelFactory: ViewModelProvider.Factory {

    private lateinit var languageDataUseCase: LanguageDataUseCase
    fun inject(languageDataUseCase: LanguageDataUseCase){
        this.languageDataUseCase = languageDataUseCase
    }


    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LanguagesDataViewModel( languageDataUseCase) as T
    }

}