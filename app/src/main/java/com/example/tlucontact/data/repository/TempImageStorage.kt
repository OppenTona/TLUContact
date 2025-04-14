package com.example.tlucontact.data.repository

// Tạo file TempImageStorage.kt
object TempImageStorage {
    // Biến lưu trữ URL của hình ảnh tạm thời
    private var currentImageUrl: String = ""

    // Hàm để thiết lập URL hình ảnh
    fun setImageUrl(url: String) {
        currentImageUrl = url
    }

    // Hàm để lấy URL hình ảnh
    fun getImageUrl(): String {
        return currentImageUrl
    }
}
