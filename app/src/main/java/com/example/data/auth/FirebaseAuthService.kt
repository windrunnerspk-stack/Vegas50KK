package com.example.data.auth

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.example.R
import com.google.android.gms.tasks.Task
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine

private const val TAG = "FirebaseAuthService"

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

    fun authStateFlow(): Flow<FirebaseUser?> = callbackFlow {
        if (!isFirebaseConfigured) {
            trySend(null)
            awaitClose {}
            return@callbackFlow
        }
        val auth = FirebaseAuth.getInstance()
        val listener = FirebaseAuth.AuthStateListener { fbAuth ->
            trySend(fbAuth.currentUser)
        }
        auth.addAuthStateListener(listener)
        awaitClose {
            auth.removeAuthStateListener(listener)
        }
    }

    suspend fun signInWithGoogle(activityContext: Context): Result<FirebaseUser> {
        if (!isFirebaseConfigured) {
            return Result.failure(IllegalStateException("Firebase no está configurado"))
        }

        return try {
            val webClientId = try {
                context.getString(R.string.default_web_client_id)
            } catch (_: Exception) {
                "779512223094-64rmehd48oocma7o40mmtq0hvf15bf3u.apps.googleusercontent.com"
            }
            val credentialManager = CredentialManager.create(activityContext)

            val googleIdOption = GetSignInWithGoogleOption.Builder(webClientId)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(
                request = request,
                context = activityContext
            )

            val credential = result.credential
            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleIdTokenCredential.idToken
                val authCredential = GoogleAuthProvider.getCredential(idToken, null)
                val authResult = FirebaseAuth.getInstance().signInWithCredential(authCredential).awaitTask()
                val user = authResult.user ?: error("No se pudo obtener el usuario de Firebase")
                Result.success(user)
            } else {
                Result.failure(IllegalArgumentException("Tipo de credencial no reconocido: ${credential.type}"))
            }
        } catch (e: GetCredentialCancellationException) {
            val detail = e.message ?: ""
            Log.w(TAG, "Cancelación o fallo en Credential Manager: $detail", e)
            val friendlyMsg = if (detail.contains("reauth", ignoreCase = true) || detail.contains("16")) {
                "Error [16] de autorización de cuenta. La clave de firma del APK en tu teléfono no coincide con la registrada en Firebase."
            } else {
                "Inicio de sesión cancelado"
            }
            Result.failure(Exception(friendlyMsg, e))
        } catch (e: Exception) {
            Log.e(TAG, "Error durante el inicio de sesión con Google", e)
            val rawMsg = e.localizedMessage ?: "Error de autenticación con Google"
            val friendlyMsg = if (rawMsg.contains("reauth", ignoreCase = true) || rawMsg.contains("16")) {
                "Error [16] de autorización de cuenta. La clave de firma del APK en tu teléfono no coincide con la registrada en Firebase."
            } else if (rawMsg.contains("network", ignoreCase = true)) {
                "Error de conexión a internet. Verifica tu conexión y vuelve a intentarlo."
            } else {
                rawMsg
            }
            Result.failure(Exception(friendlyMsg, e))
        }
    }

    fun signOut() {
        if (isFirebaseConfigured) {
            try {
                FirebaseAuth.getInstance().signOut()
            } catch (e: Exception) {
                Log.e(TAG, "Error al cerrar sesión", e)
            }
        }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
suspend fun <T> Task<T>.awaitTask(): T =
    suspendCancellableCoroutine { continuation ->
        addOnSuccessListener { result ->
            continuation.resume(result) { /* cancelled */ }
        }
        addOnFailureListener { exception ->
            continuation.resumeWith(Result.failure(exception))
        }
        addOnCanceledListener {
            continuation.cancel()
        }
    }
