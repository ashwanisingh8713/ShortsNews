package com.ns.shortsnews

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
import android.view.WindowManager
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.load
import coil.request.ImageRequest
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.ns.shortsnews.adapters.CategoryAdapter
import com.ns.shortsnews.adapters.ChannelsAdapter
import com.ns.shortsnews.adapters.GridAdapter
import com.ns.shortsnews.cache.VideoPreloadCoroutine
import com.ns.shortsnews.databinding.ActivityMainBinding
import com.ns.shortsnews.databinding.ActivityPlainVideoBinding
import com.ns.shortsnews.user.data.model.VideoItemClick
import com.ns.shortsnews.user.data.repository.UserDataRepositoryImpl
import com.ns.shortsnews.user.data.repository.VideoCategoryRepositoryImp
import com.ns.shortsnews.user.domain.usecase.videodata.VideoDataUseCase
import com.ns.shortsnews.user.domain.usecase.video_category.VideoCategoryUseCase
import com.ns.shortsnews.user.ui.fragment.ChannelVideosFragment
import com.ns.shortsnews.user.ui.viewmodel.BookmarksViewModelFactory
import com.ns.shortsnews.user.ui.viewmodel.UserBookmarksViewModel
import com.ns.shortsnews.user.ui.viewmodel.VideoCategoryViewModel
import com.ns.shortsnews.user.ui.viewmodel.VideoCategoryViewModelFactory
import com.ns.shortsnews.utils.AppConstants
import com.ns.shortsnews.utils.AppPreference
import com.videopager.utils.CategoryConstants
import com.videopager.vm.VideoSharedEventViewModel
import com.videopager.vm.SharedEventViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get


class PlainVideoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlainVideoBinding
    private val sharedEventViewModel: VideoSharedEventViewModel by viewModels { SharedEventViewModelFactory }


    override fun onCreate(savedInstanceState: Bundle?) {

        val videoItemClick  = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("videoItemClick", VideoItemClick::class.java)!!
        } else {
            intent.getParcelableExtra<VideoItemClick>("videoItemClick")!!
        }

        super.onCreate(savedInstanceState)
        statusBarFullScreen()
        binding = ActivityPlainVideoBinding.inflate(layoutInflater)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        val view = binding.root

        setContentView(view)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.black)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)


        loadHomeFragment(videoItemClick)

    }



    /**
     * Loads Home Fragment
     */
    private fun loadHomeFragment(it: VideoItemClick) {
        val fragment = AppConstants.makeVideoPagerInstance(requiredId = it.requiredId, videoFrom = it.videoFrom, this@PlainVideoActivity)
        val bundle = Bundle()
        bundle.putInt(CategoryConstants.KEY_SelectedPlay, it.selectedPosition)
        fragment.arguments = bundle
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_container, fragment)
        ft.commit()
        AppPreference.userToken?.let {
            sharedEventViewModel.sendUserPreferenceData(
                AppPreference.isUserLoggedIn, it
            )
        }
        registerVideoCache()
    }


    // Register Hot-Flow to listen Video Caching
    private fun registerVideoCache() {
        lifecycleScope.launch {
            sharedEventViewModel.cacheVideoUrl.filterNotNull().collectLatest {
                val url = it.first
                val id = it.second
                var videoUrls = Array(1){url}
                var videoIds = Array(1){id}
                VideoPreloadCoroutine.schedulePreloadWork(videoUrls=videoUrls, ids=videoIds)
            }
        }

        lifecycleScope.launch {
            sharedEventViewModel.cacheVideoUrl_2.filterNotNull().collectLatest {
                val url = it.first
                val id = it.second
                var videoUrls = Array(1){url}
                var videoIds = Array(1){id}
                VideoPreloadCoroutine.schedulePreloadWork(videoUrls=videoUrls, ids=videoIds)
            }
        }
    }

    // To set status bar in Full-Screen
    private fun statusBarFullScreen() {
        if (Build.VERSION.SDK_INT in 21..29) {
            window.statusBarColor = Color.TRANSPARENT
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.decorView.systemUiVisibility =
                SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or SYSTEM_UI_FLAG_LAYOUT_STABLE

        } else if (Build.VERSION.SDK_INT >= 30) {
            window.statusBarColor = Color.TRANSPARENT
            // Making status bar overlaps with the activity
            WindowCompat.setDecorFitsSystemWindows(window, false)
        }
    }


}