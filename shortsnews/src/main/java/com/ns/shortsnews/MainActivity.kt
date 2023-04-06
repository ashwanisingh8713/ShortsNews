package com.ns.shortsnews

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ns.shortsnews.adapters.CategoryAdapter
import com.ns.shortsnews.video.di.MainModule
import com.ns.shortsnews.databinding.ActivityMainBinding
import com.ns.shortsnews.video.data.CategoryData
import com.videopager.ui.VideoPagerFragment
import com.videopager.vm.SharedEventViewModel
import com.videopager.vm.SharedEventViewModelFactory


class MainActivity : AppCompatActivity(), onProfileItemClick{
    private lateinit var binding: ActivityMainBinding
    private lateinit var caAdapter: CategoryAdapter
    val module = MainModule(this)
    val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false)

    private val sharedEventViewModel: SharedEventViewModel by viewModels { SharedEventViewModelFactory }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        supportFragmentManager.fragmentFactory = module.fragmentFactory
        loadHomeFragment()
       // Setup recyclerView
        binding.categoryItems.layoutManager = layoutManager
        caAdapter = CategoryAdapter(getCategoryData(), this)
        binding.categoryItems.adapter = caAdapter
        binding.profileIcon.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadHomeFragment(){
        supportFragmentManager.commit {
            replace<VideoPagerFragment>(R.id.fragment_container)
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
        categoryDataList.add(CategoryData("US", false, "us"))
        categoryDataList.add(CategoryData("Canada", false, "canada"))
        categoryDataList.add(CategoryData("Bihar", false, "bihar"))
      return categoryDataList
    }

    override fun itemclick(query: String, position:Int, size:Int) {

        caAdapter.notifyItemRangeChanged(0, size)
        sharedEventViewModel.requestApi(query)
    }

}