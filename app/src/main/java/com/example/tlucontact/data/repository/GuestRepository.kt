package com.example.tlucontact.data.repository // Định nghĩa package chứa lớp GuestRepository

import com.example.tlucontact.data.model.Guest // Import lớp Guest từ package model
import com.google.firebase.firestore.FirebaseFirestore // Import Firebase Firestore để làm việc với cơ sở dữ liệu đám mây
import kotlinx.coroutines.tasks.await // Import hàm await để xử lý các tác vụ bất đồng bộ trong Coroutine

// Lớp GuestRepository quản lý các thao tác dữ liệu liên quan đến Guest
class GuestRepository {
    private val db = FirebaseFirestore.getInstance() // Lấy instance của FirebaseFirestore
    private val guestCollection = db.collection("guests") // Truy cập collection "guests" trong Firestore

    // Hàm lấy thông tin của guest dựa vào email
    fun getGuestByEmail(email: String, onResult: (Guest?) -> Unit) {
        db.collection("guests").document(email).get() // Truy cập document của email trong collection "guests"
            .addOnSuccessListener { doc -> // Xử lý khi lấy dữ liệu thành công
                if (doc.exists()) { // Kiểm tra xem document có tồn tại không
                    val guest = Guest( // Tạo đối tượng Guest từ dữ liệu trong document
                        userId = doc.getString("userId") ?: "", // Lấy userId, nếu null gán giá trị mặc định ""
                        name = doc.getString("name") ?: "", // Lấy name, nếu null gán giá trị mặc định ""
                        email = doc.id, // Lấy email, id của document là email
                        phone = doc.getString("phone") ?: "", // Lấy phone, nếu null gán giá trị mặc định ""
                        avatarURL = doc.getString("avatarURL") ?: "", // Lấy avatarURL, nếu null gán giá trị mặc định ""
                        position = doc.getString("position") ?: "", // Lấy position, nếu null gán giá trị mặc định ""
                        department = doc.getString("department") ?: "", // Lấy department, nếu null gán giá trị mặc định ""
                        address = doc.getString("address") ?: "", // Lấy address, nếu null gán giá trị mặc định ""
                        userType = doc.getString("userType") ?: "" // Lấy userType, nếu null gán giá trị mặc định ""
                    )
                    onResult(guest) // Trả kết quả là đối tượng Guest qua callback
                } else { // Nếu document không tồn tại
                    onResult(null) // Trả kết quả null qua callback
                }
            }
            .addOnFailureListener { // Xử lý khi có lỗi xảy ra khi lấy dữ liệu
                onResult(null) // Trả kết quả null qua callback
            }
    }

    // Hàm cập nhật thông tin guest
    suspend fun updateGuest(guest: Guest): Result<Unit> { // Định nghĩa hàm cập nhật thông tin khách trả về Result
        return try {
            // Tạo một bản sao của đối tượng Guest mà không bao gồm email
            val guestData = mapOf( // Tạo Map chứa dữ liệu của guest để lưu vào Firestore
                "userId" to guest.userId, // Ánh xạ trường userId từ đối tượng guest
                "name" to guest.name, // Ánh xạ trường name từ đối tượng guest
                "phone" to guest.phone, // Ánh xạ trường phone từ đối tượng guest
                "avatarURL" to guest.avatarURL, // Ánh xạ trường avatarURL từ đối tượng guest
                "position" to guest.position, // Ánh xạ trường position từ đối tượng guest
                "department" to guest.department, // Ánh xạ trường department từ đối tượng guest
                "address" to guest.address, // Ánh xạ trường address từ đối tượng guest
                "userType" to guest.userType // Ánh xạ trường userType từ đối tượng guest
            )

            // Lưu dữ liệu vào Firestore mà không bao gồm email
            guestCollection.document(guest.email).set(guestData).await() // Cập nhật dữ liệu vào document có id là email và đợi hoàn thành
            Result.success(Unit) // Trả về kết quả thành công nếu không có lỗi
        } catch (e: Exception) { // Bắt ngoại lệ nếu có lỗi xảy ra
            Result.failure(e) // Trả về kết quả thất bại với thông tin lỗi
        }
    }
}