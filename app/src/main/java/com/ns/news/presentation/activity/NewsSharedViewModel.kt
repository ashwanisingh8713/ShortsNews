package com.ns.news.presentation.activity

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ns.news.data.api.model.AWDataItem
import com.ns.news.data.api.model.SectionItem
import com.ns.news.data.db.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class NewsSharedViewModel(private val readDao: ReadDao, private val bookmarkDao: BookmarkDao) : ViewModel() {

    private val _sharedChannelEvent = MutableSharedFlow<SharedChannelEvent>() // 1
    val sharedChannelEvent = _sharedChannelEvent.asSharedFlow() // 2

    private val _navigationItemClick = MutableSharedFlow<Pair<Section, SectionItem?>>() // 1
    val navigationItemClick = _navigationItemClick.asSharedFlow() // 2

    private val _detailArticle = MutableSharedFlow<AWDataItem>() // 1
    val detailArticle = _detailArticle.asSharedFlow() // 2

    suspend fun isArticleBookmarked(articleId: String) = bookmarkDao.bookmarkArticle(articleId)

    fun openDrawer() {
        viewModelScope.launch { // 1
            _sharedChannelEvent.emit(SharedChannelEvent.DRAWER_OPEN) // 4
        }
    }

    fun closeDrawer() {
        viewModelScope.launch { // 1
            _sharedChannelEvent.emit(SharedChannelEvent.DRAWER_CLOSE) // 4
        }
    }

    fun disableDrawer() {
        viewModelScope.launch { // 1
            _sharedChannelEvent.emit(SharedChannelEvent.DRAWER_DISABLE) // 4
        }
    }
    fun enableDrawer() {
        viewModelScope.launch { // 1
            _sharedChannelEvent.emit(SharedChannelEvent.DRAWER_ENABLE) // 4
        }
    }

    fun navigationItemClick(section: Section, subSection: SectionItem?) {
        viewModelScope.launch { // 1
            _navigationItemClick.emit(Pair(section, subSection)) // 4
        }
        closeDrawer();
    }

    fun sendArticle(awDataItem: AWDataItem) {
        viewModelScope.launch {
            _detailArticle.emit(awDataItem)
        }
    }

    fun readArticle(articleId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            readDao.insertReadArticle(TableRead(articleId = articleId))
        }
    }

    fun checkArticleBookmarked(articleId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            bookmarkDao.bookmarkArticle(articleId).collect{
                if(it == null) {
                    _sharedChannelEvent.emit(SharedChannelEvent.UN_BOOKMARKED)
                } else {
                    _sharedChannelEvent.emit(SharedChannelEvent.BOOKMARKED)
                }
            }
        }

    }

    fun addToBookmark(awDataItem: AWDataItem, cellType: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val data = mutableListOf<AWDataItem>()
            data.add(awDataItem)
            val bookmark = Bookmark(
                type = cellType,
                articleId = awDataItem.articleId,
                data = awDataItem
            )
            bookmarkDao.insert(bookmark)
        }
    }

    fun removeFromBookmark(articleId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            bookmarkDao.deleteByArticleId(articleId)
        }
    }


}

