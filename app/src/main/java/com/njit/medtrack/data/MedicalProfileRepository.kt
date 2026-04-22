package com.njit.medtrack.data

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MedicalProfileRepository(context: Context) {

    private val dao = AppDatabase.get(context).medicalProfileDao()

    fun watchProfile(): Flow<MedicalProfile> = dao.watch().map { it ?: MedicalProfile() }

    suspend fun saveProfile(profile: MedicalProfile): Result<Unit> = try {
        dao.upsert(profile)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
