package com.ns.shortsnews.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ns.shortsnews.domain.models.InterestsTable
import com.ns.shortsnews.domain.models.LanguageTable
import com.ns.shortsnews.domain.repository.InterestsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class InterestsViewModel(private val interestsRepository: InterestsRepository) :ViewModel() {

    private var _sharedInsertInTable = MutableSharedFlow<InterestsTable>()
    val sharedInsertInTable = _sharedInsertInTable.asSharedFlow()

    private var _sharedDeleteFromTable = MutableSharedFlow<InterestsTable>()
    val sharedDeleteFromTable = _sharedDeleteFromTable.asSharedFlow()

    fun insert(id:String, name:String, isSelected:Boolean, icon:String){
        val interestData = InterestsTable(id, name, isSelected,icon)
        viewModelScope.launch {
            interestsRepository.insert(interestData)
            _sharedInsertInTable.emit(interestData)
        }

    }
    fun delete(id:String, name:String, isSelected:Boolean, icon:String){
        val interestData = InterestsTable(id, name, isSelected,icon)
        viewModelScope.launch {
            interestsRepository.delete(interestData)
            _sharedDeleteFromTable.emit(interestData)
        }
    }
    fun update(id:String, name:String, isSelected:Boolean, icon:String) {
        val interestsData = InterestsTable(id, name,isSelected,icon)
        viewModelScope.launch {
            interestsRepository.update(id,interestsData)
            _sharedInsertInTable.emit(interestsData)
        }

    }
    suspend fun isEmpty():Boolean  = interestsRepository.isEmpty()


    suspend fun getAllInterestedItems(): Flow<List<InterestsTable>> = interestsRepository.getAllInterestsData()
}