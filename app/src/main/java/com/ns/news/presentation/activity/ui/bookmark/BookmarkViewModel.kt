package com.ns.news.presentation.activity.ui.bookmark

import androidx.lifecycle.ViewModel
import com.ns.news.data.db.Bookmark
import com.ns.news.data.db.BookmarkDao
import kotlinx.coroutines.flow.Flow

class BookmarkViewModel(private val bookmarkDao: BookmarkDao):ViewModel() {

    fun getAllBookmarks(): Flow<List<Bookmark>> = bookmarkDao.getAllBookmarks()

}