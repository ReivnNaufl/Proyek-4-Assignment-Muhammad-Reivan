package com.example.p4w1.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.p4w1.data.AppDatabase
import com.example.p4w1.data.ProfileImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

class ImageViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).imageDao()

    private val _profileImage = MutableStateFlow<String?>(null)
    val profileImage: StateFlow<String?> = _profileImage

    fun updateProfileImage(context: Context, uri: Uri) {
        viewModelScope.launch {
            val imagePath = saveImageToInternalStorage(context, uri)
            if (imagePath != null) {
                val profileImage = ProfileImage(imgURI = imagePath)
                dao.insertOrUpdateImage(profileImage) // Save image separately in Room
                _profileImage.value = imagePath // Update UI instantly
            }
        }
    }

    fun getProfileImage() {
        viewModelScope.launch {
            val profileImage = dao.getProfileImage()
            _profileImage.value = profileImage?.imgURI
        }
    }

    private fun saveImageToInternalStorage(context: Context, uri: Uri): String? {
        val fileName = "profile_image.jpg"
        val file = File(context.filesDir, fileName)

        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                file.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            file.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun saveImage(context: Context, uri: Uri) {
        viewModelScope.launch {
            val imagePath = saveImageToInternalStorage(context, uri)
            if (imagePath != null) {
                dao.insertOrUpdateImage(ProfileImage(imgURI = imagePath))
                _profileImage.value = null
                _profileImage.value = imagePath
            }
        }
    }
}
