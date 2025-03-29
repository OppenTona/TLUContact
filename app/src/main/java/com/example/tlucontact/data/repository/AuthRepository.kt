package com.example.tlucontact.data.repository

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.example.tlucontact.PreferenceHelper
import com.example.tlucontact.data.model.User
import com.google.firebase.database.FirebaseDatabase

class AuthRepository(private val context: Context) {
    private val auth = FirebaseAuth.getInstance()
    private val preferenceHelper = PreferenceHelper(context)

    fun login(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        val trimmedEmail = email.trim()
        val trimmedPassword = password.trim()

        if (trimmedEmail.isEmpty() || trimmedPassword.isEmpty()) {
            onResult(false, "Vui lòng nhập email và mật khẩu")
            return
        }

        if (trimmedPassword.length < 6) {
            onResult(false, "Mật khẩu phải có ít nhất 6 ký tự")
            return
        }

        if (trimmedPassword.contains(trimmedEmail)) {
            onResult(false, "Mật khẩu không được chứa email")
            return
        }

        if (trimmedPassword.contains(" ")) {
            onResult(false, "Mật khẩu không được chứa khoảng trắng")
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

//    fun register(
//        email: String,
//        password: String,
//        fullName: String,
//        userId: String,
//        onResult: (Boolean, String?) -> Unit
//    ) {
//        if (!email.endsWith("@tlu.edu.vn") && !email.endsWith("@e.tlu.edu.vn")) {
//            onResult(false, "Email không hợp lệ. Sử dụng email trường.")
//            return
//        }
//
//        if (password.length < 6) {
//            onResult(false, "Mật khẩu phải từ 6 ký tự trở lên.")
//            return
//        }
//
//        auth.createUserWithEmailAndPassword(email, password)
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    val uid = auth.currentUser?.uid
//                    val type = if (email.endsWith("@tlu.edu.vn")) "CBGV" else "Sinh viên"
//                    val user = User(uid ?: "", fullName, email, userId, type)
//
//                    FirebaseDatabase.getInstance().getReference("users")
//                        .child(uid!!)
//                        .setValue(user)
//                        .addOnSuccessListener {
//                            onResult(true, null)
//                        }
//                        .addOnFailureListener { e ->
//                            onResult(false, e.message)
//                        }
//                } else {
//                    onResult(false, task.exception?.message)
//                }
//            }
//    }
}
