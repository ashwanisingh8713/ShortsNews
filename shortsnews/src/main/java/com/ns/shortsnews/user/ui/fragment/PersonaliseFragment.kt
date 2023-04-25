package com.ns.shortsnews.user.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.ns.shortsnews.R
import com.ns.shortsnews.databinding.FragmentPersonaliseBinding
import com.ns.shortsnews.databinding.FragmentUserBinding

class PersonaliseFragment : Fragment(R.layout.fragment_personalise) {
    lateinit var binding: FragmentPersonaliseBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPersonaliseBinding.bind(view)
        binding.backButtonUser.setOnClickListener {
            activity?.finish()
        }

    }
}