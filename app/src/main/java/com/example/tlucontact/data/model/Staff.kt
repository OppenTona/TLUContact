package com.example.tlucontact.data.model

import com.google.firebase.firestore.DocumentReference

data class Staff(
    val staffId: String = "", // Firestore ID
    val name: String = "", // Đổi fullName thành name để khớp UI
    val position: String = "",
    val phone: String = "",
    val email: String = "",
    val photoURL: String = "",
    val department: String = "", // Đổi unit thành department
    val userId: String = ""
)
