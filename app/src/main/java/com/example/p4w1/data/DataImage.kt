package com.example.p4w1.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profile_image")
data class ProfileImage(
    @PrimaryKey val id: Int = 1, // Ensures only one image entry exists
    val imgURI: String
)
