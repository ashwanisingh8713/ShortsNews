package com.ns.news.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ns.news.data.repositories.NewsRepository
import com.ns.news.domain.repositories.ApiRepository

class TopicViewModelFactory(var repository: ApiRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TopicViewModel(
            repository
        ) as T
    }
}