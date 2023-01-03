package com.ns.news.presentation.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ns.news.domain.repositories.ApiRepository

object Section2ViewModelFactory : ViewModelProvider.Factory {

    private lateinit var repository: ApiRepository

    fun inject(repository: ApiRepository) {
        Section2ViewModelFactory.repository = repository
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return Section2ViewModel(
            repository
        ) as T
    }
}