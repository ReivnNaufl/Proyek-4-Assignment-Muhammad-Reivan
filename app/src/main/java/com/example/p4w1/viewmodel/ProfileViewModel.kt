package com.example.p4w1.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.p4w1.data.AppDatabase
import com.example.p4w1.data.DataProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).profileDao()
    val dataList: LiveData<List<DataProfile>> = dao.getAll()

    var profile: DataProfile? = null

    private val _profileStateFlow = MutableStateFlow<DataProfile?>(null)
    val profileStateFlow: StateFlow<DataProfile?> = _profileStateFlow

    init {
        viewModelScope.launch {
            _profileStateFlow.value = dao.getProfile() ?: DataProfile()
        }
    }

    init {
        loadProfile()
    }


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
            _profileStateFlow.value = data // Update state to trigger recomposition
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

    private fun loadProfile() {
        viewModelScope.launch {
            val existingProfile = dao.getProfile()
            if (existingProfile == null) {
                // If no profile exists, insert default profile
                val defaultProfile = DataProfile()
                dao.insert(defaultProfile)
                _profileStateFlow.value = defaultProfile
            } else {
                _profileStateFlow.value = existingProfile
            }
        }
    }
}