package com.ns.shortsnews.ui.paging

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.ns.shortsnews.data.model.VideoClikedItem
import com.ns.shortsnews.databinding.ItemGridViewBinding
import com.ns.shortsnews.domain.models.Data
import com.videopager.R
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow


class BookmarksAdapter(private var videoFrom: String, private var channelId: String) :
    PagingDataAdapter<Data, BookmarksAdapter.BookmarkItemViewHolder>(BookmarksComparator) {

    private val clicks = MutableSharedFlow<VideoClikedItem>(extraBufferCapacity = 1)
    fun clicks() = clicks.asSharedFlow()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BookmarkItemViewHolder {
        return BookmarkItemViewHolder(
            ItemGridViewBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }


    override fun onBindViewHolder(holder: BookmarkItemViewHolder, position: Int) {
        val item = getItem(position)
        item?.let { holder.bindPassenger(it) }
    }

    inner class BookmarkItemViewHolder(val binding:ItemGridViewBinding):RecyclerView.ViewHolder(binding.root) {
        fun bindPassenger(item: Data) = with(binding) {
            binding.imagePreview.load(item.preview)
            binding.likeCount.text = item.like_count
            if (item.liked) {
                binding.likeIcon.setColorFilter(ContextCompat.getColor(binding.likeIcon.context, R.color.red))
            } else {
                binding.likeIcon.setColorFilter(ContextCompat.getColor(binding.likeIcon.context, R.color.white))
            }
            itemView.setOnClickListener{
                clicks.tryEmit(VideoClikedItem(requiredId = channelId, selectedPosition = position, videoFrom = videoFrom))
            }
        }
    }


    fun updateLikeStatus(id: String, liked: Boolean, likeCount: String) {
        val data = Data(id=id)
        snapshot().items.indexOf(data).takeIf {
                it1-> it1>-1
        }?.let {it1->
            getItem(it1)?.let {
                it.liked = liked
                it.like_count = likeCount
                notifyItemRangeChanged(it1, itemCount-1)
            }
        }
    }

    fun updateBookmarkStatus(id: String, bookmark: Boolean) {
        val data = Data(id=id)
        snapshot().items.indexOf(data).takeIf {
                it1-> it1>-1
        }?.let {it1->
            getItem(it1)?.let {
                it.saved = bookmark
                notifyItemChanged(it1)
            }
        }
    }

    object BookmarksComparator : DiffUtil.ItemCallback<Data>() {
        override fun areItemsTheSame(oldItem: Data, newItem: Data): Boolean {
            return (oldItem.id == newItem.id) && (oldItem.saved == newItem.saved)
        }

        override fun areContentsTheSame(oldItem: Data, newItem: Data): Boolean {
            return oldItem.id == newItem.id
        }
    }



}