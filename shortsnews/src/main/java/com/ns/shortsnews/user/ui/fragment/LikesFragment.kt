package com.ns.shortsnews.user.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.ns.shortsnews.R
import com.ns.shortsnews.adapters.GridAdapter
import com.ns.shortsnews.databinding.FragmentLikesBinding
import com.ns.shortsnews.user.data.repository.UserDataRepositoryImpl
import com.ns.shortsnews.user.domain.usecase.bookmark.UserProfileLikesListUseCase
import com.ns.shortsnews.user.ui.viewmodel.LikesViewModelFactory
import com.ns.shortsnews.user.ui.viewmodel.UserLikesViewModel
import com.ns.shortsnews.utils.AppConstants
import com.videopager.ui.VideoPagerFragment
import com.videopager.utils.CategoryConstants
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get


class LikesFragment : Fragment(R.layout.fragment_likes) {
    lateinit var binding:FragmentLikesBinding
    lateinit var adapter:GridAdapter

    private val likesViewModel: UserLikesViewModel by activityViewModels { LikesViewModelFactory().apply {
        inject(UserProfileLikesListUseCase(UserDataRepositoryImpl(get())))
    }}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLikesBinding.bind(view)
        likesViewModel.requestLikesApi()
        adapter = GridAdapter(videoFrom = CategoryConstants.BOOKMARK_VIDEO_DATA)

        viewLifecycleOwner.lifecycleScope.launch {
            likesViewModel.errorState.filterNotNull().collectLatest {
                binding.progressBar.visibility = View.GONE
                if(it != "NA"){
                    Log.i("kamlesh","ProfileFragment onError ::: $it")
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            likesViewModel.LikesSuccessState.filterNotNull().collectLatest {
                Log.i("kamlesh","ProfileFragment onSuccess ::: $it")
                it.let {
                    binding.progressBar.visibility = View.GONE
                    adapter.updateVideoData(it.data)
                    binding.likesRecyclerview.adapter = adapter
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            adapter.clicks().collectLatest {
                val fragment = AppConstants.makeVideoPagerInstance("999", CategoryConstants.DEFAULT_VIDEO_DATA, requireActivity())
                val bundle = Bundle()
                bundle.putInt(CategoryConstants.KEY_SelectedPlay, it)
                fragment.arguments = bundle
                requireActivity().supportFragmentManager.beginTransaction().replace(R.id.fragment_containerProfile, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            likesViewModel.loadingState.filterNotNull().collectLatest {
                if (it) {
                    binding.progressBar.visibility = View.VISIBLE
                }
            }
        }




    }
}