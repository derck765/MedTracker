package com.njit.medtrack.data

import android.content.Context
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class HealthLogRepository(context: Context) {

    private val dao = AppDatabase.get(context).healthLogDao()

    fun watchEntries(): Flow<Result<List<SymptomEntry>>> {
        val uid = Firebase.auth.currentUser?.uid
            ?: return flow { emit(Result.failure(IllegalStateException("Not signed in"))) }
        return dao.watchAll(uid).map { Result.success(it) }
    }

    suspend fun addEntry(entry: SymptomEntry): Result<Unit> {
        val uid = Firebase.auth.currentUser?.uid
            ?: return Result.failure(IllegalStateException("Not signed in"))
        return try {
            dao.insert(entry.copy(userId = uid))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
