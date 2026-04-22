package com.njit.medtrack.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicalProfileDao {

    @Query("SELECT * FROM medical_profiles WHERE id = 1")
    fun watch(): Flow<MedicalProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(profile: MedicalProfile)
}
