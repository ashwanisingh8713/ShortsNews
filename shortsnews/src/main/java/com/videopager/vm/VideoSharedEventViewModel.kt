package com.videopager.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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




    private var _videoInfo= MutableSharedFlow<VideoInfoData?>()
    val videoInfo = _videoInfo.asSharedFlow()

    fun shareVideoInfo(videoInfoData: VideoInfoData?) {
        viewModelScope.launch {
            _videoInfo.emit(videoInfoData)
        }
    }

    private var _paletteColor= MutableSharedFlow<Int?>()
    val paletteColor = _paletteColor.asSharedFlow()

    fun sendPaletteColor(palletColor: Int?) {
        viewModelScope.launch {
            _paletteColor.emit(palletColor)
        }
    }

    private var _SliderState= MutableSharedFlow<Int>()
    val sliderState = _SliderState.asSharedFlow()

    fun sendSliderState(sliderState: Int) {
        viewModelScope.launch {
            _SliderState.emit(sliderState)
        }
    }



}