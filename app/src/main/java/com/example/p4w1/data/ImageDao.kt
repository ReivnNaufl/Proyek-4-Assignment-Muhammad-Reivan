package com.example.p4w1.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ImageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateImage(profileImage: ProfileImage)

    @Query("SELECT * FROM profile_image WHERE id = 1 LIMIT 1")
    suspend fun getProfileImage(): ProfileImage?
}
