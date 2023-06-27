package com.ns.shortsnews.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.ns.shortsnews.R
import com.ns.shortsnews.adapters.ChannelsAdapter
import com.ns.shortsnews.databinding.FragmentFollowingBinding
import com.ns.shortsnews.data.repository.UserDataRepositoryImpl
import com.ns.shortsnews.domain.usecase.channel.ChannelsDataUseCase
import com.ns.shortsnews.ui.viewmodel.ChannelsViewModel
import com.ns.shortsnews.ui.viewmodel.ChannelsViewModelFactory
import com.ns.shortsnews.ui.viewmodel.ProfileSharedViewModel
import com.ns.shortsnews.ui.viewmodel.ProfileSharedViewModelFactory
import com.ns.shortsnews.utils.AppPreference
import com.rommansabbir.networkx.NetworkXProvider
import com.videopager.utils.NoConnection
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get

class FollowingFragment : Fragment(R.layout.fragment_following) {

    lateinit var binding: FragmentFollowingBinding
    lateinit var adapter:ChannelsAdapter
    private val channelsViewModel: ChannelsViewModel by activityViewModels { ChannelsViewModelFactory().apply {
        inject(ChannelsDataUseCase(UserDataRepositoryImpl(get())))
    } }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentFollowingBinding.bind(view)
        if (NetworkXProvider.isInternetConnected) {
            channelsViewModel.requestChannelListApi()
        } else {
            // No Internet Snack bar: Fire
            NoConnection.noConnectionSnackBarInfinite(binding.root,
                requireContext() as AppCompatActivity
            )
        }
        viewLifecycleOwner.lifecycleScope.launch(){
            channelsViewModel.errorState.filterNotNull().collectLatest {
                binding.progressBarChannels.visibility = View.GONE
                if(it != "NA"){
                    Log.i("kamlesh","FollowingFragment onError ::: $it")
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch(){
            channelsViewModel.ChannelsSuccessState.filterNotNull().collectLatest {
                Log.i("kamlesh","FollowingFragment onSuccess ::: $it")
                it.let {
                    binding.progressBarChannels.visibility = View.GONE
                    adapter = ChannelsAdapter(it.data)
                    adapter.clicksEvent()
                    binding.recyclerviewFollowing.adapter = adapter
                    if (it.data.isEmpty()){
                      binding.followingText.visibility = View.VISIBLE
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch(){
            channelsViewModel.loadingState.filterNotNull().collectLatest {
                if (it) {
                    binding.progressBarChannels.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun ChannelsAdapter.clicksEvent() {
        viewLifecycleOwner.lifecycleScope.launch() {
            clicks().collectLatest {
                if (NetworkXProvider.isInternetConnected) {
                    var channelVideosFragment = ChannelVideosFragment().apply {
                        val bundle = Bundle()
                        bundle.putString("channelId", it.channel_id)
                        bundle.putString("channelTitle", it.channelTitle)
                        bundle.putString("channelUrl", it.channel_image)
                        arguments = bundle
                    }
                    AppPreference.isUpdateNeeded = true
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_containerProfile, channelVideosFragment)
                        .addToBackStack(null).commit()
                } else {
                    // No Internet Snack bar: Fire
                    NoConnection.noConnectionSnackBarInfinite(binding.root,
                        requireContext() as AppCompatActivity
                    )
                }
            }
        }
    }
}