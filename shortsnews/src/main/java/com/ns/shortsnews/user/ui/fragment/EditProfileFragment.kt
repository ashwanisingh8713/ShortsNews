package com.ns.shortsnews.user.ui.fragment

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ns.shortsnews.R
import com.ns.shortsnews.databinding.FragmentEditProfileBinding
import com.ns.shortsnews.user.domain.models.ProfileData
import com.ns.shortsnews.utils.Alert
import java.io.FileNotFoundException
import java.io.InputStream

class EditProfileFragment : Fragment(R.layout.fragment_edit_profile) {
    lateinit var binding:FragmentEditProfileBinding
    private lateinit var mPhotoBitmap:Bitmap
    private var isProfileEdited = false

    companion object {
        const val cameraPermission = Manifest.permission.CAMERA
   }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentEditProfileBinding.bind(view)
        binding.backButton.setOnClickListener {
            if (!isProfileEdited){
                activity?.finish()
            } else {
                val userData = mutableMapOf<String, String>()
                if (mPhotoBitmap != null){
                    userData.put("photo", mPhotoBitmap.toString())
                }


                showUpdateProfileDialog("Updating","Want to Updating user information", emptyMap())
            }
        }

        binding.profileImageEditIcon.setOnClickListener {
//            openBottomDialog()
        }



    }

    var launchLoginActivityResultCameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode === RESULT_OK) {
            mPhotoBitmap = (result.data!!.extras!!["data"] as Bitmap?)!!
            Log.i(
                "pictureBitmap",
                "" + result.data!!.extras!!["data"] as Bitmap?
            )
            isProfileEdited = true
            binding.profileImageView.setImageBitmap(mPhotoBitmap)
        } else {
            Toast.makeText(requireContext(), "Image not taken", Toast.LENGTH_SHORT).show()
        }
    }


    private var cameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            if (result === true) {
                openCamera()
            }
        }
    private var launcherGalleryResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result != null) {
            if (result.resultCode == RESULT_OK) {
                try {
                    assert(result.data != null)
                    val imageStream: InputStream? =
                        result.data!!.data?.let {
                            activity?.contentResolver?.openInputStream(
                                it
                            )
                        }
                    mPhotoBitmap = BitmapFactory.decodeStream(imageStream)
                    isProfileEdited = true
                    binding.profileImageView.setImageBitmap(mPhotoBitmap)
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                    Toast.makeText(
                        requireActivity(),
                        "Something went wrong",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        launchLoginActivityResultCameraLauncher.launch(intent)
    }
    private fun accessGalleryPermission() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        launcherGalleryResult.launch(intent)
    }
    private fun accessCameraPermission() {
        if (requireActivity().checkSelfPermission(Manifest.permission.CAMERA) !== PackageManager.PERMISSION_GRANTED) {
            cameraPermissionLauncher.launch(cameraPermission)
        } else {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            launchLoginActivityResultCameraLauncher.launch(intent)
        }
    }

    private fun openBottomDialog() {
        val bottomSheetDialog =
            BottomSheetDialog(requireActivity(), com.videopager.R.style.AppBottomSheetDialogTheme)
        val bottomSheetView = LayoutInflater.from(requireActivity()).inflate(
            R.layout.bottom_sheet_layout,null
        )
        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()
        bottomSheetView.findViewById<View>(R.id.card_camera).setOnClickListener { v: View? ->
            bottomSheetDialog.dismiss()
            accessCameraPermission()
        }
        bottomSheetDialog.findViewById<View>(R.id.card_gellery)!!.setOnClickListener { v: View? ->
            bottomSheetDialog.dismiss()
            accessGalleryPermission()
        }
    }

    private fun showUpdateProfileDialog(title:String, msg:String,profileData: Map<String, String>){
        val alertDialog = MaterialAlertDialogBuilder(requireContext())
        alertDialog.apply {
            this.setTitle(title)
            this.setMessage(msg)
            this.setPositiveButton("Save") { dialog, _ ->
                dialog.dismiss()
            }

            this.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            this.show()
        }

    }

}