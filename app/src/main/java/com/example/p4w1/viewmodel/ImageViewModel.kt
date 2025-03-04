package com.example.p4w1.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import com.example.p4w1.data.AppDatabase
import com.example.p4w1.data.ProfileImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class ImageViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).imageDao()

    fun saveImageToStorage(context: Context, uri: Uri): String? {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val directory = File(context.getExternalFilesDir(null), "profile_images")
        if (!directory.exists()) directory.mkdirs()

        val file = File(directory, "profile.jpg") // Always replace the profile image
        val outputStream = FileOutputStream(file)

        inputStream?.copyTo(outputStream)

        inputStream?.close()
        outputStream.close()

        return file.absolutePath // Return the saved file path
    }

    fun updateProfileImage(context: Context, uri: Uri) {
        val profileImageDao = dao

        CoroutineScope(Dispatchers.IO).launch {
            val filePath = saveImageToStorage(context, uri)
            filePath?.let {
                val profileImage = ProfileImage(imgURI = it)
                profileImageDao.insertOrUpdateImage(profileImage)
            }
        }
    }

    fun getProfileImage(context: Context, callback: (ProfileImage?) -> Unit) {
        val profileImageDao = dao

        CoroutineScope(Dispatchers.IO).launch {
            val profileImage = profileImageDao.getProfileImage()
            callback(profileImage)
        }
    }
}