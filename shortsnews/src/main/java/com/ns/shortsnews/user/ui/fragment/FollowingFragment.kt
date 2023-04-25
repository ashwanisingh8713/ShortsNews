package com.ns.shortsnews.user.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ns.shortsnews.R
import com.ns.shortsnews.databinding.FragmentFollowingBinding
import com.ns.shortsnews.databinding.FragmentPersonaliseBinding

class FollowingFragment : Fragment(R.layout.fragment_following) {

    lateinit var binding: FragmentFollowingBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentFollowingBinding.bind(view)
    }

    }