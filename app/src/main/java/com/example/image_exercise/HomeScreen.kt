package com.example.image_exercise

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import com.example.image_exercise.myViewModel
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.SyncStateContract.Helpers.insert
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.core.content.FileProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.image_exercise.databinding.FragmentHomeScreenBinding
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class HomeScreen : Fragment() {

    private val viewModel: myViewModel by activityViewModels()
    private lateinit var homeScreenBinding: FragmentHomeScreenBinding
    lateinit var currentPhotoPath: String
    lateinit var photoFile: File

    lateinit var uri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        //requireActivity().onBackPressedDispatcher.addCallback(this){}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeScreenBinding = FragmentHomeScreenBinding.inflate(inflater, container, false)
        return homeScreenBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(viewModel.getSaved()){
            homeScreenBinding.myImage.setImageURI(viewModel.getSavedImage())
        }

        homeScreenBinding.capture.setOnClickListener {
            openActivityForResult()
        }
        homeScreenBinding.gallery.setOnClickListener {
            openGalleryforPicture()
        }
    }

    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {

                //val data: Intent? = result.data
                //Toast.makeText(requireContext(),viewModel.getUri().toString(),Toast.LENGTH_LONG).show()
                //var uri: Uri? = data?.getParcelableExtra<Uri>("imageUri")
                //homeScreenBinding.myImage.setImageURI(homeScreenViewModel.getUri())
                requireView().findNavController().navigate(R.id.action_homeScreen_to_editScreen)
            }
        }

    private fun openActivityForResult() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1)
        intent.putExtra("com.google.assistant.extra.USE_FRONT_CAMERA", true)
        intent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true)
        intent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1)
        intent.putExtra("android.intent.extras.CAMERA_FACING", 1)
        intent.putExtra("camerafacing", "front")
        intent.putExtra("previous_mode", "front")

        photoFile = createImageFile()
//        uri=FileProvider.getUriForFile(
//            requireContext(),
//            "com.example.retrofittest.fileprovider",
//            photoFile
//        )
        viewModel.setUri(FileProvider.getUriForFile(
            requireContext(),
            "com.example.retrofittest.fileprovider",
            photoFile
        ))
        intent.putExtra(MediaStore.EXTRA_OUTPUT, viewModel.getUri())
        resultLauncher.launch(intent)
    }

    private fun openGalleryforPicture(){
        val intent=Intent(Intent.ACTION_PICK)
        intent.setType("image/*")
        startActivityForResult(intent,1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==1 && resultCode== RESULT_OK){
            try{
                if (data != null) {
                    uri= data.data!!
                    viewModel.setUri(uri)
                    //homeScreenBinding.myImage.setImageURI(uri)
                    requireView().findNavController().navigate(R.id.action_homeScreen_to_editScreen)

                }
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

    private fun createImageFile(): File {

        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

}