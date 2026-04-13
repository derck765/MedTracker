package com.njit.medtrack.data

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class HealthLogRepository(context: Context) {

    private val dao = AppDatabase.get(context).healthLogDao()

    fun watchEntries(): Flow<Result<List<SymptomEntry>>> =
        dao.watchAll().map { Result.success(it) }

    suspend fun addEntry(entry: SymptomEntry): Result<Unit> = try {
        dao.insert(entry)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
