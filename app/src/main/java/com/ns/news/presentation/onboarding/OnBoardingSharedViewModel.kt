package com.ns.news.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ns.news.data.api.model.LanguagesItem
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class OnBoardingSharedViewModel : ViewModel() {

  private val _onBoardingSharedViewModel = MutableSharedFlow<LanguagesItem>() // 1
  val onBoardingSharedViewModel = _onBoardingSharedViewModel.asSharedFlow() // 2

  fun setLanguageSelection(languageItem: LanguagesItem) { //5
    viewModelScope.launch {
      _onBoardingSharedViewModel.emit(languageItem)
    }
  }

}

