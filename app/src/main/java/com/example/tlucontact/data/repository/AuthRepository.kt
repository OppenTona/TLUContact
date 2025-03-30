package com.example.tlucontact.data.repository

import android.content.Context
import android.widget.Toast
import com.example.tlucontact.MainActivity
import com.example.tlucontact.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore

class AuthRepository(private val context: Context) {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    fun login(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        val trimmedEmail = email.trim()
        val trimmedPassword = password.trim()

        val error = validateCredentials(trimmedEmail, trimmedPassword)
        if (error != null) {
            onResult(false, error)
            return
        }

        auth.signInWithEmailAndPassword(trimmedEmail, trimmedPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, null)
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    fun signup(user: User, password: String, confirmPassword: String, onResult: (Boolean, String?) -> Unit) {
        val email = user.email.trim()

        val error = validateSignupInput(email, password, confirmPassword)
        if (error != null) {
            onResult(false, error)
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    firebaseUser?.let { fbUser ->
                        saveUserData(fbUser.uid, user) { success, saveError ->
                            if (success) {
                                sendEmailVerification(fbUser) { emailSuccess, emailError ->
                                    onResult(emailSuccess, emailError)
                                }
                            } else {
                                onResult(false, saveError)
                            }
                        }
                    }
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    private fun validateCredentials(email: String, password: String): String? {
        if (email.isEmpty() || password.isEmpty()) return "Vui lòng nhập email và mật khẩu"
        if (password.length < 6) return "Mật khẩu phải có ít nhất 6 ký tự"
        if (password.contains(email)) return "Mật khẩu không được chứa email"
        if (password.contains(" ")) return "Mật khẩu không được chứa khoảng trắng"
        return null
    }

    private fun validateSignupInput(email: String, password: String, confirmPassword: String): String? {
        val basicError = validateCredentials(email, password)
        if (basicError != null) return basicError
        if (password != confirmPassword) return "Mật khẩu không khớp"
        return null
    }

    private fun saveUserData(uid: String, user: User, onResult: (Boolean, String?) -> Unit) {
        val userType = if (user.email.endsWith("@tlu.edu.vn")) "lecturer" else "student"
        val userData = hashMapOf(
            "uid" to uid,
            "email" to user.email,
            "phone" to user.phone,
            "userType" to userType
        )

        firestore.collection("users").document(uid).set(userData)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, null)
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    private fun sendEmailVerification(user: FirebaseUser, onResult: (Boolean, String?) -> Unit) {
        user.sendEmailVerification()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, null)
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }
}