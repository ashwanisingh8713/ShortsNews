package com.ns.shortsnews.user.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ns.shortsnews.R
import com.ns.shortsnews.adapters.ChannelsAdapter
import com.ns.shortsnews.adapters.ChannelsVideosAdapter
import com.ns.shortsnews.databinding.FragmentChannelVideosBinding
import com.ns.shortsnews.user.data.repository.UserDataRepositoryImpl
import com.ns.shortsnews.user.domain.usecase.channel.ChannelVideosDataUseCase
import com.ns.shortsnews.user.ui.viewmodel.ChannelsVideoDataViewModel
import com.ns.shortsnews.user.ui.viewmodel.ChannelsVideosViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get

class ChannelVideosFragment : Fragment(R.layout.fragment_channel_videos) {

    private lateinit var binding: FragmentChannelVideosBinding
    lateinit var adapter: ChannelsVideosAdapter

    private val channelsVideosViewModel: ChannelsVideoDataViewModel by viewModels { ChannelsVideosViewModelFactory().apply {
        inject(ChannelVideosDataUseCase(UserDataRepositoryImpl(get())))
    } }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentChannelVideosBinding.bind(view)
        channelsVideosViewModel.requestChannelVideosApi(arguments?.getString("channelId").toString())
        listenChannelVideos()
    }


    private fun listenChannelVideos() {
        viewLifecycleOwner.lifecycleScope.launch(){
            channelsVideosViewModel.ChannelsVideoSuccessState.collectLatest {
                adapter = ChannelsVideosAdapter(it)
                binding.channelImageRecyclerview.adapter = adapter
            }
        }

        viewLifecycleOwner.lifecycleScope.launch(){
            channelsVideosViewModel.errorState.collectLatest {

            }
        }
    }
}