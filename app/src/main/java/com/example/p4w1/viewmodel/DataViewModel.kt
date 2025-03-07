package com.example.p4w1.viewmodel

import DiseaseCase
import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.p4w1.data.AppDatabase
import com.example.p4w1.data.DataEntity
import com.example.p4w1.data.DataPagingSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.p4w1.data.Satuan
import com.example.p4w1.data.JenisPenyakit

class DataViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).dataDao()

    private val _rowCount = MutableLiveData(0)
    val rowCount: LiveData<Int> = _rowCount


    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    fun fetchDataAndInsert() {
        viewModelScope.launch {
            _isLoading.value = true  // Show loading
            try {
                val response = RetrofitClient.apiService.getDiseaseData()
                if (response.error == 0) {
                    val diseaseEntities = response.data.map { disease ->
                        DataEntity(
                            kodeProvinsi = disease.kode_provinsi.toString(),
                            namaProvinsi = disease.nama_provinsi,
                            kodeKabupatenKota = disease.kode_kabupaten_kota.toString(),
                            namaKabupatenKota = disease.nama_kabupaten_kota,
                            jenisPenyakit = mapToJenisPenyakit(disease.jenis_penyakit ?: ""),
                            total = disease.jumlah_kasus,
                            satuan = Satuan.valueOf(disease.satuan),
                            tahun = disease.tahun
                        )
                    }
                    dao.insertAll(diseaseEntities)
                }
            } catch (e: Exception) {
                Log.e("API_ERROR", "Failed to fetch data: ${e.localizedMessage}")
            } finally {
                _isLoading.value = false
                fetchRowCount()
            }
        }
    }

    private fun mapToJenisPenyakit(name: String): JenisPenyakit {
        return JenisPenyakit.values().find { it.displayName.equals(name, ignoreCase = true) }
            ?: JenisPenyakit.AIDS // Default value to prevent crashes
    }

    var errorMessage = mutableStateOf<String?>(null)
        private set

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
            val count = dao.getRowCount()
            _rowCount.postValue(count)
        }
    }


    private val pager = Pager(
        config = PagingConfig(
            pageSize = 1000,
            enablePlaceholders = true,
        ),
        pagingSourceFactory = { dao.getPaginatedData() }
    )

    val pagedData = pager.flow.cachedIn(viewModelScope)
}