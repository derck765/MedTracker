package com.njit.medtrack.data

import android.content.Context
import com.njit.medtrack.Medication
import com.njit.medtrack.notifications.ReminderScheduler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MedicationRepository(context: Context) {

    private val dao = AppDatabase.get(context).medicationDao()
    private val appContext = context.applicationContext

    fun watchMedications(): Flow<Result<List<Medication>>> =
        dao.watchAll().map { Result.success(it) }

    suspend fun addMedication(medication: Medication): Result<Unit> = try {
        val id = dao.insert(medication)
        ReminderScheduler.schedule(appContext, medication.copy(id = id))
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun updateMedication(medication: Medication): Result<Unit> = try {
        dao.update(medication)
        ReminderScheduler.cancel(appContext, medication.id)
        ReminderScheduler.schedule(appContext, medication)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun deleteMedication(medicationId: Long): Result<Unit> = try {
        dao.deleteById(medicationId)
        ReminderScheduler.cancel(appContext, medicationId)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getAllMedications(): List<Medication> = dao.getAll()
}
