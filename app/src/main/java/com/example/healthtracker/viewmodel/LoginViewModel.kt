package com.example.healthtracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    private val _userEmail = MutableStateFlow("")
    val userEmail: StateFlow<String> = _userEmail

    private val _userPhotoUrl = MutableStateFlow<String?>(null)
    val userPhotoUrl: StateFlow<String?> = _userPhotoUrl

    private val _isGoogleLogin = MutableStateFlow(false)
    val isGoogleLogin: StateFlow<Boolean> = _isGoogleLogin

    // ✅ Khi ViewModel được tạo -> lấy user hiện tại nếu đã đăng nhập trước đó
    init {
        val user = auth.currentUser
        if (user != null) {
            _userEmail.value = user.email ?: ""
            _userPhotoUrl.value = user.photoUrl?.toString()
            _isGoogleLogin.value = user.providerData.any { it.providerId == "google.com" }
        }
    }

    // Login Email/Password
    fun loginWithEmail(email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    _userEmail.value = user?.email ?: email
                    _userPhotoUrl.value = null // Email login không có ảnh
                    _isGoogleLogin.value = false
                    onSuccess()
                } else onError(task.exception?.message ?: "Login failed")
            }
    }

    // Login Google
    fun firebaseAuthWithGoogle(account: GoogleSignInAccount, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    _userEmail.value = user?.email ?: account.email ?: ""
                    _userPhotoUrl.value = user?.photoUrl?.toString()
                    _isGoogleLogin.value = true
                    onSuccess()
                } else onError(task.exception?.message ?: "Google Sign-In failed")
            }
    }

    // Logout + Reset state
    fun logout(onComplete: () -> Unit) {
        viewModelScope.launch {
            val context = getApplication<Application>().applicationContext
            auth.signOut()

            if (_isGoogleLogin.value) {
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()
                val googleClient = GoogleSignIn.getClient(context, gso)
                googleClient.signOut().addOnCompleteListener {
                    googleClient.revokeAccess().addOnCompleteListener {
                        resetState()
                        onComplete()
                    }
                }
            } else {
                resetState()
                onComplete()
            }
        }
    }

    private fun resetState() {
        _userEmail.value = ""
        _userPhotoUrl.value = null
        _isGoogleLogin.value = false
    }

    // Reset Password
    fun resetPassword(email: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (email.isBlank()) {
            onError("Please enter your email first")
            return
        }

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    onError(task.exception?.message ?: "Failed to send reset email")
                }
            }
    }
}
