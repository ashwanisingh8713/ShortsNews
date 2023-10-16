package com.ns.shortsnews.ui.paging

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ns.shortsnews.MainApplication
import com.ns.shortsnews.data.model.VideoClikedItem
import com.ns.shortsnews.databinding.ItemGridViewBinding
import com.player.models.VideoData
import com.ns.shortsnews.R
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow


class ChannelVideoAdapter(private var videoFrom: String, private var channelId: String) :
    PagingDataAdapter<VideoData, ChannelVideoAdapter.ChannelItemViewHolder>(ChannelVideoComparator) {

    var updatedChannelId: String = channelId
        get() = field
        set(value) { field = value }

    private val clicks = MutableSharedFlow<VideoClikedItem>(extraBufferCapacity = 1)
    fun clicks() = clicks.asSharedFlow()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChannelItemViewHolder {
        return ChannelItemViewHolder(
            ItemGridViewBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }


    override fun onBindViewHolder(holder: ChannelItemViewHolder, position: Int) {
        val item = getItem(position)
        item?.let { holder.bindPassenger(it) }
    }

    inner class ChannelItemViewHolder(val binding:ItemGridViewBinding):RecyclerView.ViewHolder(binding.root) {
        fun bindPassenger(item: VideoData) = with(binding) {
            Glide.with(MainApplication.instance!!).load(item.previewImageUri).into(binding.imagePreview)
            binding.likeCount.text = item.like_count
            if (item.liking) {
                binding.likeIcon.setColorFilter(ContextCompat.getColor(binding.likeIcon.context, R.color.red))
            } else {
                binding.likeIcon.setColorFilter(ContextCompat.getColor(binding.likeIcon.context, R.color.white))
            }
            itemView.setOnClickListener{
                clicks.tryEmit(VideoClikedItem(requiredId = updatedChannelId, selectedPosition = position, videoFrom = videoFrom, loadedVideoData =snapshot().items))
            }
        }
    }


    fun updateLikeStatus(id: String, liked: Boolean, likeCount: String) {
        val data = VideoData(id=id)
        snapshot().items.indexOf(data).takeIf {
                it1-> it1>-1
        }?.let {it1->
            getItem(it1)?.let {
                it.liking = liked
                it.like_count = likeCount
                notifyItemRangeChanged(it1, itemCount-1)
            }
        }
    }

    fun updateBookmarkStatus(id: String, bookmark: Boolean) {
        val data = VideoData(id=id)
        snapshot().items.indexOf(data).takeIf {
                it1-> it1>-1
        }?.let {it1->
            getItem(it1)?.let {
                it.saved = bookmark
                notifyItemChanged(it1)
            }
        }
    }

    object ChannelVideoComparator : DiffUtil.ItemCallback<VideoData>() {
        override fun areItemsTheSame(oldItem: VideoData, newItem: VideoData): Boolean {
            return (oldItem.id == newItem.id)// && (oldItem.saved == newItem.saved)
        }

        override fun areContentsTheSame(oldItem: VideoData, newItem: VideoData): Boolean {
//            return oldItem.id == newItem.id
            return oldItem == newItem
        }
    }



}