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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get


class LikesFragment : Fragment(R.layout.fragment_likes) {
    lateinit var binding:FragmentLikesBinding
    lateinit var adapter:GridAdapter

    private val likesViewModel: UserLikesViewModel by activityViewModels { LikesViewModelFactory().apply {
        inject(UserProfileLikesListUseCase(UserDataRepositoryImpl(get())))
    }  }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLikesBinding.bind(view)
        likesViewModel.requestLikesApi()

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
                    adapter = GridAdapter(it.data)
                    binding.likesRecyclerview.adapter = adapter

                }
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