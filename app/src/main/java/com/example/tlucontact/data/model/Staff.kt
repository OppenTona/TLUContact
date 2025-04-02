package com.example.tlucontact.data.model

import android.os.Parcelable
import com.google.firebase.firestore.DocumentReference
import kotlinx.parcelize.Parcelize

@Parcelize
data class Staff(
    val staffId: String = "", // Firestore ID
    val name: String = "", // Đổi fullName thành name để khớp UI
    val position: String = "",
    val avatarURL: String = "",
    val department: String = "", // Đổi unit thành department
    val userId: String = "" ,
    override val email: String = "",  // Phải khai báo email trong constructor
    override val phone: String = ""   // Phải khai báo phone trong constructor
) : User(email,phone), Parcelable
