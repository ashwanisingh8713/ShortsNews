package com.ns.shortsnews.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.ns.shortsnews.databinding.ItemGridViewBinding
import com.ns.shortsnews.user.domain.models.LikesData
import com.videopager.R

class GridAdapter(private var itemList: List<LikesData> = emptyList()): RecyclerView.Adapter<GridAdapter.GridViewHolder>() {

    class GridViewHolder(val binding:ItemGridViewBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridViewHolder {
        val binding = ItemGridViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GridViewHolder(binding)
    }

    override fun getItemCount(): Int  = itemList.size

    override fun onBindViewHolder(holder: GridViewHolder, position: Int) {
        with(holder){
            with(itemList[position]){
                binding.imagePreview.load(this.videoPreviewUrl)
                binding.likeCount.text = this.like_count
                if (this.liked){
                    holder.binding.likeIcon.setColorFilter(ContextCompat.getColor(holder.binding.likeIcon.context, R.color.red))
                }
            }


            }
    }

}