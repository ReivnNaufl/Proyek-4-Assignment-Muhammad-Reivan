package com.example.p4w1.data

import com.google.gson.annotations.SerializedName

enum class JenisPenyakit(val displayName: String) {
    @SerializedName("AIDS") AIDS("AIDS"),
    @SerializedName("CAMPAK") CAMPAK("Campak"),
    @SerializedName("DBD") DBD("DBD"),
    @SerializedName("DIARE") DIARE("Diare"),
    @SerializedName("KUSTA") KUSTA("Kusta"),
    @SerializedName("MALARIA") MALARIA("Malaria"),
    @SerializedName("PNEUMONIA") PNEUMONIA("Pneumonia"),
    @SerializedName("TB TUBERCOLOSIS") TUBERCOLOSIS("TB Tubercolosis"),
    @SerializedName("TETANUS") TETANUS("Tetanus")
}
