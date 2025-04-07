package com.example.tlucontact.data.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class SessionManager(context: Context) {

    // Tạo đối tượng SharedPreferences với tên "app_prefs" hoạt động ở chế độ PRIVATE.
    private val prefs: SharedPreferences =
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    // Hàm kiểm tra xem người dùng đã đăng nhập hay chưa bằng cách kiểm tra "user_token".
    fun isLoggedIn(): Boolean {
        // Lấy token từ SharedPreferences, nếu không có sẽ trả về null.
        val token = prefs.getString("user_token", null)
        // Nếu token không null và không rỗng, trả về true nghĩa là người dùng đã đăng nhập.
        return !token.isNullOrEmpty()
    }

    // Hàm lưu token người dùng sau khi đăng nhập thành công.
    fun saveUserToken(token: String) {
        // Ghi token vào SharedPreferences với key "user_token".
        prefs.edit().putString("user_token", token).apply()
    }

    // Hàm xóa phiên làm việc của người dùng, tức là xóa hết các dữ liệu lưu trong SharedPreferences.
    fun clearSession() {
        prefs.edit().clear().apply()
    }

    // luu user login email
    fun saveUserLoginEmail(email: String) {
        prefs.edit() { putString("user_email", email) }
    }

    fun getUserLoginEmail(): String? {
        return prefs.getString("user_email", null)
    }
}