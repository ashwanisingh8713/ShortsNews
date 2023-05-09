package com.ns.shortsnews.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.ns.shortsnews.R
import com.ns.shortsnews.databinding.ItemCategoryBinding
import com.ns.shortsnews.databinding.ItemChannelGridLayoutBinding
import com.ns.shortsnews.databinding.ItemFollowingBinding
import com.ns.shortsnews.onProfileItemClick
import com.ns.shortsnews.user.domain.models.ChannelListData
import com.ns.shortsnews.user.domain.models.ChannelVideoData
import com.ns.shortsnews.user.domain.models.VideoCategory
import com.ns.shortsnews.user.ui.callbacks.onChannelItemClick
import com.videopager.ui.extensions.ClickEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class ChannelsVideosAdapter(private var itemList: List<ChannelVideoData> = emptyList()):
    RecyclerView.Adapter<ChannelsVideosAdapter.MyViewHolder>() {

    // Extra buffer capacity so that emissions can be sent outside a coroutine
    private val clicks = MutableSharedFlow<Pair<String, String>>(extraBufferCapacity = 1)
    fun clicks() = clicks.asSharedFlow()

    class MyViewHolder(val binding: ItemChannelGridLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemChannelGridLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int  = itemList.size

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: MyViewHolder, @SuppressLint("RecyclerView") position: Int) {
        with(holder){
            with(itemList[position]){
//               binding.clientIcon.load(this.channel_image)
//                binding.channelName.text = this.channelTitle
//                binding.root.setOnClickListener{
//                    clicks.tryEmit(Pair(this.channel_id, this.channelTitle))
//                }
            }
        }
    }


}