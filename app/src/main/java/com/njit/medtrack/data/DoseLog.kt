package com.njit.medtrack.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "dose_logs",
    indices = [Index(value = ["medicationId", "scheduledFor"], unique = true)],
)
data class DoseLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val medicationId: Long,
    val scheduledFor: Long,
    val takenAt: Long,
)
