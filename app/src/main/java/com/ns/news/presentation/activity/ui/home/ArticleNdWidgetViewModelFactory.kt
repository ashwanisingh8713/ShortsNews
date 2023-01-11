package com.ns.news.presentation.activity.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ns.news.data.db.ReadDao
import com.ns.news.domain.repositories.ApiRepository

object ArticleNdWidgetViewModelFactory: ViewModelProvider.Factory {

    private lateinit var repository: ApiRepository
    private lateinit var readDao: ReadDao

    fun inject(repository: ApiRepository, readDao: ReadDao) {
        ArticleNdWidgetViewModelFactory.repository = repository
        ArticleNdWidgetViewModelFactory.readDao = readDao
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ArticleNdWidgetViewModel(
            repository, readDao
        ) as T
    }
}