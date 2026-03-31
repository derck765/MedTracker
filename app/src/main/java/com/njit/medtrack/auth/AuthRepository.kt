package com.njit.medtrack.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

/**
 * Sign-in and sign-up using Firebase Auth. Passwords are never written to Firestore or local storage;
 * Firebase stores credentials using its own secure process. Profile rows in Firestore only hold
 * non-secret fields (email, timestamps) under [USERS_COLLECTION]/[uid].
 */
class AuthRepository(
    private val auth: FirebaseAuth = Firebase.auth,
) {

    private val db = Firebase.firestore

    fun signIn(email: String, password: String, callback: (Result<Unit>) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val user = result.user
                if (user == null) {
                    callback(Result.failure(IllegalStateException("No user")))
                    return@addOnSuccessListener
                }
                // Auth is the source of truth for the session; sync metadata best-effort (offline-safe).
                updateLastLoginDocument(user.uid, user.email.orEmpty())
                callback(Result.success(Unit))
            }
            .addOnFailureListener { e -> callback(Result.failure(e)) }
    }

    fun signUp(email: String, password: String, callback: (Result<Unit>) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val user = result.user
                if (user == null) {
                    callback(Result.failure(IllegalStateException("No user")))
                    return@addOnSuccessListener
                }
                createUserDocument(user.uid, user.email.orEmpty())
                    .addOnCompleteListener { fireTask ->
                        if (fireTask.isSuccessful) {
                            callback(Result.success(Unit))
                        } else {
                            auth.signOut()
                            callback(
                                Result.failure(
                                    fireTask.exception ?: Exception("Could not save your profile"),
                                ),
                            )
                        }
                    }
            }
            .addOnFailureListener { e -> callback(Result.failure(e)) }
    }

    private fun createUserDocument(uid: String, email: String) =
        db.collection(USERS_COLLECTION).document(uid).set(
            hashMapOf(
                FIELD_EMAIL to email,
                FIELD_CREATED_AT to FieldValue.serverTimestamp(),
                FIELD_LAST_LOGIN_AT to FieldValue.serverTimestamp(),
            ),
        )

    private fun updateLastLoginDocument(uid: String, email: String) =
        db.collection(USERS_COLLECTION).document(uid).set(
            hashMapOf(
                FIELD_EMAIL to email,
                FIELD_LAST_LOGIN_AT to FieldValue.serverTimestamp(),
            ),
            SetOptions.merge(),
        )

    companion object {
        const val USERS_COLLECTION = "users"
        const val FIELD_EMAIL = "email"
        const val FIELD_CREATED_AT = "createdAt"
        const val FIELD_LAST_LOGIN_AT = "lastLoginAt"
    }
}

fun Throwable.toAuthMessage(fallback: String): String {
    return when (this) {
        is FirebaseAuthInvalidCredentialsException -> "Invalid email or password."
        is FirebaseAuthInvalidUserException -> "No account found for this email."
        is FirebaseAuthUserCollisionException -> "An account with this email already exists."
        is FirebaseAuthWeakPasswordException -> "Password is too weak. Use at least 6 characters."
        else -> localizedMessage?.takeIf { it.isNotBlank() } ?: fallback
    }
}
