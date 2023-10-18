package com.videopager.vm

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.savedstate.SavedStateRegistryOwner
import com.ns.shortsnews.data.source.UserApiService
import com.player.models.VideoData
import com.videopager.data.VideoDataRepository
import com.player.players.AppPlayer
import com.videopager.ui.extensions.ViewState

class VideoPagerViewModelFactory_2 (
    private val userApiService: UserApiService,
    private val repository: VideoDataRepository,
    private val appPlayerFactory: AppPlayer.Factory,
    private val requiredId: String,
    private val videoFrom: String,
    private val languages:String,
    private val selectedPlay:Int,
    private val loadedVideoData: List<VideoData>

) {

    /*override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val playerSavedStateHandle = PlayerSavedStateHandle(handle)
        return VideoPagerViewModel(
            repository = repository,
            appPlayerFactory = appPlayerFactory,
            handle = playerSavedStateHandle,
            initialState = ViewState(playerSavedStateHandle),
            categoryId = requiredId,
            videoFrom = videoFrom,
            languages = languages,
            selectedPlay = selectedPlay
        ) as T
    }*/
    fun create(owner: SavedStateRegistryOwner): ViewModelProvider.Factory {
        return object : AbstractSavedStateViewModelFactory(owner, null) {
            override fun <T : ViewModel> create(
                key: String,
                modelClass: Class<T>,
                handle: SavedStateHandle
            ): T {
                val playerSavedStateHandle = PlayerSavedStateHandle(handle)

                @Suppress("UNCHECKED_CAST")
                return VideoPagerViewModel(
                    userApiService = userApiService,
                    repository = repository,
                    appPlayerFactory = appPlayerFactory,
                    handle = playerSavedStateHandle,
                    initialState = ViewState(playerSavedStateHandle),
                    categoryId = requiredId,
                    videoFrom = videoFrom,
                    languages = languages,
                    selectedPlay = selectedPlay,
                    loadedVideoData = loadedVideoData
                ) as T
            }
        }
    }
}
