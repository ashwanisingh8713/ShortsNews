package com.ns.shortsnews

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.ns.shortsnews.cache.VideoPreloadCoroutine
import com.ns.shortsnews.databinding.ActivityPlainVideoBinding
import com.ns.shortsnews.data.model.VideoClikedItem
import com.ns.shortsnews.utils.AppConstants
import com.ns.shortsnews.utils.AppPreference
import com.videopager.ui.VideoPagerFragment_2
import com.videopager.utils.CategoryConstants
import com.videopager.vm.VideoSharedEventViewModel
import com.videopager.vm.SharedEventViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import java.util.Timer
import kotlin.concurrent.schedule


class PlainVideoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlainVideoBinding
    private val sharedEventViewModel: VideoSharedEventViewModel by viewModels { SharedEventViewModelFactory }
    private var videoPagerFragment: VideoPagerFragment_2? = null

    companion object {
        const val KEY_VIDEO_CLICKED_ITEM = "videoClickedItem"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i("intent_newLaunch", "newIntent PlainVideo onCreate before intent receive :: ")


        val videoClickedItem = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(KEY_VIDEO_CLICKED_ITEM, VideoClikedItem::class.java)!!
        } else {
            intent.getParcelableExtra<VideoClikedItem>(KEY_VIDEO_CLICKED_ITEM)!!
        }

        super.onCreate(savedInstanceState)

        binding = ActivityPlainVideoBinding.inflate(layoutInflater)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        val view = binding.root

        setContentView(view)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.black)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        loadVideoFragment(videoClickedItem)
        registerVideoCache()
        if (intent.getStringExtra(AppConstants.ID) != null) {
            Timer().schedule(1000) {
                CoroutineScope(Dispatchers.Main).launch {
                    notificationDataFromIntent(
                        intent.getStringExtra(AppConstants.ID).toString(),
                        intent.getStringExtra(AppConstants.VIDEO_PREVIEW_URL).toString(),
                        intent.getStringExtra(AppConstants.VIDEO_URL).toString()
                    )
                }
            }
        }
    }

    /**
     * Loads Home Fragment
     */
    private fun loadVideoFragment(videoItems: VideoClikedItem) {
        /*videoPagerFragment = AppConstants.makeVideoPagerInstance(
            requiredId = videoItems.requiredId,
            videoFrom = videoItems.videoFrom,
            this@PlainVideoActivity,
            languages = AppPreference.getSelectedLanguagesAsString(),
            selectedPlay = videoItems.selectedPosition
        )*/
        videoPagerFragment = VideoPagerFragment_2()
        val bundle = Bundle()
        bundle.putBoolean("logged_in", AppPreference.isUserLoggedIn)
        bundle.putString("directFrom", "PlainActivity")
        bundle.putParcelable("videoItems", videoItems)
        videoPagerFragment!!.arguments = bundle
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_container, videoPagerFragment!!)
        ft.commit()
    }


    // Register Hot-Flow to listen Video Caching
    private fun registerVideoCache() {
        lifecycleScope.launch {
            sharedEventViewModel.cacheVideoUrl.filterNotNull().collectLatest {
                val url = it.first
                val id = it.second
                var videoUrls = Array(1) { url }
                var videoIds = Array(1) { id }
                VideoPreloadCoroutine.schedulePreloadWork(videoUrls = videoUrls, ids = videoIds)
            }
        }

        lifecycleScope.launch {
            sharedEventViewModel.cacheVideoUrl_2.filterNotNull().collectLatest {
                val url = it.first
                val id = it.second
                var videoUrls = Array(1) { url }
                var videoIds = Array(1) { id }
                VideoPreloadCoroutine.schedulePreloadWork(videoUrls = videoUrls, ids = videoIds)
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

    @SuppressLint("MissingSuperCall")
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent?.extras != null) {
            val videoId = intent.getStringExtra(AppConstants.ID)!!
            val previewUrl = intent.getStringExtra(AppConstants.VIDEO_PREVIEW_URL)!!
            val videoUrl = intent.getStringExtra(AppConstants.VIDEO_URL)!!
            Log.i("intent_newLaunch", "$videoId$previewUrl$videoUrl")

            Timer().schedule(1000) {
                CoroutineScope(Dispatchers.Main).launch {
                    getNotificationIntentExtras(videoId, previewUrl, videoUrl)
                }
            }
        } else {
            Log.i("intent_newLaunch", "newIntent PlainVideo activity extras is null :: ")
        }
    }

    private fun getNotificationIntentExtras(videoId: String, previewUrl: String, videoUrl: String) {
        Log.i("intent_newLaunch", "in plaint video activity  getNotification${videoId}")
        notificationDataFromIntent(videoId, previewUrl, videoUrl)
    }

    private fun notificationDataFromIntent(videoId: String, previewUrl: String, mediaUri: String) {
        videoPagerFragment?.getNotificationData(videoId, previewUrl, mediaUri)
    }
}