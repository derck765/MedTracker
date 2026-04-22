package com.njit.medtrack

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medications")
data class Medication(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val dosage: String,
    val scheduleTime: String,
    val frequency: String = "Daily",
    val tags: List<String>,
    val notes: String,
)
