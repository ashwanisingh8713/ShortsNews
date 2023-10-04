package com.ns.shortsnews.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.ns.shortsnews.data.source.UserApiService
import com.ns.shortsnews.domain.exception.ApiError
import com.ns.shortsnews.domain.models.VideoDataResponse
import com.ns.shortsnews.domain.usecase.base.UseCaseResponse
import com.ns.shortsnews.domain.usecase.videodata.VideoDataUseCase
import com.ns.shortsnews.ui.paging.ChannelVideoPaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import org.koin.java.KoinJavaComponent

class ChannelVideoViewModel(private val channelId: String): ViewModel() {



    val channelVideoData = Pager(PagingConfig(pageSize = 10)) {
        ChannelVideoPaging(channelId = channelId, userApiService = KoinJavaComponent.getKoin().get<UserApiService>())
    }.flow.cachedIn(viewModelScope)






}