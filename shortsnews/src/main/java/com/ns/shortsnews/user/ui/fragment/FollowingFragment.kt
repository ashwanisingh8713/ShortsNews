package com.ns.shortsnews.user.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.ns.shortsnews.R
import com.ns.shortsnews.adapters.ChannelsAdapter
import com.ns.shortsnews.databinding.FragmentFollowingBinding
import com.ns.shortsnews.user.data.repository.UserDataRepositoryImpl
import com.ns.shortsnews.user.domain.usecase.channel.ChannelsDataUseCase
import com.ns.shortsnews.user.ui.viewmodel.ChannelsViewModel
import com.ns.shortsnews.user.ui.viewmodel.ChannelsViewModelFactory
import com.ns.shortsnews.user.ui.viewmodel.ProfileSharedViewModel
import com.ns.shortsnews.user.ui.viewmodel.ProfileSharedViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get

class FollowingFragment : Fragment(R.layout.fragment_following) {

    lateinit var binding: FragmentFollowingBinding
    lateinit var adapter:ChannelsAdapter

    private val sharedViewModel: ProfileSharedViewModel by activityViewModels { ProfileSharedViewModelFactory }

    private val channelsViewModel: ChannelsViewModel by activityViewModels { ChannelsViewModelFactory().apply {
        inject(ChannelsDataUseCase(UserDataRepositoryImpl(get())))
    } }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentFollowingBinding.bind(view)

//        binding.backButtonUser.setOn`Click`Listener {
//            activity?.finish()
//        }
        channelsViewModel.requestChannelListApi()


        viewLifecycleOwner.lifecycleScope.launch(){
            channelsViewModel.errorState.filterNotNull().collectLatest {
                binding.progressBarChannels.visibility = View.GONE
                if(!it.equals("NA")){
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
                var channelVideosFragment = ChannelVideosFragment().apply {
                    val bundle = Bundle()
                    bundle.putString("channelId", it.channel_id)
                    bundle.putString("channelTitle", it.channelTitle)
                    bundle.putString("channelUrl", it.channel_image)
                    arguments = bundle
                }
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_containerProfile, channelVideosFragment)
                    .addToBackStack(null).commit()
            }
        }
    }

}