package com.ns.news.presentation.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ns.news.data.db.ReadDao
import com.ns.news.domain.repositories.ApiRepository

object NewsSharedViewModelFactory: ViewModelProvider.Factory {

  private lateinit var readDao: ReadDao

  fun inject(readDao: ReadDao) {
    NewsSharedViewModelFactory.readDao = readDao
  }

  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    return NewsSharedViewModel(readDao) as T
  }
}
