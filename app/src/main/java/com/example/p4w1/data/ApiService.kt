import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("bigdata/dinkes/od_15940_jumlah_kasus_penyakit_berdasarkan_jenis_penyakit")
    suspend fun getDiseaseData(
        @Query("limit") limit: Int = 250,
        ): ApiResponse
}