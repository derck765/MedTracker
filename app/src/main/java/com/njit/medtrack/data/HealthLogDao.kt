package com.njit.medtrack.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface HealthLogDao {

    @Query("SELECT * FROM health_log ORDER BY date DESC")
    fun watchAll(): Flow<List<SymptomEntry>>

    @Insert
    suspend fun insert(entry: SymptomEntry)

    @Update
    suspend fun update(entry: SymptomEntry)

    @Query("DELETE FROM health_log WHERE id = :id")
    suspend fun deleteById(id: Long)
}
