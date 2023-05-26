package com.ns.shortsnews.user.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.ns.shortsnews.R
import com.ns.shortsnews.databinding.FragmentEditProfileBinding
import java.security.Permission

class EditProfileFragment : Fragment(R.layout.fragment_edit_profile) {
    lateinit var binding:FragmentEditProfileBinding
   companion object {
       private const val STORAGE_PERMISSOIN_EXTELNAL = 100
   }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentEditProfileBinding.bind(view)
        binding.backButton.setOnClickListener {
          activity?.finish()
        }

        binding.profileImageEditIcon.setOnClickListener {

        }

    }

    private fun requestPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            try {
               Log.i("kamlesh", "requesting permission try :: ")
                val intent  = Intent()
                intent.action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
                val uri = Uri.fromParts("package", activity?.packageName, null)
                intent.data = uri
            } catch (e:java.lang.Exception){
                Log.i("kamlesh", "requesting permission try :: $e ")
                val intent  = Intent()
                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
            }
        }
    }

//
//    fun showPictureDialog(){
//        val pictureDialog = AlertDialog.Builder(requireActivity())
//        pictureDialog.setTitle("Select Action")
//        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
//        pictureDialog.setItems(pictureDialogItems
//        ) { dialog, which ->
//            when (which) {
//                0 -> choosePhotoFromGallary()
//                1 -> takePhotoFromCamera()
//            }
//        }
//        pictureDialog.show()
//    }

}