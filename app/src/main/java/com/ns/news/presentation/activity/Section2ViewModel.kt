package com.ns.news.presentation.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ns.news.data.db.Cell
import com.ns.news.data.db.Section
import com.ns.news.domain.repositories.ApiRepository
import kotlinx.coroutines.flow.Flow

class Section2ViewModel(private val repository: ApiRepository) : ViewModel() {

    private var articleNdWidgetResult: Flow<PagingData<Section>>? = null

    suspend fun getSections(sectionId: String): Flow<PagingData<Section>> {
        val newResult: Flow<PagingData<Section>> =
            repository.getSectionsDB(sectionId).cachedIn(viewModelScope)
        articleNdWidgetResult = newResult
        return newResult
    }

}