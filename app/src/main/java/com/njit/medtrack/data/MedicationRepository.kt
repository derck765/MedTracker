package com.njit.medtrack.data

import android.content.Context
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.njit.medtrack.Medication
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class MedicationRepository(context: Context) {

    private val dao = AppDatabase.get(context).medicationDao()

    fun watchMedications(): Flow<Result<List<Medication>>> {
        val uid = Firebase.auth.currentUser?.uid
            ?: return flow { emit(Result.failure(IllegalStateException("Not signed in"))) }
        return dao.watchAll(uid).map { Result.success(it) }
    }

    suspend fun addMedication(medication: Medication): Result<Unit> {
        val uid = Firebase.auth.currentUser?.uid
            ?: return Result.failure(IllegalStateException("Not signed in"))
        return try {
            dao.insert(medication.copy(userId = uid))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteMedication(medicationId: Long): Result<Unit> = try {
        dao.deleteById(medicationId)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
