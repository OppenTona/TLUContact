package com.example.tlucontact.viewmodel // Định nghĩa package chứa lớp LoginViewModel

import android.app.Activity // Import lớp Activity từ Android SDK
import android.app.Application // Import lớp Application từ Android SDK
import androidx.lifecycle.AndroidViewModel // Import lớp AndroidViewModel từ thư viện Jetpack
import androidx.lifecycle.viewModelScope // Import viewModelScope để quản lý coroutine
import com.example.tlucontact.data.repository.AuthRepository // Import AuthRepository để sử dụng trong lớp
import kotlinx.coroutines.flow.MutableStateFlow // Import MutableStateFlow để quản lý trạng thái
import kotlinx.coroutines.flow.asStateFlow // Import asStateFlow để chỉ expose trạng thái không mutable
import kotlinx.coroutines.launch // Import launch để chạy coroutine

class LoginViewModel(application: Application) : AndroidViewModel(application) { // Định nghĩa lớp LoginViewModel kế thừa AndroidViewModel
    private val repository = AuthRepository(application) // Tạo một instance của AuthRepository với tham số application

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

    fun login(email: String, password: String) { // Hàm login nhận tham số email và password
        val error = validateCredentials() // Gọi hàm validateCredentials để kiểm tra thông tin đăng nhập
        if (error != null) { // Nếu có lỗi trả về từ validateCredentials
            _loginState.value = Pair(false, error) // Cập nhật trạng thái đăng nhập thất bại và lỗi
            return // Kết thúc hàm nếu có lỗi
        }

        viewModelScope.launch { // Sử dụng coroutine để gọi hàm login từ repository
            repository.login(email, password) { success, error -> // Gọi hàm login và xử lý callback
                _loginState.value = Pair(success, error) // Cập nhật trạng thái đăng nhập dựa trên kết quả callback
            }
        }
    }

    private fun validateCredentials(): String? { // Hàm kiểm tra thông tin đăng nhập, trả về lỗi nếu có
        val emailValue = email.value.trim() // Lấy và loại bỏ khoảng trắng trong giá trị email
        val passwordValue = password.value // Lấy giá trị password

        if (emailValue.isEmpty()) { // Kiểm tra nếu email trống
            _emailError.value = true // Cập nhật trạng thái lỗi email
            return "Vui lòng nhập email" // Trả về thông báo lỗi
        } else { // Nếu email không trống
            _emailError.value = false // Cập nhật trạng thái lỗi email về false
        }

        if (passwordValue.isEmpty()) { // Kiểm tra nếu password trống
            _passwordError.value = true // Cập nhật trạng thái lỗi password
            return "Vui lòng nhập mật khẩu" // Trả về thông báo lỗi
        } else { // Nếu password không trống
            _passwordError.value = false // Cập nhật trạng thái lỗi password về false
        }
        return null // Trả về null nếu không có lỗi
    }

    fun resetPassword(email: String) { // Hàm đặt lại mật khẩu, nhận tham số email
        viewModelScope.launch { // Sử dụng coroutine để gọi hàm resetPassword từ repository
            repository.resetPassword(email) { success, message -> // Gọi hàm resetPassword và xử lý callback
                _resetState.value = Pair(success, message) // Cập nhật trạng thái đặt lại mật khẩu dựa trên kết quả callback
            }
        }
    }

    fun loginWithMicrosoft(activity: Activity, onResult: (Boolean, String?) -> Unit) { // Hàm hỗ trợ đăng nhập bằng Microsoft, nhận tham số activity và callback onResult
        viewModelScope.launch { // Sử dụng coroutine để gọi hàm loginWithMicrosoft từ repository
            repository.loginWithMicrosoft(activity) { result -> // Gọi hàm loginWithMicrosoft và xử lý callback
                result.onSuccess { firebaseUser -> // Nếu đăng nhập thành công
                    _loginState.value = Pair(true, null) // Cập nhật trạng thái đăng nhập thành công
                    onResult(true, null) // Gọi callback onResult với kết quả thành công
                }.onFailure { exception -> // Nếu đăng nhập thất bại
                    _loginState.value = Pair(false, exception.message) // Cập nhật trạng thái đăng nhập thất bại và lỗi
                    onResult(false, exception.message) // Gọi callback onResult với kết quả thất bại
                }
            }
        }
    }
}