package com.ns.articledetail.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ns.articledetail.model.ArticleDetailViewModel
import com.ns.news.R

class ItemDetailFragment(articleId:String, position:Int) : Fragment() {
     var articleIds: String
     var fragmentPosition: Int
    lateinit var fragmentTitle:TextView
    lateinit var articleDetailViewModel: ArticleDetailViewModel
    init {
        articleIds = articleId
        fragmentPosition = position
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        articleDetailViewModel = ViewModelProvider(requireActivity()).get(ArticleDetailViewModel::class.java)
        articleDetailViewModel.fragmentValueLiveData.observe(requireActivity()){ articleData ->
            setupFragment( articleData.articleString)
        }
    }

    private fun setupFragment(value:String) {
            fragmentTitle.text = value
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

       val view:View =  inflater.inflate(R.layout.fragment_item_detail, container, false)
        fragmentTitle  =view.findViewById(R.id.article_detail_fragment_text)
        return view
    }
}