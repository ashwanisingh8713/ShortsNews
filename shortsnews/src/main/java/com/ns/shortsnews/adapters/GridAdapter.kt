package com.ns.shortsnews.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.ns.shortsnews.databinding.ItemGridViewBinding
import com.ns.shortsnews.data.model.VideoClikedItem
import com.ns.shortsnews.domain.models.Data
import com.videopager.R
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class GridAdapter(private var itemList: MutableList<Data> = mutableListOf(),
                  private var videoFrom: String, private var channelId: String): RecyclerView.Adapter<GridAdapter.GridViewHolder>() {

    private val clicks = MutableSharedFlow<VideoClikedItem>(extraBufferCapacity = 1)
    fun clicks() = clicks.asSharedFlow()

    class GridViewHolder(val binding:ItemGridViewBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridViewHolder {
        val binding = ItemGridViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GridViewHolder(binding)
    }

    override fun getItemCount(): Int  = itemList.size

    override fun onBindViewHolder(holder: GridViewHolder, position: Int) {
        with(holder){
            with(itemList){
                binding.imagePreview.load(this[position].preview)
                binding.likeCount.text = this[position].like_count
                if (this[position].liked){
                    holder.binding.likeIcon.setColorFilter(ContextCompat.getColor(holder.binding.likeIcon.context, R.color.red))
                }
            }
            holder.itemView.setOnClickListener{
                clicks.tryEmit(VideoClikedItem(requiredId = channelId, selectedPosition = position, videoFrom = videoFrom))
            }
            }
    }

    fun updateVideoData(itemList: MutableList<Data>) {
        this.itemList = itemList
        notifyDataSetChanged()
    }

    fun clearChannelData() {
        this.itemList.clear()
        channelId = ""
        videoFrom = ""
        notifyDataSetChanged()
    }


}