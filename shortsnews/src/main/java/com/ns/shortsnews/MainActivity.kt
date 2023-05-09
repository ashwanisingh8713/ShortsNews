package com.ns.shortsnews

import android.content.Intent
import android.content.res.Resources.Theme
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import coil.imageLoader
import coil.load
import com.ns.shortsnews.cache.VideoPreloadWorker
import com.exo.players.ExoAppPlayerFactory
import com.exo.ui.ExoAppPlayerViewFactory
import com.ns.shortsnews.adapters.CategoryAdapter
import com.ns.shortsnews.databinding.ActivityMainBinding
import com.ns.shortsnews.user.data.repository.VideoCategoryRepositoryImp
import com.ns.shortsnews.user.domain.usecase.video_category.VideoCategoryUseCase
import com.ns.shortsnews.user.ui.viewmodel.VideoCategoryViewModel
import com.ns.shortsnews.user.ui.viewmodel.VideoCategoryViewModelFactory
import com.ns.shortsnews.utils.PrefUtils
import com.ns.shortsnews.utils.Validation
import com.ns.shortsnews.video.data.VideoDataRepositoryImpl
import com.player.models.VideoData
import com.videopager.ui.VideoPagerFragment
import com.videopager.vm.SharedEventViewModel
import com.videopager.vm.SharedEventViewModelFactory
import com.videopager.vm.VideoPagerViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get


class MainActivity : AppCompatActivity(), onProfileItemClick{
    private lateinit var binding: ActivityMainBinding
    private lateinit var caAdapter: CategoryAdapter

    private val sharedEventViewModel: SharedEventViewModel by viewModels { SharedEventViewModelFactory }

    private val videoRepository = VideoCategoryRepositoryImp(get())
    private val videoCategoryUseCase = VideoCategoryUseCase(videoRepository)

    private val userViewModelFactory = VideoCategoryViewModelFactory().apply {
        inject(
            videoCategoryUseCase
        )
    }

    private val videoCategoryViewModel: VideoCategoryViewModel by viewModels { userViewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        val view = binding.root
        setContentView(view)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.black)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        val isUserLoggedIn = PrefUtils.with(this@MainActivity).getBoolean(Validation.PREF_IS_USER_LOGGED_IN, false)
        if(isUserLoggedIn){
            val profileImage = PrefUtils.with(this@MainActivity).getString(Validation.PREF_USER_IMAGE, "")
            binding.profileIcon.load(profileImage)
        }

        binding.profileIcon.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        videoCategoryViewModel.loadVideoCategory()
        showCategory()
    }

    /**
     * It listens category item click
     */
    override fun itemclick(shortsType: String, position:Int, size:Int) {
        loadHomeFragment(shortsType)
    }


    /**
     * Loads Home Fragment
     */
    private fun loadHomeFragment(shortsType: String) {
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_container, makeVideoPagerInstance(shortsType))
        ft.commit()
    }

    /**
     * Creates VideoPagerFragment Instance
     */
    private fun makeVideoPagerInstance(shortsType: String): VideoPagerFragment {
        val vpf =  VideoPagerFragment(
            viewModelFactory = { owner ->
                VideoPagerViewModelFactory(
                    repository = VideoDataRepositoryImpl(),
                    appPlayerFactory = ExoAppPlayerFactory(
                        context = this@MainActivity, cache = MainApplication.cache
                    )
                ).create(owner)
            },
            appPlayerViewFactory = ExoAppPlayerViewFactory(),
            imageLoader = this@MainActivity.imageLoader,
            shortsType = shortsType
        )
        return vpf
    }

    private fun showCategory() {
        lifecycleScope.launch() {
                videoCategoryViewModel.videoCategorySuccessState.filterNotNull().collectLatest {
                    // Setup recyclerView
                    caAdapter = CategoryAdapter(
                        itemList = it.videoCategories,
                        itemListener = this@MainActivity
                    )
                    binding.recyclerView.adapter = caAdapter
                    val defaultCate = it.videoCategories[0]
                    loadHomeFragment(defaultCate.id)
                }

        }
    }






}