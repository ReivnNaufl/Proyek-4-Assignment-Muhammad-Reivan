package com.example.p4w1.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "data_profile")
data class DataProfile (
    @PrimaryKey val id: Int = 1,
    var username: String = "Muhammad Reivan Naufal Mufid",
    var uid: String = "231511021",
    var email: String = "muhammad.reivan.tif23@polban.ac.id"
)