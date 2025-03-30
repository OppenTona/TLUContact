package com.example.tlucontact.data.repository

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.tlucontact.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.example.tlucontact.data.model.User
import com.google.firebase.database.FirebaseDatabase

class AuthRepository(private val context: Context) {
    private val auth = FirebaseAuth.getInstance()

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
        val trimmedemail = user.email.trim()

        val error = validateSignupInput(trimmedemail, password, confirmPassword)
        if (error != null) {
            onResult(false, error)
            return
        }


        val auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(trimmedemail, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Đăng ký thành công!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(context, MainActivity::class.java)
                    var user = User(auth.currentUser?.uid ?: "", trimmedemail)
                    saveUserData(user)
                    context.startActivity(intent)
                } else {
                    Toast.makeText(context, "Lỗi: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
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

    private fun saveUserData(user: User) {
        if (user.uid.isEmpty()) return

        val database = FirebaseDatabase.getInstance("https://tlucontract-default-rtdb.asia-southeast1.firebasedatabase.app/")
        val userRef = database.getReference("users").child(user.uid)

        val userData = mapOf(
            "uid" to user.uid,
            "email" to user.email,
            "name" to user.name,
            "phone" to user.phone,
            "position" to user.position,
            "image" to user.image
        )

        userRef.setValue(userData)
    }
}
