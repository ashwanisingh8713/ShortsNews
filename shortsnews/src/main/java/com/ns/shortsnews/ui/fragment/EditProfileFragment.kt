package com.ns.shortsnews.ui.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import coil.load
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.ns.shortsnews.R
import com.ns.shortsnews.databinding.FragmentEditProfileBinding
import com.ns.shortsnews.data.repository.UserDataRepositoryImpl
import com.ns.shortsnews.database.ShortsDatabase
import com.ns.shortsnews.domain.models.ProfileData
import com.ns.shortsnews.domain.usecase.updateuser.DeleteProfileUseCase
import com.ns.shortsnews.domain.usecase.updateuser.UpdateUserUseCase
import com.ns.shortsnews.ui.activity.ContainerActivity
import com.ns.shortsnews.ui.activity.LanguageContainer
import com.ns.shortsnews.ui.viewmodel.UpdateProfileViewModel
import com.ns.shortsnews.ui.viewmodel.UpdateProfileViewModelFactory
import com.ns.shortsnews.utils.Alert
import com.ns.shortsnews.utils.AppConstants
import com.ns.shortsnews.utils.AppPreference
import com.rommansabbir.networkx.NetworkXProvider
import com.videopager.utils.NoConnection
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.koin.android.ext.android.get
import java.io.*

class EditProfileFragment : Fragment(R.layout.fragment_edit_profile) {
    lateinit var binding: FragmentEditProfileBinding
    private lateinit var mPhotoBitmap: Bitmap
    private var isProfileEdited = false
    private var isProfileImageSelected = false
    private var deleteProfileSelect:Boolean = false
    private val updateProfileViewModel: UpdateProfileViewModel by activityViewModels {
        UpdateProfileViewModelFactory().apply {
            inject(UpdateUserUseCase(UserDataRepositoryImpl(get())), DeleteProfileUseCase(UserDataRepositoryImpl(get())))
        }
    }

    companion object {
        const val cameraPermission = Manifest.permission.CAMERA
    }

    private val textWatcher = object : TextWatcher{
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
           Log.i("","")
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            isProfileEdited = true
        }

        override fun afterTextChanged(s: Editable?) {
            binding.consParent.forEach { view ->
                (view as? TextInputLayout)?.let {
                    with(it){
                        error = null
                        isErrorEnabled = false
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentEditProfileBinding.bind(view)
        val userData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable("profile", ProfileData::class.java)
        } else {
            arguments?.getParcelable("profile")
        }
        if (userData != null){
            binding.profileImageView.load(userData.image)
            binding.nameEditText.setText(userData.name)
            binding.ageEditText.setText(userData.age)
            binding.locationEditText.setText(userData.location)
        }
        addTextWatcherToEditTexts()
        binding.constLanguage.setOnClickListener {
            if (NetworkXProvider.isInternetConnected) {
                languagesFragment()
            } else {
                // No Internet Snackbar: Fire
                NoConnection.noConnectionSnackBarInfinite(binding.root,
                    requireContext() as AppCompatActivity
                )
            }
        }

        binding.backButton.setOnClickListener {
            it.hideKeyBoard()
            if (!isProfileEdited) {
                activity?.finish()
            } else {

                if (!isProfileImageSelected) {
                    showUpdateProfileDialog(
                            "Updating",
                    "Want to Updating user information",
                    getRequestBody(null, binding.nameEditText.text.toString()
                        ,binding.ageEditText.text.toString(),
                        binding.locationEditText.text.toString() )
                    )
                } else {
                    showUpdateProfileDialog(
                        "Updating",
                        "Want to Updating user information",
                        getRequestBody(bitmapToFile(mPhotoBitmap, "user") as File, binding.nameEditText.text.toString()
                            ,binding.ageEditText.text.toString(),
                            binding.locationEditText.text.toString() )
                    )
                }
            }
        }
        binding.constLogout.setOnClickListener {
            AppPreference.clear()
            AppPreference.isProfileDeleted = true
            lifecycleScope.launch {
                ShortsDatabase.instance!!.languageDao().deleteLanguageData()
                ShortsDatabase.instance!!.interestsDao().deleteInterestsData()
                activity?.finish()
            }
        }

        binding.profileImageEditIcon.setOnClickListener {
            openBottomDialog()
        }
        binding.constInterested.setOnClickListener{
            if (NetworkXProvider.isInternetConnected) {
                val intent = Intent(requireActivity(), ContainerActivity::class.java)
                intent.putExtra("to", "interests")
                requireActivity().startActivity(intent)
            } else {
                // No Internet Snackbar: Fire
                NoConnection.noConnectionSnackBarInfinite(binding.root,
                    requireContext() as AppCompatActivity
                )
            }
        }
        binding.constDelete.setOnClickListener {
            deleteProfileSelect = true
            updateProfileViewModel.requestDeleteProfile()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            updateProfileViewModel.errorState.filterNotNull().collectLatest {
                if (it != null) {
                    binding.progressBar.visibility = View.GONE
                    if (deleteProfileSelect){
                        Alert().showErrorDialog(
                            "Profile Delete",
                            "Error in deleting your profile",
                            requireActivity()
                        )
                        deleteProfileSelect = false
                    } else {
                        Alert().showErrorDialog(
                            "Profile Update",
                            "Error in updating you profile",
                            requireActivity()
                        )
                    }

                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            updateProfileViewModel.loadingState.filterNotNull().collectLatest {
                if (it) {
                    binding.progressBar.visibility = View.VISIBLE
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            updateProfileViewModel.UpdateProfileSuccessState.filterNotNull().collectLatest {
                if (it.status) {
                    AppPreference.isProfileUpdated = true
                    activity?.finish()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            updateProfileViewModel.DeleteProfileSuccessState.filterNotNull().collectLatest {
                if (it.status){
                    binding.progressBar.visibility = View.GONE
                    AppPreference.clear()
                    AppPreference.isProfileDeleted = true
                    ShortsDatabase.instance!!.languageDao().deleteLanguageData()
                    ShortsDatabase.instance!!.interestsDao().deleteInterestsData()
                    activity?.finish()
                }
            }
        }
    }

    private fun languagesFragment() {
        val intent  = Intent(requireActivity(), LanguageContainer::class.java)
        startActivity(intent)
    }


    private var launchLoginActivityResultCameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            isProfileImageSelected = true
            mPhotoBitmap = (result.data!!.extras!!["data"] as Bitmap?)!!

            Log.i(
                "pictureBitmap",
                "" + mPhotoBitmap
            )
            isProfileEdited = true
            binding.profileImageView.setImageBitmap(mPhotoBitmap)
        } else {
            Toast.makeText(requireContext(), "Image not taken", Toast.LENGTH_SHORT).show()
        }
    }


    //Camera permission
    private var cameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            if (result == true) {
                openCamera()
            } else {
                Alert().showGravityToast(
                    requireActivity(),
                    "Please accept camera permission to open take picture"
                )
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
                    isProfileImageSelected = true
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
        if (requireActivity().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            cameraPermissionLauncher.launch(cameraPermission)
        } else {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            launchLoginActivityResultCameraLauncher.launch(intent)
        }
    }

    @SuppressLint("InflateParams")
    private fun openBottomDialog() {
        val bottomSheetDialog =
            BottomSheetDialog(requireActivity(), com.videopager.R.style.AppBottomSheetDialogTheme)
        val bottomSheetView = LayoutInflater.from(requireActivity()).inflate(
            R.layout.bottom_sheet_layout, null
        )
        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()
        bottomSheetView.findViewById<View>(R.id.card_camera).setOnClickListener {
            bottomSheetDialog.dismiss()
            accessCameraPermission()
        }
        bottomSheetDialog.findViewById<View>(R.id.card_gellery)!!.setOnClickListener {
            bottomSheetDialog.dismiss()
            accessGalleryPermission()
        }
    }

    private fun showUpdateProfileDialog(title: String, msg: String, profileData: RequestBody) {
        val alertDialog = MaterialAlertDialogBuilder(requireContext())
        alertDialog.apply {
            this.setTitle(title)
            this.setMessage(msg)
            this.setPositiveButton("Save") { dialog, _ ->
                if (NetworkXProvider.isInternetConnected) {
                    updateProfileViewModel.requestUpdateProfileApi(profileData)
                } else {
                    // No Internet Snackbar: Fire
                    NoConnection.noConnectionSnackBarInfinite(binding.root,
                        requireContext() as AppCompatActivity
                    )
                }
            }
            this.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
                activity?.finish()
            }
            this.show()
        }

    }

    private fun bitmapToFile(bitmap: Bitmap, fileNameToSave: String): File? { // File name like "image.png"
        //create a file to write bitmap data
        var file: File? = null
        return try {
            file = File(requireActivity().cacheDir,fileNameToSave)
            file.createNewFile()

            //Convert bitmap to byte array
            val bos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos) // YOU can also save it in JPEG
            val bitmapdata = bos.toByteArray()

            //write the bytes in file
            val fos = FileOutputStream(file)
            fos.write(bitmapdata)
            fos.flush()
            fos.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            file // it will return null
        }
    }

    private fun getRequestBody(file: File?, name: String?, age: String?, location: String?): RequestBody {

        val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
        if (file != null) {
            builder.addFormDataPart(
                "photo",
                file.name,
                RequestBody.create(MultipartBody.FORM, File(file.absolutePath))
            )
        }
        if (name != null) {
            builder.addFormDataPart("name", name)
        }
        if (age != null) {
            builder.addFormDataPart("age", age)
        }
        if (location != null) {
            builder.addFormDataPart("location", location)
        }
        return builder.build()
    }

    private fun addTextWatcherToEditTexts(){
      binding.consParent.forEach {  view ->
          (view as? EditText)?.addTextChangedListener( textWatcher )
      }
    }

    private fun View.hideKeyBoard() {
        val inputManager =
            activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }
}