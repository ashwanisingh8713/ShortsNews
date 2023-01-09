package com.ns.news.presentation.activity.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import com.ns.news.databinding.FragmentArticleShortsBinding
import com.ns.news.presentation.activity.ui.shorts.adapter.ArticleShortsPagerAdapter
import com.ns.news.presentation.activity.ui.shorts.data.ArticleShortsData
import com.ns.news.utils.CommonFunctions

class ArticleShortsFragment : Fragment() {
    lateinit var binding: FragmentArticleShortsBinding
    val articleMutableList: MutableList<ArticleShortsData> = ArrayList()
    val shortDesCriptionList: MutableList<String> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentArticleShortsBinding.inflate(inflater, container, false)
        val root = binding.root
        setUpUi()
        binding.retryButton.setOnClickListener {
            setUpUi()
        }

        return root
    }
    fun setUpUi(){
        if (CommonFunctions.checkForInternet(requireActivity())) {
            binding.internetConnectionTv.visibility = View.GONE
            binding.retryButton.visibility = View.GONE
            binding.articleShortsViewpager.visibility  = View.VISIBLE
            setUpViewPager()
        } else {
            binding.internetConnectionTv.visibility = View.VISIBLE
            binding.retryButton.visibility = View.VISIBLE
            binding.articleShortsViewpager.visibility  = View.GONE
        }
    }

    fun createDemoData(): List<ArticleShortsData> {
        shortDesCriptionList.add(
            0,
            "जनसंख्या विस्फोट (Population Explosion) को लेकर भारतीय जनता पार्टी (BJP) के नेता और पूर्व केंद्रीय मंत्री मुख्तार अब्बास नकवी का बयान सामने आया है."
        )
        shortDesCriptionList.add(
            0,
            "पूर्व केंद्रीय मंत्री मुख्तार अब्बास नकवी (Mukhtar Abbas Naqvi) का कहना है कि किसी एक मजहब को जनसंख्या."
        )
        articleMutableList.add(
            ArticleShortsData(
                "https://media.istockphoto.com/id/1291177121/photo/low-angle-of-tall-building-in-manhattan.jpg?s=612x612&w=is&k=20&c=IiK5KvS-tENobs5KVV2hye6gxpumRqrVw7KTW9GM6Ic=",
                "Russia Ukraine War",
                "05:30",
                "Crypto Prices: दो साल के निचले स्तर पर बिटकॉइन, इथीरियम में भी बड़ी गिरावट दर्ज",
                shortDesCriptionList
            )
        )
        articleMutableList.add(
            ArticleShortsData(
                "https://media.istockphoto.com/id/1291177121/photo/low-angle-of-tall-building-in-manhattan.jpg?s=612x612&w=is&k=20&c=IiK5KvS-tENobs5KVV2hye6gxpumRqrVw7KTW9GM6Ic=",
                "Russia Ukraine War",
                "05:30",
                "Crypto Prices: दो साल के निचले स्तर पर बिटकॉइन, इथीरियम में भी बड़ी गिरावट दर्ज",
                shortDesCriptionList
            )
        )
        articleMutableList.add(
            ArticleShortsData(
                "https://media.istockphoto.com/id/1291177121/photo/low-angle-of-tall-building-in-manhattan.jpg?s=612x612&w=is&k=20&c=IiK5KvS-tENobs5KVV2hye6gxpumRqrVw7KTW9GM6Ic=",
                "Russia Ukraine War",
                "05:30",
                "Crypto Prices: दो साल के निचले स्तर पर बिटकॉइन, इथीरियम में भी बड़ी गिरावट दर्ज",
                shortDesCriptionList
            )
        )
        articleMutableList.add(
            ArticleShortsData(
                "https://media.istockphoto.com/id/1291177121/photo/low-angle-of-tall-building-in-manhattan.jpg?s=612x612&w=is&k=20&c=IiK5KvS-tENobs5KVV2hye6gxpumRqrVw7KTW9GM6Ic=",
                "Russia Ukraine War",
                "05:30",
                "Crypto Prices: दो साल के निचले स्तर पर बिटकॉइन, इथीरियम में भी बड़ी गिरावट दर्ज",
                shortDesCriptionList
            )
        )
        articleMutableList.add(
            ArticleShortsData(
                "https://media.istockphoto.com/id/1291177121/photo/low-angle-of-tall-building-in-manhattan.jpg?s=612x612&w=is&k=20&c=IiK5KvS-tENobs5KVV2hye6gxpumRqrVw7KTW9GM6Ic=",
                "Russia Ukraine War",
                "05:30",
                "Crypto Prices: दो साल के निचले स्तर पर बिटकॉइन, इथीरियम में भी बड़ी गिरावट दर्ज",
                shortDesCriptionList
            )
        )
        articleMutableList.add(
            ArticleShortsData(
                "https://d2jo35ozacw6sq.cloudfront.net/wp-content/uploads/2022/12/Blood-Pressure-Here-is-a-tip-to-treat-blood-pressure-problem-at-home-kannada-Health-Tips-akp-300x169.jpg",
                "Russia Ukraine War",
                "05:30",
                "Crypto Prices: दो साल के निचले स्तर पर बिटकॉइन, इथीरियम में भी बड़ी गिरावट दर्ज",
                shortDesCriptionList
            )
        )
        articleMutableList.add(
            ArticleShortsData(
                "https://media.istockphoto.com/id/1291177121/photo/low-angle-of-tall-building-in-manhattan.jpg?s=612x612&w=is&k=20&c=IiK5KvS-tENobs5KVV2hye6gxpumRqrVw7KTW9GM6Ic=",
                "Russia Ukraine War",
                "05:30",
                "Crypto Prices: दो साल के निचले स्तर पर बिटकॉइन, इथीरियम में भी बड़ी गिरावट दर्ज",
                shortDesCriptionList
            )
        )
        articleMutableList.add(
            ArticleShortsData(
                "https://media.istockphoto.com/id/1291177121/photo/low-angle-of-tall-building-in-manhattan.jpg?s=612x612&w=is&k=20&c=IiK5KvS-tENobs5KVV2hye6gxpumRqrVw7KTW9GM6Ic=",
                "Russia Ukraine War",
                "05:30",
                "Crypto Prices: दो साल के निचले स्तर पर बिटकॉइन, इथीरियम में भी बड़ी गिरावट दर्ज",
                shortDesCriptionList
            )
        )
        articleMutableList.add(
            ArticleShortsData(
                "https://media.istockphoto.com/id/1291177121/photo/low-angle-of-tall-building-in-manhattan.jpg?s=612x612&w=is&k=20&c=IiK5KvS-tENobs5KVV2hye6gxpumRqrVw7KTW9GM6Ic=",
                "Russia Ukraine War",
                "05:30",
                "Crypto Prices: दो साल के निचले स्तर पर बिटकॉइन, इथीरियम में भी बड़ी गिरावट दर्ज",
                shortDesCriptionList
            )
        )
        articleMutableList.add(
            ArticleShortsData(
                "https://d2jo35ozacw6sq.cloudfront.net/wp-content/uploads/2022/12/Dharwad-Soppu-Mela-3-300x169.jpg",
                "Russia Ukraine War",
                "05:30",
                "Crypto Prices: दो साल के निचले स्तर पर बिटकॉइन, इथीरियम में भी बड़ी गिरावट दर्ज",
                shortDesCriptionList
            )
        )
        return articleMutableList
    }

    fun setUpViewPager() {
        val articleShortsPagerAdapter = ArticleShortsPagerAdapter(fragment = this, createDemoData())
        val viewPager: ViewPager2 = binding.articleShortsViewpager
        viewPager.adapter = articleShortsPagerAdapter

    }

}