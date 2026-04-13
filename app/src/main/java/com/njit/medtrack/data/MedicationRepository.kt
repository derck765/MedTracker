package com.njit.medtrack.data

import android.content.Context
import com.njit.medtrack.Medication
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MedicationRepository(context: Context) {

    private val dao = AppDatabase.get(context).medicationDao()

    fun watchMedications(): Flow<Result<List<Medication>>> =
        dao.watchAll().map { Result.success(it) }

    suspend fun addMedication(medication: Medication): Result<Unit> = try {
        dao.insert(medication)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun deleteMedication(medicationId: Long): Result<Unit> = try {
        dao.deleteById(medicationId)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
