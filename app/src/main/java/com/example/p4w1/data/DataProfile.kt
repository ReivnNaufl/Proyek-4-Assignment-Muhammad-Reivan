package com.example.p4w1.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "data_profile")
class DataProfile (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val username: String,
    val uid: String,
    val email: String
)