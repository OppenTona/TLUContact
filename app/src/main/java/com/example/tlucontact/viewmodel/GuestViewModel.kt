package com.example.tlucontact.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tlucontact.data.model.Guest
import com.example.tlucontact.data.repository.GuestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GuestViewModel(
    private val repository: GuestRepository = GuestRepository()
) : ViewModel() {

    private val _selectedGuest = MutableStateFlow<Guest?>(null)
    val selectedGuest: StateFlow<Guest?> = _selectedGuest

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage

    fun fetchGuestByEmail(email: String) {
        repository.getGuestByEmail(email) { guest ->
            _selectedGuest.value = guest
            if (guest == null) {
                _snackbarMessage.value = "Không thể tải dữ liệu"
            }
        }
    }

    fun updateGuestInfo(updatedGuest: Guest) {
        viewModelScope.launch {
            val result = repository.updateGuest(updatedGuest)
            if (result.isSuccess) {
                _selectedGuest.value = updatedGuest
                _snackbarMessage.value = "Cập nhật thông tin thành công"
            } else {
                _snackbarMessage.value = "Lỗi cập nhật: ${result.exceptionOrNull()?.message}"
            }
        }
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }
}
