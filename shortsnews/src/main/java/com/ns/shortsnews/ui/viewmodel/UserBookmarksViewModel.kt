package com.ns.shortsnews.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ns.shortsnews.data.source.UserApiService
import com.ns.shortsnews.domain.exception.ApiError
import com.ns.shortsnews.domain.models.Data
import com.ns.shortsnews.domain.models.VideoDataResponse
import com.ns.shortsnews.domain.usecase.base.UseCaseResponse
import com.ns.shortsnews.domain.usecase.videodata.VideoDataUseCase
import com.ns.shortsnews.ui.paging.BookmarkPaging
import com.videopager.utils.CategoryConstants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import org.koin.core.Koin
import org.koin.java.KoinJavaComponent.getKoin


class UserBookmarksViewModel(private val channelsDataUseCase: VideoDataUseCase): ViewModel() {


    private val _loadingState = MutableStateFlow(true)
    val loadingState: MutableStateFlow<Boolean> get() = _loadingState

    /*val bookmark = Pager(PagingConfig(pageSize = 10, initialLoadSize = 2)) {
        BookmarkPaging(userApiService = getKoin().get<UserApiService>())
    }.flow.cachedIn(viewModelScope).map {
        Log.i("AshwaniXYZX", "PagerViewModel")
        it
    }*/

    val bookmarks = Pager(config = PagingConfig(
    pageSize = 5,
    enablePlaceholders = false,
//    initialLoadSize = 2 // It loads only 2 items in the first load request
    ),
    pagingSourceFactory = {
        BookmarkPaging(userApiService = getKoin().get<UserApiService>())
    }, initialKey = BookmarkPaging.INITIAL_PAGE
    ).flow.cachedIn(viewModelScope).map {
        Log.i("AshwaniXYZX", "PagerViewModel")
        it
    }

}