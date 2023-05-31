package com.ns.shortsnews.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ns.shortsnews.domain.repository.InterestsRepository

@Suppress("UNCHECKED_CAST")
class InterestsViewModelFactory(private val interestsRepository: InterestsRepository) :ViewModelProvider.Factory{

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InterestsViewModel::class.java)){
            return InterestsViewModel(interestsRepository) as T
        }
        throw IllegalArgumentException("Unable to connect view model")
    }
}