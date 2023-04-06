package com.videopager.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

object SharedEventViewModelFactory:ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SharedEventViewModel() as T
    }
}