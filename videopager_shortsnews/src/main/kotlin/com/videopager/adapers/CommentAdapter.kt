package com.videopager.adapers

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.videopager.data.CommentData
import com.videopager.databinding.ItemCommentBinding
import com.videopager.utils.CommentPostTime

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
                binding.profileIcon.load(this.user_image)
                binding.profileName.text = this.user_name
                binding.postTime.text = this.created_at
                binding.commentText.text = this.comment
            }
        }
    }
}