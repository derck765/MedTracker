package com.njit.medtrack.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HealthLogDao {

    @Query("SELECT * FROM health_log WHERE userId = :userId ORDER BY date DESC")
    fun watchAll(userId: String): Flow<List<SymptomEntry>>

    @Insert
    suspend fun insert(entry: SymptomEntry)
}
