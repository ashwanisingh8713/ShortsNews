package com.ns.shortsnews.user.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ns.shortsnews.user.domain.usecase.UserUseCases

class UserViewModelFactory : ViewModelProvider.Factory {
    private lateinit var userUseCases: UserUseCases
    fun inject(userUseCases: UserUseCases) {
        this.userUseCases = userUseCases
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return UserViewModel(userUseCases) as T
    }

}