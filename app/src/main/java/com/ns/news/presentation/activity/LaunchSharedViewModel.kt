package com.ns.news.presentation.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ns.news.data.api.model.SectionItem
import com.ns.news.data.db.Section
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class LaunchSharedViewModel : ViewModel() {

  private val _drawerShareClick = MutableSharedFlow<SharedClickEvent>() // 1
  val drawerSharedClick = _drawerShareClick.asSharedFlow() // 2

    private val _navigationItemClick = MutableSharedFlow<Pair<Section, SectionItem?>>() // 1
    val navigationItemClick = _navigationItemClick.asSharedFlow() // 2

   fun openDrawer() {
    viewModelScope.launch { // 1
      _drawerShareClick.emit(SharedClickEvent.DRAWER_OPEN) // 4
    }
  }

    fun closeDrawer() {
        viewModelScope.launch { // 1
            _drawerShareClick.emit(SharedClickEvent.DRAWER_CLOSE) // 4
        }
    }

    fun navigationItemClick(section:Section, subSection:SectionItem?) {
        viewModelScope.launch { // 1
            _navigationItemClick.emit(Pair(section, subSection)) // 4
        }
        closeDrawer();
    }

}

