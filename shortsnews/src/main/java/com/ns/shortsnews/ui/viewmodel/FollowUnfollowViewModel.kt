package com.ns.shortsnews.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ns.shortsnews.domain.exception.ApiError
import com.ns.shortsnews.domain.usecase.base.UseCaseResponse
import com.ns.shortsnews.domain.usecase.followunfollow.FollowUnfollowUseCase
import com.videopager.data.Following
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FollowUnfollowViewModel(private val followUnfollowUseCse: FollowUnfollowUseCase): ViewModel() {

    // Profile
    private val _followUnfollowSuccessState = MutableStateFlow<Following?>(null)
    val FollowUnfollowSuccessState: StateFlow<Following?> get() = _followUnfollowSuccessState

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> get() = _errorState

    private val _loadingState = MutableStateFlow(true)
    val loadingState: MutableStateFlow<Boolean> get() = _loadingState


    fun requestFollowUnfollowApi(channelId:String) {
        followUnfollowUseCse.invoke(viewModelScope, channelId,
            object : UseCaseResponse<Following> {
                override fun onSuccess(type: Following) {
                    _followUnfollowSuccessState.value = type
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