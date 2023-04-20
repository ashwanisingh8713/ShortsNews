package com.ns.shortsnews

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.imageLoader
import com.exo.players.ExoAppPlayerFactory
import com.exo.ui.ExoAppPlayerViewFactory
import com.ns.shortsnews.adapters.CategoryAdapter
import com.ns.shortsnews.databinding.ActivityMainBinding
import com.ns.shortsnews.video.data.CategoryData
import com.ns.shortsnews.video.data.VideoDataRepository
import com.videopager.ui.VideoPagerFragment
import com.videopager.vm.SharedEventViewModel
import com.videopager.vm.SharedEventViewModelFactory
import com.videopager.vm.VideoPagerViewModelFactory


class MainActivity : AppCompatActivity(), onProfileItemClick{
    private lateinit var binding: ActivityMainBinding
    private lateinit var caAdapter: CategoryAdapter
    val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false)

    private val sharedEventViewModel: SharedEventViewModel by viewModels { SharedEventViewModelFactory }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        loadHomeFragment("")
       // Setup recyclerView
        binding.categoryItems.layoutManager = layoutManager
        caAdapter = CategoryAdapter(getCategoryData(), this)
        binding.categoryItems.adapter = caAdapter
        binding.profileIcon.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getCategoryData():List<CategoryData>{
        var categoryDataList = mutableListOf<CategoryData>()
        categoryDataList.add(CategoryData("All", true, "all"))
        categoryDataList.add(CategoryData("India", false, "india"))
        categoryDataList.add(CategoryData("Political", false, "political"))
        categoryDataList.add(CategoryData("Sports", false, "sports"))
        categoryDataList.add(CategoryData("Nature", false, "nature"))
        categoryDataList.add(CategoryData("Space", false, "space"))
        categoryDataList.add(CategoryData("Local", false, "local"))
        categoryDataList.add(CategoryData("Entertainment", false, "us"))
        categoryDataList.add(CategoryData("Media", false, "canada"))
        categoryDataList.add(CategoryData("Technology", false, "bihar"))
      return categoryDataList
    }

    override fun itemclick(shortsType: String, position:Int, size:Int) {
        loadHomeFragment(shortsType)
    }

    private fun topBarResponse() {
        sharedEventViewModel.requestApi("All")
    }

    private fun loadHomeFragment(shortsType: String) {
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_container, makeVideoPagerInstance(shortsType))
        ft.commit()
    }
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

}