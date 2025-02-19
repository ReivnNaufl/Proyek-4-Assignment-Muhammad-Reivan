package com.example.p4w1.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.p4w1.data.AppDatabase
import com.example.p4w1.data.DataProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).profileDao()
    val dataList: LiveData<List<DataProfile>> = dao.getAll()

    fun insertData(
        username: String,
        uid: String,
        email: String
    ) {
        viewModelScope.launch {
            dao.insert(
                DataProfile(
                    username = username,
                    uid = uid,
                    email = email
                )
            )
        }
    }

    fun updateData(data: DataProfile) {
        viewModelScope.launch {
            dao.update(data)
        }
    }

    suspend fun getDataById(id: Int): DataProfile? {
        return withContext(Dispatchers.IO) {
            dao.getById(id)
        }
    }

    fun deleteData(data: DataProfile) {
        viewModelScope.launch {
            dao.delete(data)
        }
    }
}