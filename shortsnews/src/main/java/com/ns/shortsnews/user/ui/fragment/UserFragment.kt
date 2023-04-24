package com.ns.shortsnews.user.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.ns.shortsnews.R
import com.ns.shortsnews.databinding.FragmentUserBinding

class UserFragment : Fragment(R.layout.fragment_user) {
    lateinit var binding:FragmentUserBinding



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentUserBinding.bind(view)

    }

}