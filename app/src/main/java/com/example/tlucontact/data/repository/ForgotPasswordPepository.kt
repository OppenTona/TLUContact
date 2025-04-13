package com.example.tlucontact.data.repository

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ForgotPasswordPepository(private val context: Context) {
    private val auth = FirebaseAuth.getInstance() // Lấy instance của FirebaseAuth để sử dụng các chức năng xác thực
    private val firestore = FirebaseFirestore.getInstance() // Lấy instance của FirebaseFirestore để thao tác với database

    // Hàm xử lý đặt lại mật khẩu
    fun resetPassword(email: String, onResult: (Boolean, String?) -> Unit) {
        val trimmedEmail = email.trim() // Xóa khoảng trắng ở đầu và cuối email
        if (trimmedEmail.isEmpty()) { // Kiểm tra nếu email rỗng
            onResult(false, "Vui lòng nhập email!") // Trả kết quả thất bại với thông báo email rỗng
            return // Thoát khỏi hàm
        }
        auth.sendPasswordResetEmail(trimmedEmail) // Gửi email đặt lại mật khẩu qua Firebase
            .addOnCompleteListener { task -> // Lắng nghe kết quả của quá trình gửi email
                if (task.isSuccessful) { // Nếu gửi email thành công
                    onResult(true, "Email đặt lại mật khẩu đã được gửi!") // Trả kết quả thành công
                } else {
                    onResult(false, task.exception?.message) // Trả kết quả thất bại kèm thông báo lỗi
                }
            }
    }
}