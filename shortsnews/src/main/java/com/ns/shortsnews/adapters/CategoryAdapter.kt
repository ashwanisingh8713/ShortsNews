package com.ns.shortsnews.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ns.shortsnews.R
import com.ns.shortsnews.databinding.ItemCategoryBinding
import com.ns.shortsnews.onProfileItemClick
import com.ns.shortsnews.user.domain.models.VideoCategory

class CategoryAdapter(private var itemList: List<VideoCategory> = emptyList(), private val itemListener:onProfileItemClick): RecyclerView.Adapter<CategoryAdapter.MyViewHolder>() {
    private var lastCheckedPosition = 0

    class MyViewHolder(val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int  = itemList.size

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: MyViewHolder, @SuppressLint("RecyclerView") position: Int) {
        with(holder){
            with(itemList[position]){
                binding.categoryTitle.text = this.name
                if (position == lastCheckedPosition){
                    binding.categoryTitle.setTextColor(ContextCompat.getColor(holder.itemView.context, com.videopager.R.color.red))
                } else {
                    binding.categoryTitle.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
                }
            }
        }
        holder.itemView.setOnClickListener {
            lastCheckedPosition = position
            notifyItemRangeChanged(0, itemCount)
            itemListener.itemclick(itemList[position].id, position, itemCount)
        }
    }


}