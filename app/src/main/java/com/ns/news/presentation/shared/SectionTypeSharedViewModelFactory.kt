package com.ns.news.presentation.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SectionTypeSharedViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SectionTypeSharedViewModel(
        ) as T
    }
}