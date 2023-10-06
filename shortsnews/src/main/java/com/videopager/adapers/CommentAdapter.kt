package com.videopager.adapers

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ns.shortsnews.MainApplication
import com.videopager.data.CommentData
import com.ns.shortsnews.databinding.ItemCommentBinding

class CommentAdapter (private var commentsList: MutableList<CommentData>): RecyclerView.Adapter<CommentAdapter.MyViewHolder>(){
   inner class MyViewHolder(val binding: ItemCommentBinding): RecyclerView.ViewHolder(binding.root) {

    }

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
      val binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context))
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return commentsList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        with(holder){
            with(commentsList[position]){
                Glide.with(MainApplication.instance!!).load(this.user_image).into(binding.profileIcon)
                binding.profileName.text = this.user_name
                binding.postTime.text = this.created_at
                binding.commentText.text = this.comment
            }
        }
    }
}