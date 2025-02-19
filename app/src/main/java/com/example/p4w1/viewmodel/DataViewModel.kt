package com.example.p4w1.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.p4w1.data.AppDatabase
import com.example.p4w1.data.DataEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.p4w1.data.Satuan
import com.example.p4w1.data.JenisPenyakit

class DataViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).dataDao()
    val dataList: LiveData<List<DataEntity>> = dao.getAll()

    private val _rowCount = MutableLiveData(0)
    val rowCount: LiveData<Int> = _rowCount

    fun insertData(
        kodeProvinsi: String,
        namaProvinsi: String,
        kodeKabupatenKota: String,
        namaKabupatenKota: String,
        jenisPenyakit: JenisPenyakit,
        total: String,
        satuan: Satuan,
        tahun: String
    ) {
        viewModelScope.launch {
            val totalValue = total.toIntOrNull() ?: 0
            val tahunValue = tahun.toIntOrNull() ?: 0
            dao.insert(
                DataEntity(
                    kodeProvinsi = kodeProvinsi,
                    namaProvinsi = namaProvinsi,
                    kodeKabupatenKota = kodeKabupatenKota,
                    namaKabupatenKota = namaKabupatenKota,
                    jenisPenyakit = jenisPenyakit,
                    total = totalValue,
                    satuan = satuan,
                    tahun = tahunValue
                )
            )
        }
    }

    fun updateData(data: DataEntity) {
        viewModelScope.launch {
            dao.update(data)
        }
    }

    suspend fun getDataById(id: Int): DataEntity? {
        return withContext(Dispatchers.IO) {
            dao.getById(id)
        }
    }

    fun deleteData(data: DataEntity) {
        viewModelScope.launch {
            dao.delete(data)
        }
    }

    fun fetchRowCount() {
        viewModelScope.launch {
            _rowCount.value = dao.getRowCount()
        }
    }
}
