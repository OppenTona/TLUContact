package com.example.tlucontact.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tlucontact.data.repository.ForgotPasswordPepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ForgotPasswordPepository() // Tạo một instance của AuthRepository với tham số application

    private val _emailError = MutableStateFlow(false) // Trạng thái lỗi email, mặc định là false
    val emailError = _emailError.asStateFlow() // Expose emailError như một StateFlow không mutable

    private val _resetState = MutableStateFlow<Pair<Boolean, String?>>(Pair(false, null)) // Trạng thái đặt lại mật khẩu
    val resetState = _resetState.asStateFlow() // Expose resetState như một StateFlow không mutable

    var email = MutableStateFlow("") // Khởi tạo MutableStateFlow để lưu giá trị email

    fun resetPassword(email: String) {  // Hàm xử lý việc đặt lại mật khẩu với tham số là email
        val emailValue = email.trim()  // Loại bỏ khoảng trắng thừa từ email người dùng nhập vào
        if (emailValue.isEmpty()) {  // Kiểm tra nếu email rỗng sau khi đã loại bỏ khoảng trắng
            _emailError.value = true  // Cập nhật trạng thái lỗi email thành true
            _resetState.value = Pair(false, "Vui lòng nhập email")  // Cập nhật trạng thái đặt lại mật khẩu với thông báo lỗi
            return  // Kết thúc hàm sớm nếu có lỗi
        } else {  // Trường hợp email không rỗng
            _emailError.value = false  // Đặt trạng thái lỗi email thành false
        }

        viewModelScope.launch {  // Khởi chạy coroutine trong phạm vi ViewModel
            repository.resetPassword(emailValue) { success, message ->  // Gọi hàm resetPassword từ repository với callback
                _resetState.value = Pair(success, message)  // Cập nhật trạng thái đặt lại mật khẩu với kết quả từ callback
            }
        }
    }
}