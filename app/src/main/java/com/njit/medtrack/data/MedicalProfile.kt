package com.njit.medtrack.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medical_profiles")
data class MedicalProfile(
    @PrimaryKey val id: Int = 1,
    val fullName: String = "",
    val dateOfBirth: String = "",
    val bloodType: String = "",
    val allergies: String = "",
    val conditions: String = "",
    val emergencyContact: String = "",
    val emergencyPhone: String = "",
    val insuranceProvider: String = "",
    val insuranceId: String = "",
)
