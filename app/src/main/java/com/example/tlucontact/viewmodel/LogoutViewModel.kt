package com.example.tlucontact.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tlucontact.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LogoutViewModel (application: Application) : AndroidViewModel(application) {
    private val repository = AuthRepository(application)

    // Trạng thái đăng xuất (true nếu thành công, false nếu thất bại)
    private val _logoutState = MutableStateFlow<Pair<Boolean, String?>>(Pair(false, null))
    val logoutState = _logoutState.asStateFlow()

    // Hàm thực hiện đăng xuất
    fun logout() {
        viewModelScope.launch {
            repository.logout { success, error ->
                _logoutState.value = Pair(success, error) // Cập nhật trạng thái đăng xuất
            }
        }
    }
}