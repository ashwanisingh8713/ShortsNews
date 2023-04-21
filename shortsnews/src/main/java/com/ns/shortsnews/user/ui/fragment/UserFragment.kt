package com.ns.shortsnews.user.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.ns.shortsnews.R
import com.ns.shortsnews.databinding.FragmentLoginBinding
import com.ns.shortsnews.databinding.FragmentUserBinding

class UserFragment : Fragment(R.layout.fragment_user) {
    lateinit var binding:FragmentUserBinding
    lateinit var chipGroup: ChipGroup
    val choices = listOf("Sports","National","International", "Indian","Politics","Science")


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentUserBinding.bind(view)
       val  chipGroup = binding.choiceChipGroup
        for (title in choices){
            val chip = Chip(binding.choiceChipGroup.context)
            chip.text= title
            chip.isClickable = true
            chip.isCheckable = true
            chipGroup.addView(chip)
        }
    }

}