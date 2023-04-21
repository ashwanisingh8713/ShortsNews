package com.ns.shortsnews.user.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.ns.shortsnews.R
import com.ns.shortsnews.databinding.FragmentLoginBinding

class LoginFragment : Fragment(R.layout.fragment_login) {
    lateinit var binding: FragmentLoginBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLoginBinding.bind(view)



    }

}