package com.ns.news.presentation.activity.ui.detail

import androidx.lifecycle.ViewModel
import com.ns.news.data.db.Cell
import com.ns.news.data.db.CellDao
import kotlinx.coroutines.flow.Flow

class ArticleDetailViewModel(val cellDao: CellDao): ViewModel() {
    fun getArticle(sectionId: String): Flow<List<Cell>> = cellDao.detailArticles(sectionId)
}

