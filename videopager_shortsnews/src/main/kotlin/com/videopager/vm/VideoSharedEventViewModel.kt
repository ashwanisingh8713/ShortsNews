package com.videopager.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.exoplayer2.MediaItem
import com.player.models.VideoData
import com.videopager.data.VideoInfoData
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class VideoSharedEventViewModel:ViewModel() {

    private var _cacheVideoUrl= MutableSharedFlow<Pair<String, String>>()
    val cacheVideoUrl = _cacheVideoUrl.asSharedFlow()

    fun cacheVideoData(uri: String, id: String) {
        viewModelScope.launch {
            _cacheVideoUrl.emit(Pair(uri, id))
        }
    }


    private var _userLoginStatus= MutableSharedFlow<Pair<Boolean, String>>()
    val cacheUserStatus = _userLoginStatus.asSharedFlow()

    fun sendUserPreferenceData(loginStatus: Boolean, userToken: String = "") {
        viewModelScope.launch {
            delay(2000)
            _userLoginStatus.emit(Pair(loginStatus, userToken))
        }
    }


    private var _launchLoginEvent= MutableSharedFlow<Boolean>()
    val getLoginEventStatus = _launchLoginEvent.asSharedFlow()

    fun launchLoginEvent(launchLogin: Boolean) {
        viewModelScope.launch {
            _launchLoginEvent.emit(launchLogin)
        }
    }

    private var _cacheVideoUrl_2= MutableSharedFlow<Pair<String, String>>()
    val cacheVideoUrl_2 = _cacheVideoUrl_2.asSharedFlow()

    fun cacheVideoData_2(uri: String, id: String) {
        viewModelScope.launch {
            _cacheVideoUrl_2.emit(Pair(uri, id))
        }
    }


    private var _videoInfo= MutableSharedFlow<VideoInfoData?>()
    val videoInfo = _videoInfo.asSharedFlow()

    fun shareVideoInfo(videoInfoData: VideoInfoData?) {
        viewModelScope.launch {
            _videoInfo.emit(videoInfoData)
        }
    }

    private var _followRequest= MutableSharedFlow<String?>()
    val followRequest = _followRequest.asSharedFlow()

    fun followRequest(channelId: String?) {
        viewModelScope.launch {
            _followRequest.emit(channelId)
        }
    }

    private var _followResponse= MutableSharedFlow<VideoData>()
    val followResponse = _followResponse.asSharedFlow()

    fun followResponse(videoData: VideoData) {
        viewModelScope.launch {
            _followResponse.emit(videoData)
        }
    }

}