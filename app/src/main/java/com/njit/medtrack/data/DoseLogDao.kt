package com.njit.medtrack.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DoseLogDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(log: DoseLog): Long

    @Query(
        "SELECT * FROM dose_logs " +
            "WHERE scheduledFor >= :startOfDay AND scheduledFor < :endOfDay " +
            "ORDER BY scheduledFor ASC",
    )
    fun watchLogsForDay(startOfDay: Long, endOfDay: Long): Flow<List<DoseLog>>
}
