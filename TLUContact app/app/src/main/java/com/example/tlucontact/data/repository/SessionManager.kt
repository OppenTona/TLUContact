package com.example.tlucontact.data.repository // Định nghĩa package của file, giúp tổ chức mã nguồn.

import android.content.Context // Import lớp Context để làm việc với các thành phần của Android.
import android.content.SharedPreferences // Import lớp SharedPreferences để lưu trữ dữ liệu.
import androidx.core.content.edit // Import extension function để dễ dàng chỉnh sửa SharedPreferences.

class SessionManager(context: Context) { // Định nghĩa lớp SessionManager để quản lý phiên làm việc.

    // Tạo đối tượng SharedPreferences với tên "app_prefs" hoạt động ở chế độ PRIVATE.
    private val prefs: SharedPreferences =
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE) // Lấy SharedPreferences với tên "app_prefs".

    // Hàm kiểm tra xem người dùng đã đăng nhập hay chưa bằng cách kiểm tra "user_token".
    fun isLoggedIn(): Boolean { // Định nghĩa hàm isLoggedIn để kiểm tra trạng thái đăng nhập.
        val token = prefs.getString("user_token", null) // Lấy giá trị "user_token" từ SharedPreferences, nếu không có thì trả về null.
        return !token.isNullOrEmpty() // Kiểm tra nếu token không null và không rỗng thì trả về true, ngược lại trả về false.
    }

    // Hàm lưu token người dùng sau khi đăng nhập thành công.
    fun saveUserToken(token: String) { // Định nghĩa hàm saveUserToken để lưu token người dùng.
        prefs.edit().putString("user_token", token).apply() // Ghi token vào SharedPreferences với key "user_token" và áp dụng thay đổi ngay.
    }

    // Hàm xóa phiên làm việc của người dùng, tức là xóa hết các dữ liệu lưu trong SharedPreferences.
    fun clearSession() { // Định nghĩa hàm clearSession để xóa toàn bộ dữ liệu trong SharedPreferences.
        prefs.edit().clear().apply() // Xóa tất cả dữ liệu trong SharedPreferences và áp dụng thay đổi ngay.
    }

    // Hàm lưu email đăng nhập của người dùng.
    fun saveUserLoginEmail(email: String) { // Định nghĩa hàm saveUserLoginEmail để lưu email của người dùng.
        prefs.edit() { putString("user_email", email) } // Ghi email vào SharedPreferences với key "user_email".
    }

    // Hàm lấy email đăng nhập của người dùng.
    fun getUserLoginEmail(): String? { // Định nghĩa hàm getUserLoginEmail để lấy email của người dùng.
        return prefs.getString("user_email", null) // Lấy giá trị "user_email" từ SharedPreferences, nếu không có thì trả về null.
    }
}