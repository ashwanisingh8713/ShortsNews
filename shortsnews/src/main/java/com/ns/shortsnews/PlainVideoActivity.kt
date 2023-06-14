package com.ns.shortsnews

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
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.savedstate.SavedStateRegistryOwner
import com.ns.shortsnews.cache.VideoPreloadCoroutine
import com.ns.shortsnews.databinding.ActivityPlainVideoBinding
import com.ns.shortsnews.data.model.VideoClikedItem
import com.ns.shortsnews.database.ShortsDatabase
import com.ns.shortsnews.domain.models.LanguageTable
import com.ns.shortsnews.domain.repository.LanguageRepository
import com.ns.shortsnews.ui.viewmodel.LanguageViewModel
import com.ns.shortsnews.ui.viewmodel.LanguageViewModelFactory
import com.ns.shortsnews.utils.AppConstants
import com.ns.shortsnews.utils.AppPreference
import com.videopager.utils.CategoryConstants
import com.videopager.vm.VideoSharedEventViewModel
import com.videopager.vm.SharedEventViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch


class PlainVideoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlainVideoBinding
    private val sharedEventViewModel: VideoSharedEventViewModel by viewModels { SharedEventViewModelFactory }
    private var languageListDB = emptyList<LanguageTable>()
    private val languageDao = ShortsDatabase.instance!!.languageDao()
    private val languageItemRepository = LanguageRepository(languageDao)
    private val languageViewModel: LanguageViewModel by viewModels { LanguageViewModelFactory(languageItemRepository) }

    companion object {
        const val KEY_VIDEO_CLICKED_ITEM = "videoClickedItem"
    }


    override fun onCreate(savedInstanceState: Bundle?) {

        val videoClickedItem  = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
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
        getSelectedLanguagesValues(videoClickedItem)
        registerVideoCache()
    }



    /**
     * Loads Home Fragment
     */
    private fun loadVideoFragment(it: VideoClikedItem, languages:String) {
        val fragment = AppConstants.makeVideoPagerInstance(requiredId = it.requiredId,
            videoFrom = it.videoFrom, this@PlainVideoActivity, languages)
        val bundle = Bundle()
        bundle.putInt(CategoryConstants.KEY_SelectedPlay, it.selectedPosition)
        bundle.putBoolean("logged_in", AppPreference.isUserLoggedIn)
        fragment.arguments = bundle
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_container, fragment)
        ft.commit()
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

    private fun getSelectedLanguagesValues(videoClikedItem: VideoClikedItem) {
        var languageString = ""
        lifecycleScope.launch {
            languageViewModel.getAllLanguage().filterNotNull().filter { it.isNotEmpty() }.collectLatest {
                for (data in it){
                    if (data.selected){
                        languageString = languageString + data.id +","
                    }
                }
                Log.i("language",languageString)
                loadVideoFragment(videoClikedItem, languageString)
            }
        }

    }


}