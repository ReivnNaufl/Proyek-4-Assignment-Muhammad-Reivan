package com.example.p4w1.viewmodel

import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.p4w1.data.AppDatabase
import com.example.p4w1.data.DataEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.p4w1.data.Satuan
import com.example.p4w1.data.JenisPenyakit
import okio.IOException
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook

class DataViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).dataDao()

    private val _rowCount = MutableLiveData(0)
    val rowCount: LiveData<Int> = _rowCount

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    val dataList: LiveData<List<DataEntity>> = dao.getAll()


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

    fun convertToCsv(dataList: List<DataEntity>): String {
        val csvHeader = "ID,Nama Provinsi,Kode Provinsi,Nama Kabupaten/Kota,Kode Kabupaten/Kota,Jenis Penyakit,Total,Satuan,Tahun\n"
        val csvRows = dataList.joinToString("\n") { data ->
            "${data.id},${data.namaProvinsi},${data.kodeProvinsi},${data.namaKabupatenKota},${data.kodeKabupatenKota},${data.jenisPenyakit},${data.total},${data.satuan},${data.tahun}"
        }
        return csvHeader + csvRows
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun saveCsvToDownloads(context: Context, csvContent: String) {
        val fileName = "OpenData - Jumlah Kasus Penyakit - ${System.currentTimeMillis()}.csv"
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "text/csv")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

        uri?.let {
            try {
                resolver.openOutputStream(uri)?.use { outputStream ->
                    outputStream.write(csvContent.toByteArray())
                    Log.d("CSVSave", "CSV file saved to Downloads folder: $uri")
                }
            } catch (e: IOException) {
                Log.e("CSVSave", "Failed to save CSV file: ${e.message}")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun exportToExcel(
        context: Context,
        dataList: List<DataEntity>
    ) {
        val fileName = "OpenData - Jumlah Kasus Penyakit - ${System.currentTimeMillis()}.xlsx"
        // Create a new workbook and sheet
        val workbook: Workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Data")

        // Create header row
        val headerRow = sheet.createRow(0)
        headerRow.createCell(0).setCellValue("ID")
        headerRow.createCell(1).setCellValue("Kode Provinsi")
        headerRow.createCell(2).setCellValue("Nama Provinsi")
        headerRow.createCell(3).setCellValue("Kode Kabupaten/Kota")
        headerRow.createCell(4).setCellValue("Nama Kabupaten/Kota")
        headerRow.createCell(5).setCellValue("Jenis Penyakit")
        headerRow.createCell(6).setCellValue("Total")
        headerRow.createCell(7).setCellValue("Satuan")
        headerRow.createCell(8).setCellValue("Tahun")

        // Fill data rows
        for ((index, data) in dataList.withIndex()) {
            val row = sheet.createRow(index + 1)
            row.createCell(0).setCellValue(data.id.toDouble())
            row.createCell(1).setCellValue(data.kodeProvinsi)
            row.createCell(2).setCellValue(data.namaProvinsi)
            row.createCell(3).setCellValue(data.kodeKabupatenKota)
            row.createCell(4).setCellValue(data.namaKabupatenKota)
            row.createCell(5).setCellValue(data.jenisPenyakit.toString()) // Assuming JenisPenyakit has a proper toString()
            row.createCell(6).setCellValue(data.total.toDouble())
            row.createCell(7).setCellValue(data.satuan.toString()) // Assuming Satuan has a proper toString()
            row.createCell(8).setCellValue(data.tahun.toDouble())
        }

        // Save the workbook to a file using MediaStore
        val contentValues = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, fileName)
            put(MediaStore.Downloads.MIME_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
            put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        val resolver = context.contentResolver
        val uri: Uri? = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        uri?.let {
            try {
                resolver.openOutputStream(it)?.use { outputStream ->
                    workbook.write(outputStream)
                }
                workbook.close()
                Toast.makeText(context, "File saved to Downloads", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Failed to save file: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            Toast.makeText(context, "Failed to create file", Toast.LENGTH_SHORT).show()
        }
    }
}