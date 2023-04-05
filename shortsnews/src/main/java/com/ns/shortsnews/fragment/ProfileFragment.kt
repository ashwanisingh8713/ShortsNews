package com.ns.shortsnews.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ns.shortsnews.R
import com.ns.shortsnews.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {
    lateinit var profileBinding: FragmentProfileBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        profileBinding = FragmentProfileBinding.inflate(layoutInflater,container, false)
        val root = profileBinding.root
        return root
    }

}