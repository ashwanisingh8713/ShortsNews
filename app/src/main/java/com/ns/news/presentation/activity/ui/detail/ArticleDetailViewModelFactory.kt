package com.ns.news.presentation.activity.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ns.news.data.db.CellDao

object ArticleDetailViewModelFactory: ViewModelProvider.Factory {

    private lateinit var cellDao: CellDao

    fun inject(cellDao: CellDao) {
        ArticleDetailViewModelFactory.cellDao = cellDao
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ArticleDetailViewModel(
            cellDao
        ) as T
    }
}