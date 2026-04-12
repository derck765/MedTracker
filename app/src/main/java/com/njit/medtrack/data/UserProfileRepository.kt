package com.njit.medtrack.data

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.Date

class UserProfileRepository {

    fun fetchProfileForCurrentUser(callback: (Result<UserProfile>) -> Unit) {
        val user = Firebase.auth.currentUser
        if (user == null) {
            callback(Result.failure(IllegalStateException("Not signed in")))
            return
        }
        val profile = UserProfile(
            uid = user.uid,
            email = user.email.orEmpty(),
            createdAt = user.metadata?.creationTimestamp?.let { Date(it) },
            lastLoginAt = user.metadata?.lastSignInTimestamp?.let { Date(it) },
        )
        callback(Result.success(profile))
    }
}
