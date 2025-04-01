package com.example.tlucontact.viewmodel

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tlucontact.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AuthRepository(application)

    private val _loginState = MutableStateFlow<Pair<Boolean, String?>>(Pair(false, null))
    val loginState = _loginState.asStateFlow()

    // State cho đặt lại mật khẩu
    private val _resetState = MutableStateFlow<Pair<Boolean, String?>>(Pair(false, null))
    val resetState = _resetState.asStateFlow()

    var email = MutableStateFlow("")
    var password = MutableStateFlow("")

    fun login(email: String, password: String) {
        viewModelScope.launch {
            repository.login(email, password) { success, error ->
                _loginState.value = Pair(success, error)
            }
        }
    }

    // Hàm reset mật khẩu
    fun resetPassword(email: String) {
        viewModelScope.launch {
            repository.resetPassword(email) { success, message ->
                _resetState.value = Pair(success, message)
            }
        }
    }

    // Hỗ trợ đăng nhập bằng Microsoft (Outlook)
    fun loginWithMicrosoft(activity: Activity, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            repository.loginWithMicrosoft(activity) { result ->
                result.onSuccess { firebaseUser ->
                    // Nếu cần, bạn có thể lưu token hoặc lưu thông tin user lên Firestore ở đây
                    _loginState.value = Pair(true, null)
                    onResult(true, null)
                }.onFailure { exception ->
                    _loginState.value = Pair(false, exception.message)
                    onResult(false, exception.message)
                }
            }
        }
    }


}
