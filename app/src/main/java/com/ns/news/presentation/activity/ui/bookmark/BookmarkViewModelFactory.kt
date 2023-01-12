package com.ns.news.presentation.activity.ui.bookmark

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ns.news.data.db.BookmarkDao

class BookmarkViewModelFactory(var bookmarkDao: BookmarkDao): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BookmarkViewModel(
            bookmarkDao
        ) as T
    }
}