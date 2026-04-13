package com.njit.medtrack.data

import android.content.Context
import java.util.Calendar
import kotlinx.coroutines.flow.Flow

class DoseLogRepository(context: Context) {

    private val dao = AppDatabase.get(context).doseLogDao()

    fun watchTodaysLogs(): Flow<List<DoseLog>> {
        val start = startOfToday()
        val end = start + DAY_MILLIS
        return dao.watchLogsForDay(start, end)
    }

    suspend fun markTaken(medicationId: Long, scheduledFor: Long): Result<Unit> = try {
        dao.insert(
            DoseLog(
                medicationId = medicationId,
                scheduledFor = scheduledFor,
                takenAt = System.currentTimeMillis(),
            ),
        )
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    private fun startOfToday(): Long = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    companion object {
        private const val DAY_MILLIS = 24L * 60 * 60 * 1000
    }
}
