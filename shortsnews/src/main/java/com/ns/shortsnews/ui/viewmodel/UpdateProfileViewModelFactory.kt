package com.ns.shortsnews.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ns.shortsnews.domain.usecase.updateuser.DeleteProfileUseCase
import com.ns.shortsnews.domain.usecase.updateuser.UpdateUserUseCase
import com.ns.shortsnews.domain.usecase.user.UserProfileDataUseCase

class UpdateProfileViewModelFactory: ViewModelProvider.Factory {

    private lateinit var updateProfileDataUseCases: UpdateUserUseCase
    private lateinit var deleteProfileUseCase: DeleteProfileUseCase

    fun inject(updateProfileDataUseCase: UpdateUserUseCase, deleteProfileUseCase: DeleteProfileUseCase){
        this.updateProfileDataUseCases = updateProfileDataUseCase
        this.deleteProfileUseCase = deleteProfileUseCase

    }


    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return UpdateProfileViewModel( updateProfileDataUseCases, deleteProfileUseCase) as T
    }

}