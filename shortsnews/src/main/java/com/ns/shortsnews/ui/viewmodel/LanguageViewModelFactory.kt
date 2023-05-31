package com.ns.shortsnews.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ns.shortsnews.domain.repository.LanguageRepository

@Suppress("UNCHECKED_CAST")
class LanguageViewModelFactory(private val languageItemRepository: LanguageRepository) :ViewModelProvider.Factory{

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LanguageViewModel::class.java)){
            return LanguageViewModel(languageItemRepository) as T
        }
        throw IllegalArgumentException("Unable to connect view model")
    }
}