package com.ns.news.presentation.activity.ui.home

import androidx.lifecycle.ViewModel
import com.ns.news.data.db.Section
import com.ns.news.data.db.SectionDao
import kotlinx.coroutines.flow.Flow

class SectionDBViewModel(private val sectionDao: SectionDao):ViewModel() {

    fun getBreadcrumb(): Flow<List<Section>> = sectionDao.getBreadcrumb()
    fun getHamburger(): Flow<List<Section>> = sectionDao.getHamburger()

}