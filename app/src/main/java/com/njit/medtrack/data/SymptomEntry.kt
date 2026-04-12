package com.njit.medtrack.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "health_log")
data class SymptomEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val userId: String = "",
    val date: Date,
    val symptoms: String,
    val severity: Int,
    val notes: String,
)
