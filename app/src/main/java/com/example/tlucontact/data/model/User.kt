package com.example.tlucontact.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle

data class User(
    val uid: String = "",
    val fullName: String = "",
    val email: String = "",
    val userId: String = "",
    val type: String = "" // "CBGV" hoặc "Sinh viên"
)