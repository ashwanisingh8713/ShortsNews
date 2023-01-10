package com.ns.news.presentation.activity.ui.settings

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ns.news.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val data = ArrayList<SettingsModel>()
        data.add(SettingsModel("","Allow Notification",true))
        data.add(SettingsModel("","Customize Sections",false))
        data.add(SettingsModel("","Rate App",false))
        data.add(SettingsModel("","Share App",false))
        data.add(SettingsModel("","Privacy Policy",false))
        data.add(SettingsModel("","Terms of Service",false))
        data.add(SettingsModel("","1.11 Android",false))
        showSettingsList(data)
    }

    private fun showSettingsList(it: List<SettingsModel>) {
        binding.recyclerSettings.layoutManager = LinearLayoutManager(this)
        binding.recyclerSettings.adapter = SettingsAdapter(this, it)
        binding.recyclerSettings.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
    }
}