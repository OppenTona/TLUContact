package com.example.tlucontact.data.repository

import com.example.tlucontact.data.model.Guest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpRepository() {
    private val auth = FirebaseAuth.getInstance() // Lấy instance của FirebaseAuth để sử dụng các chức năng xác thực
    private val firestore = FirebaseFirestore.getInstance() // Lấy instance của FirebaseFirestore để thao tác với database

    // Hàm xử lý đăng ký tài khoản mới
    fun signup(email: String, password: String, name: String, phone: String, onResult: (Boolean, String?) -> Unit) {
        // Gọi FirebaseAuth để tạo tài khoản mới
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task -> // Lắng nghe kết quả của quá trình đăng ký
                if (task.isSuccessful) { // Nếu đăng ký thành công
                    val uid = auth.currentUser?.uid ?: return@addOnCompleteListener // Lấy UID của người dùng đăng ký
                    saveUserData(uid, email, name, phone) { success, error -> // Lưu dữ liệu người dùng vào Firestore
                        if (success) { // Nếu lưu dữ liệu thành công
                            auth.currentUser?.sendEmailVerification() // Gửi email xác thực
                            onResult(true, null) // Trả kết quả thành công
                        } else {
                            onResult(false, error) // Trả kết quả thất bại kèm thông báo lỗi
                        }
                    }
                } else {
                    onResult(false, task.exception?.message) // Trả kết quả thất bại kèm thông báo lỗi
                }
            }
    }

    // Hàm xác định loại tài khoản dựa trên email
    private fun detectUserType(email: String): String {
        return when { // Sử dụng when để xác định loại tài khoản
            email.endsWith("@tlu.edu.vn") -> "staff" // Nếu email kết thúc bằng @tlu.edu.vn thì loại là staff
            email.endsWith("@e.tlu.edu.vn") -> "student" // Nếu email kết thúc bằng @e.tlu.edu.vn thì loại là student
            else -> "guest" // Các trường hợp còn lại là guest
        }
    }

    // Lưu dữ liệu người dùng dựa trên loại tài khoản
    private fun saveUserData(uid: String, email: String, name: String, phone: String, onResult: (Boolean, String?) -> Unit) {
        val userType = detectUserType(email) // Xác định loại tài khoản từ email

        if (userType == "staff" || userType == "student") { // Nếu là staff hoặc student thì không lưu dữ liệu
            onResult(true, null) // Trả kết quả thành công mà không lưu dữ liệu
        } else {
            val guest = Guest(email, phone, name, userType) // Tạo đối tượng Guest với thông tin người dùng
            // Chuẩn bị dữ liệu để lưu vào Firestore
            val userData = mapOf(
                "uid" to guest.uid, // UID của người dùng
                "name" to guest.name, // Tên của người dùng
                "phone" to guest.phone, // Số điện thoại của người dùng
                "avatarURL" to guest.avatarURL, // URL ảnh đại diện của người dùng
                "position" to guest.position,
                "department" to guest.department,// Chức vụ của người dùng
                "userId" to guest.userId, // ID người dùng
                "adress" to guest.address, // Địa chỉ của người dùng
                "userType" to "guest" // Loại tài khoản (guest)
            )
            firestore.collection("guests") // Chọn collection "guests" trong Firestore
                .document(email) // Tạo document với ID là email
                .set(userData) // Lưu dữ liệu vào document
                .addOnCompleteListener { task -> // Lắng nghe kết quả của quá trình lưu
                    if (task.isSuccessful) { // Nếu lưu thành công
                        onResult(true, null) // Trả kết quả thành công
                    } else {
                        onResult(false, task.exception?.message) // Trả kết quả thất bại kèm thông báo lỗi
                    }
                }
        }
    }
}