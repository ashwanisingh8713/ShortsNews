package com.ns.shortsnews.adapters

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.ns.shortsnews.R
import com.ns.shortsnews.databinding.ItemCategoryBinding
import com.ns.shortsnews.onProfileItemClick
import com.ns.shortsnews.domain.models.VideoCategory


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
                if (position == lastCheckedPosition) {
                    val typeface: Typeface? = ResourcesCompat.getFont(binding.root.context, R.font.roboto_bold)
                    binding.categoryTitle.typeface = typeface
                    binding.indicatorImg.visibility = View.VISIBLE
                    binding.categoryTitle.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
                } else {
                    val typeface: Typeface? = ResourcesCompat.getFont(binding.root.context, R.font.roboto_light)
                    binding.categoryTitle.typeface = typeface
                    binding.indicatorImg.visibility = View.GONE
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