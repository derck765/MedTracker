package com.njit.medtrack.data

import java.util.Date

/** Profile data sourced from Firebase Auth (no sensitive fields). */
data class UserProfile(
    val uid: String,
    val email: String,
    val createdAt: Date?,
    val lastLoginAt: Date?,
)
