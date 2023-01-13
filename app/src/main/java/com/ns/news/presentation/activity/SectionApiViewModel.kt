package com.ns.news.presentation.activity

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ns.news.data.db.Section
import com.ns.news.domain.repositories.ApiRepository
import kotlinx.coroutines.launch
import com.ns.news.domain.Result

class SectionApiViewModel(private val repository: ApiRepository): ViewModel() {
//    val viewState: StateFlow<Pair<List<Section>, List<Section>>> get() = _viewState
//    private val _viewState = MutableStateFlow(Pair<List<Section>, List<Section>>(emptyList(), emptyList()))

    fun requestSections(languageId: String) {
        viewModelScope.launch {
            when (val result = repository.getSectionsDirect(languageId)) {
                is Result.Success -> handlePageData(result.value)
                is Result.Failure -> handleFailure(result.cause)
            }
        }
    }

    private fun handlePageData(sections: List<Section>) {
        Log.i("", "")
        /*var breadcrums: MutableList<Section> = mutableListOf()
        var drawer: MutableList<Section> = mutableListOf()
        for(section in sections) {
            if (section.inBreadcrumb) {
                breadcrums.add(section)
            }
            if (section.inHamburger) {
                drawer.add(section)
            }
        }*/
//        _viewState.value = Pair(drawer, breadcrums)
    }

    private fun handleFailure(cause: Throwable) {
        Log.e("NEWSAPP", "Section API :: ${cause.message}")
        Log.e("NEWSAPP", "Section API :: ${cause.message}")
        Log.e("NEWSAPP", "Section API :: ${cause.message}")
        Log.e("NEWSAPP", "Section API :: ${cause.message}")
        Log.e("NEWSAPP", "Section API :: ${cause.message}")
        Log.e("NEWSAPP", "Section API :: ${cause.message}")
    }

}