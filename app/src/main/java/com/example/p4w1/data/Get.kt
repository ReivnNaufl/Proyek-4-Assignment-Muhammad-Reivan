import com.example.p4w1.data.JenisPenyakit

data class ApiResponse(
    val message: String,
    val error: Int,
    val data: List<DiseaseCase>
)

data class DiseaseCase(
    val id: Int,
    val kode_provinsi: Int,
    val nama_provinsi: String,
    val kode_kabupaten_kota: Int,
    val nama_kabupaten_kota: String,
    val jenis_penyakit: String,
    val jumlah_kasus: Int,
    val satuan: String,
    val tahun: Int
)