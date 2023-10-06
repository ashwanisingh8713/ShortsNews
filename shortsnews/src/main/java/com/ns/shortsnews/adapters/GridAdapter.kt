package com.ns.shortsnews.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ns.shortsnews.MainApplication
import com.ns.shortsnews.R
import com.ns.shortsnews.databinding.ItemGridViewBinding
import com.ns.shortsnews.data.model.VideoClikedItem
import com.ns.shortsnews.domain.models.Data
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
                Glide.with(MainApplication.instance!!).load(this[position].preview).into(binding.imagePreview)
                binding.likeCount.text = this[position].like_count
                if (this[position].liked){
                    holder.binding.likeIcon.setColorFilter(ContextCompat.getColor(holder.binding.likeIcon.context, R.color.red))
                } else {
                    holder.binding.likeIcon.setColorFilter(ContextCompat.getColor(holder.binding.likeIcon.context, R.color.white))
                }
            }
            holder.itemView.setOnClickListener{
                clicks.tryEmit(VideoClikedItem(requiredId = channelId, selectedPosition = position, videoFrom = videoFrom, loadedVideoData = emptyList()))
            }
            }
    }

    fun updateVideoData(itemList: MutableList<Data>) {
        this.itemList = itemList
    }

    fun updateLikeStatus(id: String, liked: Boolean, likeCount: String) {
        val data = Data(id=id)
        this.itemList.indexOf(data).takeIf {
                it1-> it1>-1
        }?.let {it1->
            this.itemList.get(it1)?.let {
                it.liked = liked
                it.like_count = likeCount
                notifyItemRangeChanged(it1, itemCount-1)
            }
        }
    }

    fun clearChannelData() {
        this.itemList.clear()
        channelId = ""
        videoFrom = ""
        notifyDataSetChanged()
    }


}