package com.njit.medtrack.data

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.njit.medtrack.auth.AuthRepository

/**
 * Reads the signed-in user's profile from Firestore (written on sign-up / sign-in).
 */
class UserProfileRepository {

    private val db = Firebase.firestore

    fun fetchProfileForCurrentUser(callback: (Result<UserProfile>) -> Unit) {
        val uid = Firebase.auth.currentUser?.uid
        if (uid == null) {
            callback(Result.failure(IllegalStateException("Not signed in")))
            return
        }
        db.collection(AuthRepository.USERS_COLLECTION).document(uid).get()
            .addOnSuccessListener { doc ->
                if (!doc.exists()) {
                    callback(Result.failure(NoSuchElementException("No profile")))
                    return@addOnSuccessListener
                }
                val email = doc.getString(AuthRepository.FIELD_EMAIL).orEmpty()
                val createdAt = doc.getTimestamp(AuthRepository.FIELD_CREATED_AT)?.toDate()
                val lastLoginAt = doc.getTimestamp(AuthRepository.FIELD_LAST_LOGIN_AT)?.toDate()
                callback(
                    Result.success(
                        UserProfile(
                            uid = uid,
                            email = email,
                            createdAt = createdAt,
                            lastLoginAt = lastLoginAt,
                        ),
                    ),
                )
            }
            .addOnFailureListener { callback(Result.failure(it)) }
    }
}
