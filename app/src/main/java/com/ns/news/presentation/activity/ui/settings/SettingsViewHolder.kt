package com.ns.news.presentation.activity.ui.settings

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.ns.news.databinding.SettingsItemBinding

class SettingsViewHolder(val binding: SettingsItemBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: SettingsModel, context: Context) {
        binding.textViewSettings.text = item.nameSettings
        if (item.switchVisibility)
            binding.switchSettings.visibility = View.VISIBLE
        else
            binding.switchSettings.visibility = View.GONE

//        itemView.setOnClickListener {
//            context.startActivity(
//                Intent(context, ProductListActivity::class.java)
//                    .putExtra(Constants.CATEGORY_ID, item._id)
//                    .putExtra(Constants.CATEGORY_NAME, item.name)
//                    .putExtra(Constants.CATEGORY_TEXT, item.description)
//            )
//        }
    }
}