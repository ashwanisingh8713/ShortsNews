package com.ns.news.presentation.activity.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ns.news.data.db.SectionDao

object SectionDBViewModelFactory: ViewModelProvider.Factory {

    private lateinit var sectionDao: SectionDao

    fun inject(sectionDao: SectionDao) {
        SectionDBViewModelFactory.sectionDao = sectionDao
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SectionDBViewModel(
            sectionDao
        ) as T
    }
}