package com.videopager.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.videopager.R
import com.videopager.adapers.CommentAdapter
import com.videopager.data.CommentData
import com.videopager.data.PostCommentData
import com.videopager.databinding.FragmentCommentsBinding
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow


class CommentsFragment : BottomSheetDialogFragment(R.layout.fragment_comments) {
    lateinit var binding: FragmentCommentsBinding
    private lateinit var recyclerView: RecyclerView
    lateinit var commentAdapter: CommentAdapter
    private val clicks = MutableSharedFlow<Triple<String, String, Int>>(extraBufferCapacity = 1)
    fun clicks() = clicks.asSharedFlow()
    private var itemPosition: Int = 0
    private var itemVideoId: String = ""
    private var commentList = mutableListOf<CommentData>()

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCommentsBinding.bind(view)
        recyclerView = binding.commentRecyclerview
//        commentAdapter = CommentAdapter(commentList)

        binding.sendImage.setOnClickListener {
            val msg = binding.msgEditText.text
            if (msg.toString().isNotEmpty()) {
                clicks.tryEmit(Triple(itemVideoId, msg.toString(), itemPosition))
                it.hideKeyBoard()
                binding.msgEditText.text.clear()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Please write some thing to post comment",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun setRecyclerData(videoId: String, commentsList: List<CommentData>, position: Int) {
        Log.i("", "$commentsList")
        itemPosition = position
        itemVideoId = videoId
        commentList = commentsList as MutableList<CommentData>
        if (commentsList.isEmpty()) {
            binding.noCommentText.visibility = View.VISIBLE
        } else {
            binding.noCommentText.visibility = View.GONE
            binding.commentRecyclerview.visibility = View.VISIBLE
            setUpAdapter(commentList)
        }
    }

    private fun setUpAdapter(commentsList: List<CommentData>) {
        commentAdapter = CommentAdapter(commentsList = commentsList)
        recyclerView.adapter = commentAdapter
    }

    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("NotifyDataSetChanged")
   suspend fun updateCommentAdapter(data: PostCommentData) {
        val commentData = CommentData(
            data.user_name,
            data.comment, data.created_at, data.user_image
        )
        commentList.add(0, commentData)
       withContext(Dispatchers.Main){
           setUpAdapter(commentList)
           commentAdapter.notifyDataSetChanged()
       }



    }

    private fun View.hideKeyBoard() {
        val inputManager =
            activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }

}