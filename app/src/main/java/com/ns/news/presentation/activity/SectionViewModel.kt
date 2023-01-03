package com.ns.news.presentation.activity

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ns.news.data.api.model.SectionItem
import com.ns.news.domain.repositories.ApiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.ns.news.domain.Result
import com.ns.news.domain.model.SectionDrawer

class SectionViewModel(private val repository: ApiRepository): ViewModel() {
    val viewState: StateFlow<Pair<List<SectionItem>, List<SectionDrawer>>> get() = _viewState
    private val _viewState = MutableStateFlow(Pair<List<SectionItem>, List<SectionDrawer>>(emptyList(), emptyList()))

    fun requestSections(languageId: String) {
        viewModelScope.launch {
            when (val result = repository.getSectionsDirect(languageId)) {
                is Result.Success -> handlePageData(result.value)
                is Result.Failure -> handleFailure(result.cause)
            }
        }
    }

    private fun handlePageData(sections: List<SectionItem>) {
        var breadcrums: MutableList<SectionDrawer> = mutableListOf()
        var drawer: MutableList<SectionItem> = mutableListOf()
        for(section in sections)
            if(section.inBreadcrumb) {
                breadcrums.add(SectionDrawer(section.sectionId, section.name, section.api, section.subSections!!.isEmpty()))
            } else {
                drawer.add(section)
            }
        _viewState.value = Pair(drawer, breadcrums)
    }

    private fun handleFailure(cause: Throwable) {
        Log.e("Ashwani", "Section API :: ${cause.message}")
    }

}