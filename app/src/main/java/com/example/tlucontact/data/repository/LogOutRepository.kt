package com.example.tlucontact.data.repository

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LogOutRepository(private val context: Context) {
    private val auth = FirebaseAuth.getInstance() // Lấy instance của FirebaseAuth để sử dụng các chức năng xác thực
    private val firestore = FirebaseFirestore.getInstance() // Lấy instance của FirebaseFirestore để thao tác với database

    // Hàm xử lý đăng xuất
    fun logout(onComplete: (Boolean, String?) -> Unit) {
        try {
            auth.signOut() // Đăng xuất khỏi Firebase
            SessionManager(context).clearSession() // Xóa dữ liệu phiên làm việc
            onComplete(true, null) // Thành công
        } catch (e: Exception) {
            onComplete(false, e.message) // Thất bại
        }
    }
}