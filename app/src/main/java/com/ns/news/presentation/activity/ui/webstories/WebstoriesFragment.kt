package com.ns.news.presentation.activity.ui.webstories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.ns.news.R
import com.ns.news.databinding.FragmentWebstoriesBinding
import com.zhpan.indicator.IndicatorView

class WebstoriesFragment : Fragment() {
    lateinit var indicatorView: IndicatorView
    var imageUrlList: MutableList<ImageData> = ArrayList()
    lateinit var binding: FragmentWebstoriesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWebstoriesBinding.inflate(inflater, container, false)
        val root = binding.root
        indicatorView = IndicatorView(requireActivity())
        binding.viewpager.offscreenPageLimit = 2

        setUpViewPager()

        binding.previousButton.setOnClickListener {
          binding.viewpager.setCurrentItem(getItem(-1), true)
        }
        binding.nextButton.setOnClickListener {
            binding.viewpager.setCurrentItem(getItem(+1), true)
        }
        binding.indicatorView.apply {
            setSliderWidth(resources.getDimension(R.dimen.indicator_length))
            setSliderHeight(resources.getDimension(R.dimen.indicator_height))
            setupWithViewPager(binding.viewpager)
            setPageSize(binding.viewpager.adapter!!.itemCount)
            notifyDataChanged()
        }

        binding.viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                indicatorView.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                indicatorView.onPageSelected(position)
            }
        })
        return root

    }

    private fun getItem(i: Int): Int {
        return binding.viewpager.getCurrentItem() + i
    }

    fun setUpViewPager() {
        val articleShortsPagerAdapter = WebstoriesAdapter(fragment = this, setupViewPager2())
        val viewPager: ViewPager2 = binding.viewpager
        viewPager.adapter = articleShortsPagerAdapter
        viewPager.isUserInputEnabled  = false
    }

    private fun setupViewPager2(): List<ImageData> {
        imageUrlList.add(
            ImageData("https://www.shutterstock.com/image-photo/closeup-on-smartphone-display-screen-600w-1218889750.jpg","49 साल की मलाइका का ग्लैमर देख रह जाएंगे दंग" +
                "मलाइका का ग्लैमर देख रह जाएंगे दंग")
        )
        imageUrlList.add(ImageData("https://www.shutterstock.com/image-photo/word-url-uniform-resource-locator-600w-1767033755.jpg","बेटी संग रवीना को देख फैंस बोले ‘बहन लग रही"))
        imageUrlList.add(ImageData("https://www.shutterstock.com/image-photo/close-hand-using-smartphone-creative-600w-2152034199.jpg","आज की ताजा खबर Live: भारत जोड़ो यात्रा में शामिल नहीं होंगे जगदीश टाइटलर"))
        imageUrlList.add(ImageData("https://www.shutterstock.com/image-photo/barcelona-spain-june-02-2020-600w-1747019117.jpg","चीनी आक्रामकता भारत-अमेरिका संबंधों को मजबूत करने की एक और वजह- अमेरिकी सांसद"))
        imageUrlList.add(ImageData("https://www.shutterstock.com/image-photo/letters-abbreviation-url-www-on-600w-1919169371.jpg","चीनी आक्रामकता भारत-अमेरिका संबंधों को मजबूत करने की एक और वजह- अमेरिकी सांसद"))
        imageUrlList.add(ImageData("https://www.shutterstock.com/image-photo/https-600w-178066013.jpg", "कानपुर में फैक्ट्री में लगी भीषण आग, झुलसने से तीन की मौत; 5 की हालत गंभीर"))
        imageUrlList.add(ImageData("https://www.shutterstock.com/image-photo/people-hand-using-mobile-phone-600w-1728755095.jpg","पीछा किया, शराब की बोतलें फेंकी… दबंगों ने बीच सड़क पर लड़कियों से की छेड़खानी"))
        imageUrlList.add(ImageData("https://www.shutterstock.com/image-photo/seo-search-engine-optimization-marketing-600w-1006827298.jpg","ईरान पर बड़ी कार्रवाई, UNCSW से किया गया बाहर, US के प्रस्ताव पर हुई वोटिंग"))
        imageUrlList.add(ImageData("https://media.istockphoto.com/id/1305615921/photo/young-man-shopping-online.jpg?s=2048x2048&w=is&k=20&c=D2CVAH5K_RGFXlm9DkmFFMaf8YdhGc8ihEhZD29NUp4=", "18 दिसंबर 2022 की बड़ी खबरें: इंडियन नेवी में INS मोरमुगाओ शामिल, भारत ने"))
        imageUrlList.add(ImageData("https://media.istockphoto.com/id/1418822730/photo/young-asian-couple-managing-finance-for-their-business-together-at-home.jpg?s=2048x2048&w=is&k=20&c=_ZfLa64z-vVYlFH_0kTS5eURHYlSDYGBaWseaoj_TNM=","18 दिसंबर 2022 की बड़ी खबरें: इंडियन नेवी में INS मोरमुगाओ शामिल, भारत ने"))
        imageUrlList.add(ImageData("https://media.istockphoto.com/id/1299671484/photo/security-concepts-on-blackboard-background.jpg?s=1024x1024&w=is&k=20&c=VtGIxTiHJPBc66mQIG0YbZ5Y6CZ7uyVTPlZh8-XXo1g=","18 दिसंबर 2022 की बड़ी खबरें: इंडियन नेवी में INS मोरमुगाओ शामिल, भारत ने"))
        imageUrlList.add(ImageData("https://media.istockphoto.com/id/1349390515/photo/paperless-workplace-idea-e-signing-electronic-signature-document-management-businessman-signs.jpg?s=1024x1024&w=is&k=20&c=60tt0AgNgFsNDR28IT78VEw0yAsZQ-XsiUbj5zhwXFk=","18 दिसंबर 2022 की बड़ी खबरें: इंडियन नेवी में INS मोरमुगाओ शामिल, भारत ने"))
        imageUrlList.add(ImageData("https://www.shutterstock.com/image-photo/closeup-on-smartphone-display-screen-600w-1218889750.jpg","18 दिसंबर 2022 की बड़ी खबरें: इंडियन नेवी में INS मोरमुगाओ शामिल, भारत ने"))
        imageUrlList.add(ImageData("https://www.shutterstock.com/image-photo/word-url-uniform-resource-locator-600w-1767033755.jpg","18 दिसंबर 2022 की बड़ी खबरें: इंडियन नेवी में INS मोरमुगाओ शामिल, भारत ने"))
        imageUrlList.add(ImageData("https://www.shutterstock.com/image-photo/barcelona-spain-june-02-2020-600w-1747019117.jpg","18 दिसंबर 2022 की बड़ी खबरें: इंडियन नेवी में INS मोरमुगाओ शामिल, भारत ने"))
        imageUrlList.add(ImageData("https://www.shutterstock.com/image-photo/seo-search-engine-optimization-marketing-600w-1006827298.jpg","18 दिसंबर 2022 की बड़ी खबरें: इंडियन नेवी में INS मोरमुगाओ शामिल, भारत ने"))
        imageUrlList.add(ImageData("https://media.istockphoto.com/id/1349390515/photo/paperless-workplace-idea-e-signing-electronic-signature-document-management-businessman-signs.jpg?s=1024x1024&w=is&k=20&c=60tt0AgNgFsNDR28IT78VEw0yAsZQ-XsiUbj5zhwXFk=","18 दिसंबर 2022 की बड़ी खबरें: इंडियन नेवी में INS मोरमुगाओ शामिल, भारत ने"))
        imageUrlList.add(ImageData("https://www.shutterstock.com/image-photo/people-hand-using-mobile-phone-600w-1728755095.jpg","18 दिसंबर 2022 की बड़ी खबरें: इंडियन नेवी में INS मोरमुगाओ शामिल, भारत ने"))
        return imageUrlList
    }
}