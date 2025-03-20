package com.example.tlucontact.Model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle

data class User(var uid: String, var email: String, var name: String) {
    var position: String = ""
    var phone: String = ""
    var image: Int = Icons.Default.AccountCircle.hashCode()
}