package com.ns.shortsnews.user.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ns.shortsnews.user.domain.usecase.user.UserChoiceDataUseCase

class UserChoiceViewModelFactory: ViewModelProvider.Factory {

    private lateinit var profileDataUseCases: UserChoiceDataUseCase

    fun inject(profileDataUseCases: UserChoiceDataUseCase){
        this.profileDataUseCases = profileDataUseCases
    }


    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return UserChoiceViewModel( profileDataUseCases) as T
    }

}