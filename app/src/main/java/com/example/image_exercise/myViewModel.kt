package com.example.image_exercise

import android.net.Uri
import androidx.lifecycle.ViewModel
import java.io.File

class myViewModel:ViewModel() {
    private lateinit var myuri: Uri
    private var saved:Boolean=false
    private lateinit var savedUri:Uri
    fun saveImage(uri:Uri){
        savedUri=uri
    }
    fun getSavedImage():Uri{
        return savedUri
    }
    fun setSaved(op:Boolean){
        saved=op
    }
    fun getSaved():Boolean{
        return saved
    }
    fun setUri(inUri:Uri){
        myuri=inUri
    }
    fun getUri():Uri{
        return myuri
    }
}
