package com.ns.shortsnews.user.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import com.ns.shortsnews.R
import com.ns.shortsnews.databinding.FragmentUserBinding
import com.ns.shortsnews.user.ui.activity.ContainerActivity

class UserFragment : Fragment(R.layout.fragment_user) {
    lateinit var binding:FragmentUserBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentUserBinding.bind(view)
        binding.editConsLayout.setOnClickListener {
            Toast.makeText(requireContext(),"Coming soon", Toast.LENGTH_SHORT).show()
        }
        binding.perConLayout.setOnClickListener {
            launchContainerActivity(to = "per")
        }

        binding.followConLayout.setOnClickListener {
            launchContainerActivity(to = "fol")
        }
    }

    private fun launchContainerActivity(to:String){
        val intent = Intent(requireActivity(), ContainerActivity::class.java)
        intent.putExtra("to",to)
        startActivity(intent)
    }
}