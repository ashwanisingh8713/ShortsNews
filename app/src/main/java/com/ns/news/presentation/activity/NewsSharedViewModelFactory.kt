package com.ns.news.presentation.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ns.news.data.db.BookmarkDao
import com.ns.news.data.db.ReadDao
import com.ns.news.domain.repositories.ApiRepository

object NewsSharedViewModelFactory: ViewModelProvider.Factory {

  private lateinit var readDao: ReadDao
  private lateinit var bookmarkDao: BookmarkDao

  fun inject(readDao: ReadDao, bookmarkDao: BookmarkDao) {
    NewsSharedViewModelFactory.readDao = readDao
    NewsSharedViewModelFactory.bookmarkDao = bookmarkDao
  }

  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    return NewsSharedViewModel(readDao, bookmarkDao) as T
  }
}
