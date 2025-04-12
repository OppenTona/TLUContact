package com.example.tlucontact.viewmodel // Định nghĩa package chứa ViewModel này

import androidx.lifecycle.ViewModel // Import lớp ViewModel từ Android Jetpack
import androidx.lifecycle.viewModelScope // Import phạm vi Coroutine liên quan đến vòng đời ViewModel
import com.example.tlucontact.data.model.Guest // Import lớp Guest từ package model
import com.example.tlucontact.data.repository.GuestRepository // Import lớp GuestRepository để làm việc với dữ liệu khách
import kotlinx.coroutines.flow.MutableStateFlow // Import MutableStateFlow để quản lý trạng thái có thể thay đổi
import kotlinx.coroutines.flow.StateFlow // Import StateFlow để phát trạng thái không thể thay đổi
import kotlinx.coroutines.launch // Import hàm launch cho Coroutine

// Lớp GuestViewModel quản lý logic và trạng thái liên quan đến khách
class GuestViewModel(
    private val repository: GuestRepository = GuestRepository() // Sử dụng GuestRepository để truy cập dữ liệu khách
) : ViewModel() { // Kế thừa từ lớp ViewModel

    private val _selectedGuest = MutableStateFlow<Guest?>(null) // Trạng thái lưu trữ khách được chọn (có thể null)
    val selectedGuest: StateFlow<Guest?> = _selectedGuest // StateFlow phát ra trạng thái khách được chọn (chỉ đọc)

    private val _snackbarMessage = MutableStateFlow<String?>(null) // Trạng thái lưu trữ thông báo Snackbar (có thể null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage // StateFlow phát ra thông báo Snackbar (chỉ đọc)

    // Hàm lấy dữ liệu khách theo email
    fun fetchGuestByEmail(email: String) {
        repository.getGuestByEmail(email) { guest -> // Gọi repository để lấy khách theo email
            _selectedGuest.value = guest // Cập nhật trạng thái khách được chọn
            if (guest == null) { // Kiểm tra nếu không tìm thấy khách
                _snackbarMessage.value = "Không thể tải dữ liệu" // Cập nhật thông báo lỗi
            }
        }
    }

    // Hàm cập nhật thông tin khách
    fun updateGuestInfo(updatedGuest: Guest) {
        viewModelScope.launch { // Sử dụng Coroutine để chạy hàm bất đồng bộ
            val result = repository.updateGuest(updatedGuest) // Gọi repository để cập nhật khách
            if (result.isSuccess) { // Kiểm tra nếu cập nhật thành công
                _selectedGuest.value = updatedGuest // Cập nhật trạng thái khách được chọn
                _snackbarMessage.value = "Cập nhật thông tin thành công" // Hiển thị thông báo thành công
            } else { // Nếu cập nhật thất bại
                _snackbarMessage.value = "Lỗi cập nhật: ${result.exceptionOrNull()?.message}" // Hiển thị thông báo lỗi
            }
        }
    }

    // Hàm xóa thông báo Snackbar
    fun clearSnackbar() {
        _snackbarMessage.value = null // Đặt trạng thái thông báo Snackbar về null
    }
}