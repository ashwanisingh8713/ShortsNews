package com.ns.shortsnews

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.imageLoader
import com.exo.players.ExoAppPlayerFactory
import com.exo.ui.ExoAppPlayerViewFactory
import com.ns.shortsnews.adapters.CategoryAdapter
import com.ns.shortsnews.databinding.ActivityMainBinding
import com.ns.shortsnews.user.data.network.NetService
import com.ns.shortsnews.user.data.repository.VideoCategoryRepositoryImp
import com.ns.shortsnews.user.domain.usecase.video_category.VideoCategoryUseCase
import com.ns.shortsnews.user.ui.viewmodel.VideoCategoryViewModel
import com.ns.shortsnews.user.ui.viewmodel.VideoCategoryViewModelFactory
import com.ns.shortsnews.video.data.VideoDataRepository
import com.videopager.ui.VideoPagerFragment
import com.videopager.vm.SharedEventViewModel
import com.videopager.vm.SharedEventViewModelFactory
import com.videopager.vm.VideoPagerViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity(), onProfileItemClick{
    private lateinit var binding: ActivityMainBinding
    private lateinit var caAdapter: CategoryAdapter

    private val sharedEventViewModel: SharedEventViewModel by viewModels { SharedEventViewModelFactory }

    private val netService = NetService()
    private val videoRepository = VideoCategoryRepositoryImp(netService.createRetrofit())
    private val videoCategoryUseCase = VideoCategoryUseCase(videoRepository)

    private val userViewModelFactory = VideoCategoryViewModelFactory().apply {
        inject(
            videoCategoryUseCase
        )
    }

    private val videoCategoryViewModel: VideoCategoryViewModel by viewModels { userViewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.profileIcon.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        videoCategoryViewModel.loadVideoCategory()
        showCategory();
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
                    repository = VideoDataRepository(),
                    appPlayerFactory = ExoAppPlayerFactory(
                        context = this@MainActivity
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
                caAdapter = CategoryAdapter(itemList = it.videoCategories, itemListener = this@MainActivity)
                binding.recyclerView.adapter = caAdapter
                val defaultCate = it.videoCategories[0]
                loadHomeFragment(defaultCate.id)
            }
        }
    }


}