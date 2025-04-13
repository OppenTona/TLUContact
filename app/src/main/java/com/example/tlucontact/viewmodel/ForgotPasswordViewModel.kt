package com.example.tlucontact.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tlucontact.data.repository.ForgotPasswordPepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ForgotPasswordPepository(application) // Tạo một instance của AuthRepository với tham số application

    private val _loginState = MutableStateFlow<Pair<Boolean, String?>>(Pair(false, null)) // MutableStateFlow để lưu trạng thái đăng nhập
    val loginState = _loginState.asStateFlow() // Expose loginState như một StateFlow không mutable

    private val _emailError = MutableStateFlow(false) // Trạng thái lỗi email, mặc định là false
    val emailError = _emailError.asStateFlow() // Expose emailError như một StateFlow không mutable

    private val _passwordError = MutableStateFlow(false) // Trạng thái lỗi password, mặc định là false
    val passwordError = _passwordError.asStateFlow() // Expose passwordError như một StateFlow không mutable

    private val _resetState = MutableStateFlow<Pair<Boolean, String?>>(Pair(false, null)) // Trạng thái đặt lại mật khẩu
    val resetState = _resetState.asStateFlow() // Expose resetState như một StateFlow không mutable

    var email = MutableStateFlow("") // Khởi tạo MutableStateFlow để lưu giá trị email
    var password = MutableStateFlow("") // Khởi tạo MutableStateFlow để lưu giá trị password

    fun resetPassword(email: String) {
        val emailValue = email.trim()
        if (emailValue.isEmpty()) {
            _emailError.value = true
            _resetState.value = Pair(false, "Vui lòng nhập email")
            return
        } else {
            _emailError.value = false
        }

        viewModelScope.launch {
            repository.resetPassword(emailValue) { success, message ->
                _resetState.value = Pair(success, message)
            }
        }
    }
}