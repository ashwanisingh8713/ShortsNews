package com.ns.shortsnews.adapters

import android.annotation.SuppressLint
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.ns.shortsnews.R
import com.ns.shortsnews.databinding.ItemCategoryBinding
import com.ns.shortsnews.databinding.ItemFollowingBinding
import com.ns.shortsnews.onProfileItemClick
import com.ns.shortsnews.user.callbacks.onFollowingItemClick
import com.ns.shortsnews.user.domain.models.ChannelListData
import com.ns.shortsnews.user.domain.models.VideoCategory
import com.ns.shortsnews.user.ui.callbacks.onChannelItemClick
import com.videopager.ui.extensions.ClickEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class ChannelsAdapter(private var itemList: List<ChannelListData> = emptyList()):
    RecyclerView.Adapter<ChannelsAdapter.MyViewHolder>() {


    // Extra buffer capacity so that emissions can be sent outside a coroutine
    private val clicks = MutableSharedFlow<Pair<String, String>>(extraBufferCapacity = 1)
    fun clicks() = clicks.asSharedFlow()

    class MyViewHolder(val binding: ItemFollowingBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemFollowingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int  = itemList.size

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: MyViewHolder, @SuppressLint("RecyclerView") position: Int) {
        with(holder){
            with(itemList[position]){
               binding.clientIcon.load(this.channel_image)
                binding.channelName.text = this.channelTitle
                binding.root.setOnClickListener{
                    clicks.tryEmit(Pair(this.channel_id, this.channelTitle))
                }
            }
        }
        holder.itemView.setOnClickListener {
            val toast =   Toast.makeText(holder.itemView.context,"Coming in sprint 3rd",Toast.LENGTH_LONG)
            toast.setGravity(Gravity.BOTTOM, 0, 0)
            toast.show()
        }
    }


}