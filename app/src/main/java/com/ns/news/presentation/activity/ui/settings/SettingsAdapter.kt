package com.ns.news.presentation.activity.ui.settings

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ns.news.databinding.SettingsItemBinding

class SettingsAdapter(var context: Context, private val mList: List<SettingsModel>) :
    RecyclerView.Adapter<SettingsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsViewHolder {
        return SettingsViewHolder(
            SettingsItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: SettingsViewHolder, position: Int) {
        val item = mList[position]
        (holder).bind(item, context)

    }

    override fun getItemCount(): Int {
        return mList.size
    }
}
