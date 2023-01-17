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
import coil.load
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.ns.news.R
import com.ns.news.data.api.model.AWDataItem
import com.ns.news.data.db.Section
import com.ns.news.databinding.FragmentHomeBinding
import com.ns.news.databinding.FragmentItemDetailBinding
import com.ns.news.presentation.activity.NewsSharedViewModel
import com.ns.news.presentation.activity.NewsSharedViewModelFactory
import com.ns.news.presentation.activity.SharedChannelEvent
import com.ns.news.presentation.activity.ui.home.HomeArticleNdWidgetFragment
import com.ns.news.utils.CommonFunctions

class ArticleDetailFragment : Fragment() {
    lateinit var item: AWDataItem
    private val newsShareViewModel: NewsSharedViewModel by activityViewModels { NewsSharedViewModelFactory }
    lateinit var adRequest:AdRequest

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
        MobileAds.initialize(requireActivity())
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
         adRequest = AdRequest.Builder().build()
        setUpUI()
        return binding.root
    }

    private fun setUpUI() {
        var descriptionList:MutableList<String> = CommonFunctions.splitHtmlString(item.content)
        if (item.media.size >0) {
            binding.articleDetailBanner.load(item.media[0].images.large)
        }
        var author:String = ""
        author = item.author[0].name+"\n"+item.modifiedGmt
        binding.articleEditor.text = author
        binding.appLogoDetail.load(item.author[0].avatarUrl)
        binding.adView1.loadAd(adRequest)
        binding.adView2.loadAd(adRequest)

        binding.articleDetailTitle.text = item.categoryName
        binding.articleSubTitle.text = item.title
        if (descriptionList.size>0) {
            binding.webViewDes1.loadData(descriptionList[0], "text/html; charset=utf-8", "UTF-8")
            if (descriptionList.size > 1) {
                binding.webViewDes2.loadData(
                    descriptionList[1],
                    "text/html; charset=utf-8",
                    "UTF-8"
                )
            }
            if (descriptionList.size > 2) {
                binding.webViewDes3.loadData(
                    descriptionList[2],
                    "text/html; charset=utf-8",
                    "UTF-8"
                )
            }
            if (descriptionList.size > 3) {
                binding.webViewDes4.loadData(
                    descriptionList[3],
                    "text/html; charset=utf-8",
                    "UTF-8"
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        newsShareViewModel.sendArticle(item)
    }
}