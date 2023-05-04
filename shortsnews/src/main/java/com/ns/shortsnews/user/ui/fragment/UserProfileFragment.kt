package com.ns.shortsnews.user.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import coil.load
import com.ns.shortsnews.R
import com.ns.shortsnews.databinding.FragmentUserBinding
import com.ns.shortsnews.user.data.repository.UserDataRepositoryImpl
import com.ns.shortsnews.user.domain.usecase.user.UserProfileDataUseCase
import com.ns.shortsnews.user.ui.activity.ContainerActivity
import com.ns.shortsnews.user.ui.viewmodel.UserProfileViewModel
import com.ns.shortsnews.user.ui.viewmodel.UserProfileViewModelFactory
import com.ns.shortsnews.user.ui.viewmodel.UserViewModel
import com.ns.shortsnews.utils.PrefUtils
import com.ns.shortsnews.utils.Validation
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get

class UserProfileFragment : Fragment(R.layout.fragment_user) {
    lateinit var binding:FragmentUserBinding
    private val userProfileViewModel:UserProfileViewModel by activityViewModels { UserProfileViewModelFactory().apply {
        inject(UserProfileDataUseCase(UserDataRepositoryImpl(get())))
    } }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentUserBinding.bind(view)
        userProfileViewModel.requestProfileApi()
//        binding.userNameTxt.text = PrefUtils.with(requireContext()).getString(Validation.PREF_USERNAME,"")


        viewLifecycleOwner.lifecycleScope.launch(){
            userProfileViewModel.errorState.filterNotNull().collectLatest {
                binding.progressBarProfile.visibility = View.GONE
                if(!it.equals("NA")){
                    Log.i("kamlesh","ProfileFragment onError ::: $it")
                    Toast.makeText(requireActivity(),"$it", Toast.LENGTH_LONG).show()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch(){
            userProfileViewModel.UserProfileSuccessState.filterNotNull().collectLatest {
                Log.i("kamlesh","ProfileFragment onSuccess ::: $it")
                it.let {
                    binding.progressBarProfile.visibility = View.GONE
                    binding.nestedParentView.visibility = View.VISIBLE
                    binding.profileImageView.load(it.data.image)
                    binding.userNameTxt.text = it.data.name
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch(){
            userProfileViewModel.loadingState.filterNotNull().collectLatest {
                if (it) {
                    binding.progressBarProfile.visibility = View.VISIBLE
                }
            }
        }

        binding.editConsLayout.setOnClickListener {
            Toast.makeText(requireContext(),"Coming soon", Toast.LENGTH_SHORT).show()
        }
        binding.perConLayout.setOnClickListener {
            launchContainerActivity(to = "per")
        }

        binding.followConLayout.setOnClickListener {
            launchContainerActivity(to = "fol")
        }
        binding.backButtonUser.setOnClickListener {
            activity?.finish()
        }
        binding.logoutConLayout.setOnClickListener {
            PrefUtils.with(requireContext()).clear()
            activity?.finish()

        }
    }

    private fun launchContainerActivity(to:String){
        val intent = Intent(requireActivity(), ContainerActivity::class.java)
        intent.putExtra("to",to)
        startActivity(intent)
    }
}