package com.ns.shortsnews.user.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.ns.shortsnews.R
import com.ns.shortsnews.databinding.FragmentNewProfileBinding
import com.ns.shortsnews.databinding.FragmentUserBinding
import com.ns.shortsnews.user.data.repository.UserDataRepositoryImpl
import com.ns.shortsnews.user.domain.usecase.user.UserProfileDataUseCase
import com.ns.shortsnews.user.ui.viewmodel.UserProfileViewModel
import com.ns.shortsnews.user.ui.viewmodel.UserProfileViewModelFactory
import org.koin.android.ext.android.get

class NewProfileFragment : Fragment(R.layout.fragment_new_profile) {

    lateinit var binding:FragmentNewProfileBinding
    private val userProfileViewModel: UserProfileViewModel by activityViewModels { UserProfileViewModelFactory().apply {
        inject(UserProfileDataUseCase(UserDataRepositoryImpl(get())))
    } }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentNewProfileBinding.bind(view)
    }
}