package com.example.data.auth

import android.content.Context
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine

class FirebaseAuthService(private val context: Context) {

    private val isFirebaseConfigured: Boolean
        get() = try {
            if (FirebaseApp.getApps(context).isNotEmpty()) {
                true
            } else {
                FirebaseApp.initializeApp(context) != null
            }
        } catch (_: Exception) {
            false
        }

    val currentFirebaseUser: FirebaseUser?
        get() = if (isFirebaseConfigured) {
            try {
                FirebaseAuth.getInstance().currentUser
            } catch (_: Exception) {
                null
            }
        } else {
            null
        }

    suspend fun registerUser(email: String, password: String, displayName: String): Result<String> {
        val cleanEmail = email.trim().lowercase()
        return if (isFirebaseConfigured) {
            try {
                val auth = FirebaseAuth.getInstance()
                val authResult = auth.createUserWithEmailAndPassword(cleanEmail, password).awaitTask()
                val user = authResult.user
                if (user != null && displayName.isNotBlank()) {
                    val update = UserProfileChangeRequest.Builder()
                        .setDisplayName(displayName.trim())
                        .build()
                    user.updateProfile(update).awaitTask()
                }
                Result.success(user?.uid ?: "uid_${System.currentTimeMillis()}")
            } catch (e: Exception) {
                Result.failure(e)
            }
        } else {
            // Local fallback if google-services.json has not been attached
            Result.success("local_user_${System.currentTimeMillis()}")
        }
    }

    suspend fun loginUser(email: String, password: String): Result<String> {
        val cleanEmail = email.trim().lowercase()
        return if (isFirebaseConfigured) {
            try {
                val auth = FirebaseAuth.getInstance()
                val authResult = auth.signInWithEmailAndPassword(cleanEmail, password).awaitTask()
                Result.success(authResult.user?.uid ?: "uid_${System.currentTimeMillis()}")
            } catch (e: Exception) {
                Result.failure(e)
            }
        } else {
            Result.success("local_user_${System.currentTimeMillis()}")
        }
    }

    fun signOut() {
        if (isFirebaseConfigured) {
            try {
                FirebaseAuth.getInstance().signOut()
            } catch (_: Exception) {}
        }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
suspend fun <T> Task<T>.awaitTask(): T =
    suspendCancellableCoroutine { continuation ->
        addOnSuccessListener { result ->
            continuation.resume(result, null)
        }
        addOnFailureListener { exception ->
            continuation.resumeWith(Result.failure(exception))
        }
        addOnCanceledListener {
            continuation.cancel()
        }
    }
