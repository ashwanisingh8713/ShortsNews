package com.ns.shortsnews.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ns.shortsnews.domain.repository.LanguageRepository
import com.ns.shortsnews.domain.models.LanguageTable
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LanguageViewModel(private val languageTableRepository: LanguageRepository): ViewModel() {

    private var _sharedInsertInTable = MutableSharedFlow<LanguageTable>()
    val sharedInsertInTable = _sharedInsertInTable.asSharedFlow()

    private var _sharedDeleteFromTable = MutableSharedFlow<LanguageTable>()
    val sharedDeleteFromTable = _sharedDeleteFromTable.asSharedFlow()

    fun insert(id:String, name:String, slug:String, isSelected:Boolean, icon:String) {
        viewModelScope.launch {
            val languageData = LanguageTable(id, name, slug,isSelected,icon)
            languageTableRepository.insert(languageData)
            _sharedInsertInTable.emit(languageData)
        }

    }
    fun delete(id:String, name:String, slug:String, isSelected:Boolean, icon:String){
        viewModelScope.launch {
            val languageData = LanguageTable(id, name, slug,isSelected,icon)
            languageTableRepository.delete(languageData)
            _sharedDeleteFromTable.emit(languageData)
        }
    }

    fun update(id:String, name:String, slug:String, isSelected:Boolean, icon:String) {

        viewModelScope.launch {
            val languageData = LanguageTable(id, name, slug,isSelected,icon)
            languageTableRepository.update(id,isSelected)
            _sharedInsertInTable.emit(languageData)
        }

    }
    suspend fun isEmpty():Boolean  = languageTableRepository.isEmpty()

    suspend fun getAllLanguage(): Flow<List<LanguageTable>> = languageTableRepository.getAllLanguageData()
}