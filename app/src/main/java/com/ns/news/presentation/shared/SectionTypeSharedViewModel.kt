package com.ns.news.presentation.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ns.news.data.api.model.SectionItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class SectionTypeSharedViewModel : ViewModel() {

  private val _drawerSharedViewModel = MutableSharedFlow<List<SectionItem>>() // 1
  val drawerSharedViewModel = _drawerSharedViewModel.asSharedFlow() // 2

  private val _breadcrumbSharedViewModel = MutableSharedFlow<List<SectionItem>>() // 3
  val breadcrumbSharedViewModel = _breadcrumbSharedViewModel.asSharedFlow() // 4

  fun setDrawerSections(drawerSections: List<SectionItem>) { //5
    viewModelScope.launch {
      _drawerSharedViewModel.emit(drawerSections)
    }
  }

  fun setBreadcrumbSections(breadcrumbSections: List<SectionItem>) { //6
    viewModelScope.launch {
      _breadcrumbSharedViewModel.emit(breadcrumbSections)
    }

  }
}

