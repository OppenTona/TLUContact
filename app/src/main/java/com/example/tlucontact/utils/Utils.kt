package com.example.tlucontact.utils

import android.content.Context
import com.example.tlucontact.data.repository.SessionManager // Đảm bảo import SessionManager
import android.util.Log

fun checkAdminPermission(context: Context): Boolean {
    val userEmail = SessionManager(context).getUserLoginEmail()
    val adminEmails = listOf("luukhanh656@gmail.com") // Danh sách email admin
    Log.d("AdminCheck", "User email: $userEmail, Is admin: ${adminEmails.contains(userEmail)}")
    return adminEmails.contains(userEmail)
}