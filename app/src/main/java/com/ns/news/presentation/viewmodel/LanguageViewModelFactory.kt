package com.news.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ns.news.data.repositories.NewsRepository
import com.ns.news.domain.repositories.ApiRepository
import com.ns.news.presentation.viewmodel.LanguageViewModel

object LanguageViewModelFactory: ViewModelProvider.Factory {
    private lateinit var repository: ApiRepository

    fun inject(repository: NewsRepository) {
        LanguageViewModelFactory.repository = repository
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LanguageViewModel(
            repository
        ) as T
    }
}