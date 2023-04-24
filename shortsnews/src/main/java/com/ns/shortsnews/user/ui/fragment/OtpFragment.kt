package com.ns.shortsnews.user.ui.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.ns.shortsnews.ProfileActivity
import com.ns.shortsnews.R
import com.ns.shortsnews.databinding.FragmentOtpBinding
import com.ns.shortsnews.databinding.FragmentUserBinding
import com.ns.shortsnews.user.ui.viewmodel.UserViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class OtpFragment : Fragment(R.layout.fragment_otp) {
    lateinit var binding: FragmentOtpBinding

    private val userViewModel: UserViewModel by activityViewModels { ProfileActivity.userViewModelFactory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentOtpBinding.bind(view)

        binding.loginButton.setOnClickListener {
            val otpValue = binding.otpTextInput.text.toString()
            if (otpValue.isNotEmpty() && otpValue.length == 6){
                val data:MutableMap<String, String> = mutableMapOf()
                data["OTP"] = otpValue
                data["OTP_id"] = ""
                userViewModel.requestOtpValidationApi(data)
            } else{
                if (otpValue.isEmpty()){
                    Toast.makeText(requireActivity(),"Please enter OTP",Toast.LENGTH_LONG).show()

                } else {
                    Toast.makeText(requireActivity(), "Please enter valid OTP", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }


        viewLifecycleOwner.lifecycleScope.launch(){
            userViewModel.errorState.collectLatest {
                if(!it.equals("NA")){
                    Toast.makeText(requireActivity(),"$it", Toast.LENGTH_LONG).show()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch(){
            userViewModel.registrationSuccessState.collectLatest {
                it.let {
                    binding.progressBarOtp.visibility = View.GONE
                }

            }
        }

        viewLifecycleOwner.lifecycleScope.launch(){
            userViewModel.loadingState.collectLatest {
                binding.progressBarOtp.visibility = View.VISIBLE
            }
        }



    }




}