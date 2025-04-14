package com.example.tlucontact.data.repository

// Tạo file TempImageStorage.kt
object TempImageStorage {
    private var currentImageUrl: String = ""

    fun setImageUrl(url: String) {
        currentImageUrl = url
    }

    fun getImageUrl(): String {
        return currentImageUrl
    }
}
