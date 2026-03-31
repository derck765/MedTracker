package com.njit.medtrack.data

import java.util.Date

/** Backed by Firestore document `users/{uid}` (no sensitive fields). */
data class UserProfile(
    val uid: String,
    val email: String,
    val createdAt: Date?,
    val lastLoginAt: Date?,
)
