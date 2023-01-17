package com.ns.news.presentation.activity.ui.detail

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import coil.load
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.ns.news.data.api.model.AWDataItem
import com.ns.news.databinding.FragmentItemDetailBinding
import com.ns.news.presentation.activity.NewsSharedViewModel
import com.ns.news.presentation.activity.NewsSharedViewModelFactory
import com.ns.news.utils.CommonFunctions
import com.ns.news.utils.findParagraphIndex

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
        Log.d("Ashwani", "###############################################################")
        Log.d("Ashwani", "onCreate :: ${item.title}")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentItemDetailBinding.inflate(inflater, container, false)
         adRequest = AdRequest.Builder().build()
        Log.d("Ashwani", "onCreateView :: ${item.title}")

        loadBanner()
        loadTitleAuthor()
        loadDescriptionPart1()
        return binding.root
    }


    override fun onResume() {
        super.onResume()
        newsShareViewModel.sendArticle(item)
        Log.d("Ashwani", "onResume :: ${item.title}")
        loadDescriptionPart2()
        loadDescriptionPart3()
        loadDescriptionPart4()
        loadBannerAds1()
        loadBannerAds2()
    }

    override fun onPause() {
        super.onPause()
        Log.d("Ashwani", "onPause :: ${item.title}")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d("Ashwani", "onDetach :: ${item.title}")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("Ashwani", "onDestroyView :: ${item.title}")
    }

    var firstParagraphIndex = 0
    var secondParagraphIndex = 0
    var thirdParagraphIndex = 0
    var fourthParagraphIndex = 0

    private fun loadBanner() {
        if (item.media.isNotEmpty()) {
            binding.articleDetailBanner.load(item.media[0].images.large)
        }
    }

    private fun loadTitleAuthor() {
        var author:String = ""
        author = item.author[0].name+"\n"+item.modifiedGmt
        binding.articleEditor.text = author
        binding.appLogoDetail.load(item.author[0].avatarUrl)

        binding.articleDetailTitle.text = item.categoryName
        binding.articleSubTitle.text = item.title
    }

    private fun loadDescriptionPart1() {
        firstParagraphIndex = findParagraphIndex(item.content, firstParagraphIndex)
        if(firstParagraphIndex != -1)binding.webViewDes1.loadData(item.content.substring(0, firstParagraphIndex), "text/html; charset=utf-8", "UTF-8")
    }

    private fun loadDescriptionPart2() {
        secondParagraphIndex = findParagraphIndex(item.content, firstParagraphIndex+1)
        if(secondParagraphIndex != -1 && firstParagraphIndex != -1)binding.webViewDes2.loadData(item.content.substring(firstParagraphIndex, secondParagraphIndex), "text/html; charset=utf-8", "UTF-8")
    }

    private fun loadDescriptionPart3() {
        thirdParagraphIndex = findParagraphIndex(item.content, secondParagraphIndex+1)
        if(thirdParagraphIndex != -1 && secondParagraphIndex != -1)binding.webViewDes3.loadData(item.content.substring(secondParagraphIndex, thirdParagraphIndex), "text/html; charset=utf-8", "UTF-8")
    }

    private fun loadDescriptionPart4() {
        fourthParagraphIndex = findParagraphIndex(item.content, thirdParagraphIndex+1)
        if(fourthParagraphIndex != -1 && thirdParagraphIndex != -1)binding.webViewDes4.loadData(item.content.substring(thirdParagraphIndex, item.content.lastIndex  ), "text/html; charset=utf-8", "UTF-8")
    }

    private fun loadBannerAds1() {
        binding.adView1.loadAd(adRequest)
    }

    private fun loadBannerAds2() {
        binding.adView2.loadAd(adRequest)
    }

    private fun loadRelatedNews() {

    }

    private fun loadTopics() {

    }

    private fun loadNextStory() {
        // It should be load on ScrollListener end arrival
    }

    private fun loadRelatedArticleList() {
        // It should be load on ScrollListener end arrival
    }







}