package com.ns.news.presentation.activity.ui.detail

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.ns.news.R
import com.ns.news.data.api.model.AWDataItem
import com.ns.news.data.db.Section
import com.ns.news.databinding.FragmentHomeBinding
import com.ns.news.databinding.FragmentItemDetailBinding
import com.ns.news.presentation.activity.NewsSharedViewModel
import com.ns.news.presentation.activity.NewsSharedViewModelFactory
import com.ns.news.presentation.activity.SharedChannelEvent
import com.ns.news.presentation.activity.ui.home.HomeArticleNdWidgetFragment

class ArticleDetailFragment : Fragment() {
    lateinit var item: AWDataItem
    lateinit var fragmentTitle: TextView
    private val newsShareViewModel: NewsSharedViewModel by activityViewModels { NewsSharedViewModelFactory }

    private var _binding: FragmentItemDetailBinding? = null
    private val binding get() = _binding!!

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentItemDetailBinding.inflate(inflater, container, false)
        binding.articleDetailFragmentText.text =  item.title
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        newsShareViewModel.sendArticle(item)
    }


    override fun onPause() {
        super.onPause()
    }


}