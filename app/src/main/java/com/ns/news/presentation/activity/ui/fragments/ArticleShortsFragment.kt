package com.ns.news.presentation.activity.ui.fragments

import android.app.ActionBar
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import com.news.data.api.ConnectionManager
import com.news.data.api.interceptors.NetworkStatusInterceptor
import com.ns.news.R
import com.ns.news.databinding.FragmentArticleShortsBinding
import com.ns.news.presentation.activity.ui.shorts.adapter.ArticleShortsPagerAdapter
import com.ns.news.presentation.activity.ui.shorts.data.ArticleShortsData
import com.ns.news.utils.CommonFunctions

class ArticleShortsFragment : Fragment() {
     lateinit var binding: FragmentArticleShortsBinding
    val articleMutableList : MutableList<ArticleShortsData> = ArrayList()
    val shortDesCriptionList:MutableList<String> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentArticleShortsBinding.inflate(inflater,container, false)
        val root = binding.root
        if (CommonFunctions.checkForInternet(requireActivity())) {
            setUpViewPager()
        } else{
         Toast.makeText(activity,"Check Internet connection",Toast.LENGTH_LONG).show()
        }
        return root
    }

    fun createDemoData():List<ArticleShortsData>{
        shortDesCriptionList.add(0,"जनसंख्या विस्फोट (Population Explosion) को लेकर भारतीय जनता पार्टी (BJP) के नेता और पूर्व केंद्रीय मंत्री मुख्तार अब्बास नकवी का बयान सामने आया है.")
        shortDesCriptionList.add(0,"पूर्व केंद्रीय मंत्री मुख्तार अब्बास नकवी (Mukhtar Abbas Naqvi) का कहना है कि किसी एक मजहब को जनसंख्या.")
        articleMutableList.add(ArticleShortsData("https://d2jo35ozacw6sq.cloudfront.net/wp-content/uploads/2022/12/Dharwad-Soppu-Mela-3-300x169.jpg","Russia Ukraine War","05:30","Crypto Prices: दो साल के निचले स्तर पर बिटकॉइन, इथीरियम में भी बड़ी गिरावट दर्ज",shortDesCriptionList))
        articleMutableList.add(ArticleShortsData("https://d2jo35ozacw6sq.cloudfront.net/wp-content/uploads/2022/12/R-Ashoka-Revenue-Minister-300x169.jpg","Russia Ukraine War","05:30","Crypto Prices: दो साल के निचले स्तर पर बिटकॉइन, इथीरियम में भी बड़ी गिरावट दर्ज",shortDesCriptionList))
        articleMutableList.add(ArticleShortsData("https://d2jo35ozacw6sq.cloudfront.net/wp-content/uploads/2022/12/New-Project-74-2-300x169.jpg","Russia Ukraine War","05:30","Crypto Prices: दो साल के निचले स्तर पर बिटकॉइन, इथीरियम में भी बड़ी गिरावट दर्ज",shortDesCriptionList))
        articleMutableList.add(ArticleShortsData("https://d2jo35ozacw6sq.cloudfront.net/wp-content/uploads/2022/12/Nes-6-300x169.jpg","Russia Ukraine War","05:30","Crypto Prices: दो साल के निचले स्तर पर बिटकॉइन, इथीरियम में भी बड़ी गिरावट दर्ज",shortDesCriptionList))
        articleMutableList.add(ArticleShortsData("https://d2jo35ozacw6sq.cloudfront.net/wp-content/uploads/2022/12/Janardhana-Reddy-Bommai-300x169.jpg","Russia Ukraine War","05:30","Crypto Prices: दो साल के निचले स्तर पर बिटकॉइन, इथीरियम में भी बड़ी गिरावट दर्ज",shortDesCriptionList))
        articleMutableList.add(ArticleShortsData("https://d2jo35ozacw6sq.cloudfront.net/wp-content/uploads/2022/12/Blood-Pressure-Here-is-a-tip-to-treat-blood-pressure-problem-at-home-kannada-Health-Tips-akp-300x169.jpg","Russia Ukraine War","05:30","Crypto Prices: दो साल के निचले स्तर पर बिटकॉइन, इथीरियम में भी बड़ी गिरावट दर्ज",shortDesCriptionList))
        articleMutableList.add(ArticleShortsData("https://d2jo35ozacw6sq.cloudfront.net/wp-content/uploads/2022/12/New-Project-72-2-300x169.jpg","Russia Ukraine War","05:30","Crypto Prices: दो साल के निचले स्तर पर बिटकॉइन, इथीरियम में भी बड़ी गिरावट दर्ज",shortDesCriptionList))
        articleMutableList.add(ArticleShortsData("https://d2jo35ozacw6sq.cloudfront.net/wp-content/uploads/2022/12/Investment-300x169.jpg","Russia Ukraine War","05:30","Crypto Prices: दो साल के निचले स्तर पर बिटकॉइन, इथीरियम में भी बड़ी गिरावट दर्ज",shortDesCriptionList))
        articleMutableList.add(ArticleShortsData("https://d2jo35ozacw6sq.cloudfront.net/wp-content/uploads/2022/12/Dharwad-Soppu-Mela-3-300x169.jpg","Russia Ukraine War","05:30","Crypto Prices: दो साल के निचले स्तर पर बिटकॉइन, इथीरियम में भी बड़ी गिरावट दर्ज",shortDesCriptionList))
        articleMutableList.add(ArticleShortsData("https://d2jo35ozacw6sq.cloudfront.net/wp-content/uploads/2022/12/Dharwad-Soppu-Mela-3-300x169.jpg","Russia Ukraine War","05:30","Crypto Prices: दो साल के निचले स्तर पर बिटकॉइन, इथीरियम में भी बड़ी गिरावट दर्ज",shortDesCriptionList))
        return articleMutableList
    }

    fun setUpViewPager(){
        val articleShortsPagerAdapter = ArticleShortsPagerAdapter(fragment = this, createDemoData() )
        val viewPager:ViewPager2 = binding.articleShortsViewpager
        viewPager.adapter  = articleShortsPagerAdapter

    }

}