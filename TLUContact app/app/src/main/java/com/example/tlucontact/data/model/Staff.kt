package com.example.tlucontact.data.model

import android.os.Parcelable
import com.google.firebase.firestore.DocumentReference
import kotlinx.parcelize.Parcelize
@Parcelize
//Dùng để tự động implement Parcelable mà không cần viết writeToParcel() thủ công.
data class Staff(
    val staffId: String = "",       // ID trong Firestore
    override val name: String = "", // Ghi đè name từ lớp cha User, thay vì fullName
    val position: String = "",      // Chức vụ: ví dụ Trưởng Khoa, Giảng viên...
    val avatarURL: String = "",     // URL ảnh đại diện
    val department: String = "",    // Đơn vị/khoa/phòng ban, thay vì dùng "unit"
    val userId: String = "",        // ID của user trong hệ thống authentication
    val staffIdFB: String = "",     // Có thể là ID gốc trên Firestore để update document
    override val email: String = "", // Ghi đè email từ lớp cha User
    override val phone: String = ""  // Ghi đè phone từ lớp cha User
) : User(email, phone, name, userId), Parcelable
