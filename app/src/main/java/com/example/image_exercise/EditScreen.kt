package com.example.image_exercise

import android.app.Activity.RESULT_OK
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.image_exercise.databinding.FragmentEditScreenBinding
import com.example.image_exercise.databinding.FragmentHomeScreenBinding
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class EditScreen : Fragment() {
    lateinit var currentPhotoPath: String
    lateinit var savePhoto: File

    lateinit var bmap: Bitmap
    lateinit var bmapDraw: BitmapDrawable
    lateinit var sCard: File
    lateinit var dir: File
    lateinit var opFile: File
    lateinit var fName: String
//    lateinit var outStream: FileOutputStream

    private lateinit var editScreenBinding: FragmentEditScreenBinding
    private val viewModel: myViewModel by activityViewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        editScreenBinding = FragmentEditScreenBinding.inflate(inflater, container, false)
        return editScreenBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setSaved(false)
        editScreenBinding.myImage.setImageURI(viewModel.getUri())

        editScreenBinding.undo.setOnClickListener {
            editScreenBinding.myImage.setImageURI(viewModel.getUri())
            //requireView().findNavController().navigate(R.id.action_editScreen_to_homeScreen)
        }

//        editScreenBinding.rotate.setOnClickListener {
//            editScreenBinding.myImage.rotation = (editScreenBinding.myImage.rotation + 90)
//
//        }

        editScreenBinding.crop.setOnClickListener {
            CropImage.activity(viewModel.getUri())
                .start(requireContext(),this)
        }

        editScreenBinding.save.setOnClickListener {
            editScreenBinding.myImage.buildDrawingCache()
            editScreenBinding.myImage.drawable?.let {
                bmap = (it as BitmapDrawable).bitmap
            }

            try {
                var fileopstream: OutputStream? = null
                //sCard = Environment.getExternalStorageDirectory()
                ////dir=File(sCard(Environment.DIRECTORY_PICTURES))
                fName = String.format("${System.currentTimeMillis()}.jpg")

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    requireContext()?.contentResolver.also { resolver ->
                        val contentValues = ContentValues().apply {
                            put(MediaStore.MediaColumns.DISPLAY_NAME, fName)
                            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                            put(
                                MediaStore.MediaColumns.RELATIVE_PATH,
                                Environment.DIRECTORY_PICTURES
                            )
                        }
                        val imageUri: Uri? = resolver.insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            contentValues
                        )
                        fileopstream = imageUri?.let { resolver.openOutputStream(it) }
                    }
                } else {
                    val imagesDir =
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    val image = File(imagesDir, fName)
                    fileopstream = FileOutputStream(image)
                }
                fileopstream?.use {
                    bmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                    //requireContext()?.toast("saved to photos")
                }

//                dir = File(sCard.absolutePath + "/myApp")
//                dir.mkdirs()
//                //Toast.makeText(requireContext(),fName,Toast.LENGTH_LONG).show()
//
//                opFile = File(dir, fName)
//                //fileopstream = FileOutputStream(opFile)
//                bmap.compress(Bitmap.CompressFormat.JPEG, 100, fileopstream)
//                //fileopstream.flush()
//                //fileopstream.close()
//
//                Toast.makeText(requireContext(),"onPictureTaken - wrote to " + opFile.getAbsolutePath().toString(),Toast.LENGTH_LONG).show()
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            if (viewModel.getSaved()==false){
                viewModel.saveImage(viewModel.getUri())
                viewModel.setSaved(true)
            }
            Toast.makeText(requireContext(),"Image Saved",Toast.LENGTH_SHORT).show()
            requireView().findNavController().navigateUp()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            var result:CropImage.ActivityResult=CropImage.getActivityResult(data)
            if(resultCode==RESULT_OK){
                var uri:Uri=result.uri
                viewModel.saveImage(uri)
                editScreenBinding.myImage.setImageURI(null)
                editScreenBinding.myImage.setImageURI(viewModel.getSavedImage())
                viewModel.setSaved(true)


            }
        }
    }

}