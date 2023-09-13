package com.videopager.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rommansabbir.networkx.NetworkXProvider
import com.videopager.R
import com.videopager.adapers.CommentAdapter
import com.videopager.data.CommentData
import com.videopager.data.PostCommentData
import com.videopager.databinding.FragmentCommentsBinding
import com.videopager.utils.NoConnection
import com.videopager.vm.SharedEventViewModelFactory
import com.videopager.vm.VideoSharedEventViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow


class CommentsFragment() : BottomSheetDialogFragment(R.layout.fragment_comments) {
    lateinit var binding: FragmentCommentsBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var commentAdapter: CommentAdapter
    private val clicks = MutableSharedFlow<Triple<String, String, Int>>(extraBufferCapacity = 1)
    private val sharedEventViewModel: VideoSharedEventViewModel by activityViewModels { SharedEventViewModelFactory }
    fun clicks() = clicks.asSharedFlow()
    private var itemPosition: Int = 0
    private var itemVideoId: String = ""
    private var commentList = mutableListOf<CommentData>()


    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCommentsBinding.bind(view)
        recyclerView = binding.commentRecyclerview

        binding.sendImage.setOnClickListener {
            val msg = binding.msgEditText.text.trim()
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

      binding.msgEditText.doAfterTextChanged {
          if (it != null) {
              if (it.isNotEmpty()){
                 binding.noCommentText.visibility = View.GONE
              } else {
                  binding.noCommentText.visibility = View.VISIBLE
              }
          }

        }

    }

    fun setRecyclerData(videoId: String, commentsList: List<CommentData>, position: Int) {
        // It emits value in VideoPagerFragment, when CommentFragment receives successful comments data
        sharedEventViewModel.sendSliderState(BottomSheetBehavior.STATE_EXPANDED)

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
        commentAdapter = CommentAdapter(commentsList = commentsList as MutableList<CommentData>)
        recyclerView.adapter = commentAdapter
    }

    @SuppressLint("NotifyDataSetChanged")
   suspend fun updateCommentAdapter(data: PostCommentData) {
        val commentData = CommentData(
            data.user_name,
            data.comment, data.created_at, data.user_image
        )
        commentList.add(0, commentData)
        if (commentList.isEmpty()) {
            binding.noCommentText.visibility = View.VISIBLE
        } else {
            binding.noCommentText.visibility = View.GONE
            binding.commentRecyclerview.visibility = View.VISIBLE
            setUpAdapter(commentList)
        }


    }

    private fun View.hideKeyBoard() {
        val inputManager =
            activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }

    override fun getTheme(): Int {
        return R.style.AppBottomSheetDialogTheme
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        // It emits value in VideoPagerFragment, when CommentFragment collapsed
        sharedEventViewModel.sendSliderState(BottomSheetBehavior.STATE_COLLAPSED)
        binding.msgEditText.text.clear()
    }



}