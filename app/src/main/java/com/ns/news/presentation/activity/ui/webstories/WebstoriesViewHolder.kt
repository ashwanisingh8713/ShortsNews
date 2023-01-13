package com.ns.news.presentation.activity.ui.webstories

import android.os.Build.VERSION
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import coil.load
import com.ns.news.databinding.WebstoriesPagerItemBinding

class WebstoriesViewHolder : Fragment() {
    private var imageData: ImageData? = null
     lateinit var binding: WebstoriesPagerItemBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if (VERSION.SDK_INT >= 33) {
                imageData = it.getParcelable(IMAGE)
            } else {
                imageData = it.getParcelable(IMAGE)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = WebstoriesPagerItemBinding.inflate(inflater, container, false)
        val root = binding.root
        setupView()
        return root
    }

    companion object {
        private const val IMAGE = "image"
        @JvmStatic
        fun newInstance(param1: ImageData) =
            WebstoriesViewHolder().apply {
                arguments = Bundle().apply {
                    putParcelable(IMAGE, param1)
                }
            }
    }
    fun setupView() {
        imageData.let {
            binding.imageView.load(it?.imageUrl)
            binding.title.text = it?.title
        }
    }
}