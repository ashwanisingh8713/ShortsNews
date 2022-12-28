package com.ns.news.presentation.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ns.news.domain.repositories.ApiRepository

object SectionViewModelFactory : ViewModelProvider.Factory {

    private lateinit var repository: ApiRepository

    fun inject(repository: ApiRepository) {
        SectionViewModelFactory.repository = repository
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SectionViewModel(
            repository
        ) as T
    }
}