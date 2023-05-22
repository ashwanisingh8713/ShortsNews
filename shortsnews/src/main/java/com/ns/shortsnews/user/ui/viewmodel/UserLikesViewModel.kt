package com.ns.shortsnews.user.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ns.shortsnews.user.domain.exception.ApiError
import com.ns.shortsnews.user.domain.models.LikesResult
import com.ns.shortsnews.user.domain.usecase.base.UseCaseResponse
import com.ns.shortsnews.user.domain.usecase.bookmark.UserProfileLikesListUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UserLikesViewModel(private val channelsDataUseCase: UserProfileLikesListUseCase): ViewModel() {

    // Profile
    private val _userLikesSuccessState = MutableStateFlow<LikesResult?>(null)
    val LikesSuccessState: StateFlow<LikesResult?> get() = _userLikesSuccessState

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> get() = _errorState

    private val _loadingState = MutableStateFlow(true)
    val loadingState: MutableStateFlow<Boolean> get() = _loadingState


    fun requestLikesApi() {
        channelsDataUseCase.invoke(viewModelScope, null,
            object : UseCaseResponse<LikesResult> {
                override fun onSuccess(type: LikesResult) {
                    _userLikesSuccessState.value = type
                    _loadingState.value = false
                }

                override fun onError(apiError: ApiError) {
                    _errorState.value = apiError.getErrorMessage()
                    _loadingState.value = false
                }

                override fun onLoading(isLoading: Boolean) {
                    _loadingState.value = true
                }
            }
        )
    }
}