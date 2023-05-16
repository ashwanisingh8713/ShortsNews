package com.ns.shortsnews.user.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.core.content.ContextCompat
import com.google.android.material.chip.Chip
import com.ns.shortsnews.R
import com.ns.shortsnews.databinding.FragmentLanguageBinding
import com.ns.shortsnews.databinding.FragmentPersonaliseBinding
import com.ns.shortsnews.user.domain.models.VideoCategory

class LanguageFragment : Fragment(R.layout.fragment_language) {
    lateinit var binding:FragmentLanguageBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLanguageBinding.bind(view)
    }
    private fun setupChipGroup(optionList:MutableList<VideoCategory>){
        var idList = mutableListOf<Int>()
        for (chipData in optionList){
            idList.clear()
            val mChip =
                this.layoutInflater.inflate(R.layout.item_chip_view, null, false) as Chip
            mChip.text = chipData.name
            mChip.isCheckable = true
            mChip.isClickable = true
            binding.choiceChipGroup.addView(mChip)
            binding.choiceChipGroup.isClickable = true
            binding.choiceChipGroup.isSingleSelection = false
            mChip.isChipIconVisible = true
            mChip.setChipBackgroundColorResource(R.color.screen_background)
            mChip.chipIcon = ContextCompat.getDrawable(requireContext(), R.drawable.add)

            mChip.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener{
                override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                    if (isChecked){
                        mChip.isCheckedIconVisible = true
                        mChip.background = ContextCompat.getDrawable(requireContext(), R.drawable.roud_corner_background_chip)
                        mChip.checkedIcon = ContextCompat.getDrawable(requireContext(), R.drawable.right)
                        mChip.setChipBackgroundColorResource(R.color.button_background)
                        mChip.isChipIconVisible = false
//                        selectedNumbers++
//                        updateSelectedItems(selectedNumbers,countValue)

                    } else{
                        mChip.isChipIconVisible = true
                        mChip.background = ContextCompat.getDrawable(requireContext(), R.drawable.roud_corner_background)
                        mChip.setChipBackgroundColorResource(R.color.screen_background)
                        mChip.chipIcon = ContextCompat.getDrawable(requireContext(), R.drawable.add)
//                        selectedNumbers--
//                        updateSelectedItems(selectedNumbers,countValue)
                    }
                }
            })
        }
    }
}