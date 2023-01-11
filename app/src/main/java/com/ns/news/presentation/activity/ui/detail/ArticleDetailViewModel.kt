package com.ns.news.presentation.activity.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.ns.news.data.api.model.CellsItem.Companion.CELLTYPE_ARTICLE
import com.ns.news.data.api.model.CellsItem.Companion.CELLTYPE_WIDGET
import com.ns.news.data.db.Cell
import com.ns.news.data.db.CellDao
import kotlinx.coroutines.flow.Flow

class ArticleDetailViewModel(private val cellDao: CellDao): ViewModel() {
    fun getArticle(sectionId: String, cellType: String, type: String): Flow<List<Cell>> {
        return when(cellType) {
            CELLTYPE_WIDGET-> cellDao.getWidgetArticles(sectionId, type)
            CELLTYPE_ARTICLE-> cellDao.getArticles(sectionId, cellType)
            else -> cellDao.getAllArticlesBySectionId(sectionId)
        }
    }
}

