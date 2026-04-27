package com.njit.medtrack.data

import android.content.Context
import com.njit.medtrack.Medication
import com.njit.medtrack.notifications.ReminderScheduler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.njit.medtrack.api.RxNormService

class MedicationRepository(context: Context) {

    private val dao = AppDatabase.get(context).medicationDao()
    private val appContext = context.applicationContext
    private val rxNormService = RxNormService()

    fun watchMedications(): Flow<Result<List<Medication>>> =
        dao.watchAll().map { Result.success(it) }

    suspend fun addMedication(medication: Medication): Result<Unit> = try {
        val rxcui = try {
            rxNormService.getRxCui(medication.name)
        } catch (e: Exception) {
            null
        }
        val updatedMedication = medication.copy(rxcui = rxcui)
        val id = dao.insert(updatedMedication)
        android.util.Log.d("RXNORM", "Medication: ${medication.name}, RxCUI: $rxcui")
        ReminderScheduler.schedule(appContext, updatedMedication.copy(id = id))
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
