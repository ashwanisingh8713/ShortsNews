package com.ns.shortsnews.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ns.shortsnews.data.mapper.UserVideoCategory
import com.ns.shortsnews.domain.exception.ApiError
import com.ns.shortsnews.domain.models.UpdateCategories
import com.ns.shortsnews.domain.models.VideoCategoryResult
import com.ns.shortsnews.domain.usecase.base.UseCaseResponse
import com.ns.shortsnews.domain.usecase.video_category.UpdateVideoCategoriesUseCase
import com.ns.shortsnews.domain.usecase.video_category.VideoCategoryUseCase
import com.ns.shortsnews.utils.AppPreference
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Created by Ashwani Kumar Singh on 24,April,2023.
 */
class VideoCategoryViewModel(
    private val videoCategoryUseCase: VideoCategoryUseCase,
    private val updateVideoCategoriesUseCase: UpdateVideoCategoriesUseCase
) : ViewModel() {

    private val _videoCategorySuccessState = MutableSharedFlow<UserVideoCategory?>(replay = 0, extraBufferCapacity = 1)
    val videoCategorySuccessState: SharedFlow<UserVideoCategory?> get() = _videoCategorySuccessState

    private val _updateCategoriesSuccessState = MutableStateFlow<UpdateCategories?>(null)
    val updateCategoriesSuccessState: StateFlow<UpdateCategories?> get() = _updateCategoriesSuccessState

    val _errorState = MutableSharedFlow<String?>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val errorState: SharedFlow<String?> get() = _errorState

    private val _loadingState = MutableStateFlow(true)
    val loadingState: MutableStateFlow<Boolean> get() = _loadingState

    fun loadVideoCategory() {
        val languageString = AppPreference.getSelectedLanguagesAsString()
        videoCategoryUseCase.invoke(
            scope = viewModelScope,
            params = languageString,
            onResult = object :
                UseCaseResponse<VideoCategoryResult> {
                override fun onSuccess(result: VideoCategoryResult) {
                    if(result.status) {
                        AppPreference.isLanguageSelected = true
                        AppPreference.isRefreshRequired = false
                        val userVideoCategory = UserVideoCategory().mapper(
                            status = result.status,
                            videoCategories = result.data
                        )
                        _videoCategorySuccessState.tryEmit(userVideoCategory)
                    } else {
                        _errorState.tryEmit("Something went wrong")
                    }

                }

                override fun onError(apiError: ApiError) {
                    viewModelScope.launch {
                        _errorState.emit(apiError.getErrorMessage())
                    }

                }

                override fun onLoading(isLoading: Boolean) {
                    _loadingState.value = isLoading
                }

            })

    }

    fun updateCategoriesApi(categories: Any) {
        updateVideoCategoriesUseCase.invoke(
            scope = viewModelScope,
            params = categories,
            onResult = object : UseCaseResponse<UpdateCategories> {
                override fun onSuccess(type: UpdateCategories) {
                    _updateCategoriesSuccessState.value = type
                }

                override fun onError(apiError: ApiError) {
                    _errorState.tryEmit(apiError.getErrorMessage())
                }

                override fun onLoading(isLoading: Boolean) {
                    _loadingState.value = isLoading
                }

            })
    }

}