package com.ns.shortsnews.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ns.shortsnews.MainApplication
import com.ns.shortsnews.R
import com.ns.shortsnews.data.repository.UserDataRepositoryImpl
import com.ns.shortsnews.databinding.FragmentUserBinding
import com.ns.shortsnews.domain.usecase.user.UserProfileDataUseCase
import com.ns.shortsnews.ui.viewmodel.UserProfileViewModel
import com.ns.shortsnews.ui.viewmodel.UserProfileViewModelFactory
import com.ns.shortsnews.utils.Alert
import com.ns.shortsnews.utils.AppConstants
import com.ns.shortsnews.utils.AppPreference
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

        viewLifecycleOwner.lifecycleScope.launch(){
            userProfileViewModel.errorState.filterNotNull().collectLatest {
                binding.progressBarProfile.visibility = View.GONE
                if(it != "NA"){
                    Log.i("kamlesh","ProfileFragment onError ::: $it")
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch(){
            userProfileViewModel.UserProfileSuccessState.filterNotNull().collectLatest {
                Log.i("kamlesh","ProfileFragment onSuccess ::: $it")
                it.let {
                    binding.progressBarProfile.visibility = View.GONE
                    binding.nestedParentView.visibility = View.VISIBLE
                    Glide.with(MainApplication.instance!!).load(it.data.image).into(binding.profileImageView)
                    binding.userNameTxt.text = it.data.name
                    AppPreference.userProfilePic = it.data.image
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
            Alert().showGravityToast(requireActivity(), AppConstants.SPRINT_TWO)
        }
        binding.disConLayout.setOnClickListener {
            Alert().showGravityToast(requireActivity(), AppConstants.SPRINT_TWO)
        }
        binding.perConLayout.setOnClickListener {
            launchContainerActivity(to = "interests")
        }

        binding.followConLayout.setOnClickListener {
            launchContainerActivity(to = "fol")
        }
        binding.backButtonUser.setOnClickListener {
            activity?.finish()
        }
        binding.logoutConLayout.setOnClickListener {
           confirmDialog()
        }
    }

    private fun launchContainerActivity(to:String){
        val intent = Intent(requireActivity(), com.ns.shortsnews.ui.activity.ContainerActivity::class.java)
        intent.putExtra("to",to)
        startActivity(intent)
    }

    private fun confirmDialog() {
        val alertDialog = MaterialAlertDialogBuilder(requireActivity())
        alertDialog.apply {
            this.setTitle("Logout")
            this.setMessage("Are you sure you want to logout?")
            this.setPositiveButton("Logout") { dialog, _ ->
                AppPreference.clear()
                activity?.finish()
            }
            this.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            this.show()
        }
    }
}