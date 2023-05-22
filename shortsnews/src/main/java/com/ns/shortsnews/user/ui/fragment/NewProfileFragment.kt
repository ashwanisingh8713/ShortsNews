package com.ns.shortsnews.user.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import coil.load
import com.google.android.material.tabs.TabLayoutMediator
import com.ns.shortsnews.R
import com.ns.shortsnews.adapters.NewProfilePagerAdapter
import com.ns.shortsnews.databinding.FragmentNewProfileBinding
import com.ns.shortsnews.user.data.repository.UserDataRepositoryImpl
import com.ns.shortsnews.user.domain.usecase.user.UserProfileDataUseCase
import com.ns.shortsnews.user.ui.viewmodel.UserProfileViewModel
import com.ns.shortsnews.user.ui.viewmodel.UserProfileViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get

class NewProfileFragment : Fragment(R.layout.fragment_new_profile) {

    lateinit var binding:FragmentNewProfileBinding
    private val userProfileViewModel: UserProfileViewModel by activityViewModels { UserProfileViewModelFactory().apply {
        inject(UserProfileDataUseCase(UserDataRepositoryImpl(get())))
    } }
    private lateinit var adapter:NewProfilePagerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentNewProfileBinding.bind(view)
        userProfileViewModel.requestProfileApi()
        adapter = NewProfilePagerAdapter(requireActivity())
        adapter.addFragment(LikesFragment(), "Bookmark")
        adapter.addFragment(FollowingFragment(), "Following")
        binding.profileViewPager.adapter = adapter
        binding.profileViewPager.currentItem = 0
        TabLayoutMediator(binding.profileTabLayout, binding.profileViewPager) { tab, position ->
            tab.text = adapter.getTabTitle(position)
        }.attach()


        binding.backButton.setOnClickListener {
            activity?.finish()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            userProfileViewModel.errorState.filterNotNull().collectLatest {
                binding.progressBarProfile.visibility = View.GONE
                if(it != "NA"){
                    Log.i("kamlesh","ProfileFragment onError ::: $it")
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            userProfileViewModel.UserProfileSuccessState.filterNotNull().collectLatest {
                Log.i("kamlesh","ProfileFragment onSuccess ::: $it")
                it.let {
                    binding.progressBarProfile.visibility = View.GONE
                    binding.profileImageView.load(it.data.image)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            userProfileViewModel.loadingState.filterNotNull().collectLatest {
                if (it) {
                    binding.progressBarProfile.visibility = View.VISIBLE
                }
            }
        }
    }
}