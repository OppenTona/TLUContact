package com.example.tlucontact.viewmodel // Định nghĩa package chứa ViewModel này

import android.app.Application // Import lớp Application từ Android
import androidx.lifecycle.AndroidViewModel // Import lớp AndroidViewModel để tạo ViewModel có liên kết với Application
import androidx.lifecycle.viewModelScope // Import phạm vi Coroutine liên quan đến vòng đời ViewModel
import com.example.tlucontact.data.repository.LogOutRepository
import kotlinx.coroutines.flow.MutableStateFlow // Import MutableStateFlow để quản lý trạng thái có thể thay đổi
import kotlinx.coroutines.flow.asStateFlow // Import hàm asStateFlow để chuyển MutableStateFlow thành StateFlow chỉ đọc
import kotlinx.coroutines.launch // Import hàm launch cho Coroutine

// Lớp LogOutViewModel quản lý logic liên quan đến việc đăng xuất người dùng
class LogOutViewModel (application: Application) : AndroidViewModel(application) { // Kế thừa từ lớp AndroidViewModel
    private val repository = LogOutRepository(application) // Tạo một instance của AuthRepository để sử dụng các chức năng liên quan đến xác thực

    // Trạng thái đăng xuất (true nếu thành công, false nếu thất bại)
    private val _logoutState = MutableStateFlow<Pair<Boolean, String?>>(Pair(false, null)) // Tạo MutableStateFlow để lưu trạng thái đăng xuất (Boolean và thông báo lỗi nếu có)
    val logoutState = _logoutState.asStateFlow() // Chuyển _logoutState thành StateFlow chỉ đọc để cung cấp cho giao diện

    // Hàm thực hiện đăng xuất
    fun logout() {
        viewModelScope.launch { // Sử dụng Coroutine để thực hiện tác vụ bất đồng bộ
            repository.logout { success, error -> // Gọi hàm logout từ AuthRepository
                _logoutState.value = Pair(success, error) // Cập nhật trạng thái đăng xuất với kết quả (success hoặc error message)
            }
        }
    }
}