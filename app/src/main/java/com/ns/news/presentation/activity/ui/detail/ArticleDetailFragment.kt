package com.ns.news.presentation.activity.ui.detail

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ns.news.R
import com.ns.news.data.api.model.AWDataItem
import com.ns.news.data.db.Section
import com.ns.news.presentation.activity.ui.home.HomeArticleNdWidgetFragment

class ArticleDetailFragment : Fragment() {
    lateinit var item: AWDataItem
    lateinit var fragmentTitle: TextView

    companion object {

        private const val ARTICLE_DATA = "articleData"

        fun newInstance(data: AWDataItem): ArticleDetailFragment {
            return ArticleDetailFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARTICLE_DATA, data)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        item = if (Build.VERSION.SDK_INT >= 33) {
            arguments?.getParcelable(ARTICLE_DATA, AWDataItem::class.java)!!
        } else {
            arguments?.getParcelable(ARTICLE_DATA)!!
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_item_detail, container, false)
        fragmentTitle = view.findViewById(R.id.article_detail_fragment_text)
        fragmentTitle.text = item.title
        return view
    }
}