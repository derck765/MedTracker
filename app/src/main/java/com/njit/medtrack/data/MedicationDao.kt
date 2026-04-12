package com.njit.medtrack.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.njit.medtrack.Medication
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicationDao {

    @Query("SELECT * FROM medications WHERE userId = :userId ORDER BY name ASC")
    fun watchAll(userId: String): Flow<List<Medication>>

    @Insert
    suspend fun insert(medication: Medication)

    @Query("DELETE FROM medications WHERE id = :id")
    suspend fun deleteById(id: Long)
}
