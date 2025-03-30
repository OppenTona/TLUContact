package com.example.tlucontact.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tlucontact.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ConfirmEmailViewModel(application: Application) : AndroidViewModel(application) {


    private val _confirmStatus = MutableStateFlow<ConfirmStatus>(ConfirmStatus.Idle)
    val confirmStatus: StateFlow<ConfirmStatus> = _confirmStatus
    private val repository = AuthRepository(application)
    var email: String = ""
    var password: String = ""

    fun checkEmailVerification() {
        viewModelScope.launch {
            repository.reloadUser(email) { reloadSuccess, error ->
                if (reloadSuccess) {
                    // Nếu email đã xác nhận, tiến hành tạo tài khoản.
                    repository.createAccount(email, password) { createSuccess, createError ->
                        if (createSuccess) {
                            _confirmStatus.value = ConfirmStatus.Verified
                        } else {
                            _confirmStatus.value = ConfirmStatus.Error(createError ?: "Lỗi tạo tài khoản")
                        }
                    }
                } else {
                    _confirmStatus.value = ConfirmStatus.NotVerified
                }
            }
        }
    }
}

sealed class ConfirmStatus {
    object Idle : ConfirmStatus()
    object Verified : ConfirmStatus()
    object NotVerified : ConfirmStatus()
    data class Error(val message: String) : ConfirmStatus()
}