package com.njit.medtrack

data class Medication(
    val id: String,
    val name: String,
    val dosage: String,
    val scheduleTime: String,
    val tags: List<String>,
    val notes: String,
)
