package com.example.tlucontact.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.tlucontact.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.text.contains
import kotlin.text.endsWith
import kotlin.text.isEmpty
import kotlin.text.startsWith

class SignupViewModel(application: Application, private val state: SavedStateHandle) : AndroidViewModel(application) {
    private val repository = AuthRepository(application)

    private val _signupState = MutableStateFlow<Pair<Boolean, String?>>(Pair(false, null))
    val signupState = _signupState.asStateFlow()

    private val _isEmailInvalid = MutableStateFlow(true)
    val isEmailInvalid = _isEmailInvalid.asStateFlow()

    private val _emailError = MutableStateFlow(false)
    val emailError = _emailError.asStateFlow()

    private val _passwordError = MutableStateFlow(false)
    val passwordError = _passwordError.asStateFlow()

    private val _confirmPasswordError = MutableStateFlow(false)
    val confirmPasswordError = _confirmPasswordError.asStateFlow()

    private val _nameError = MutableStateFlow(false)
    val nameError = _nameError.asStateFlow()

    private val _phoneError = MutableStateFlow(false)
    val phoneError = _phoneError.asStateFlow()

    var name = state.getStateFlow("name", "")
    var phone = state.getStateFlow("phone", "")
    var email = state.getStateFlow("email", "")
    var password = state.getStateFlow("password", "")
    var confirmPassword = state.getStateFlow("confirmPassword", "")


    fun signup() {
        viewModelScope.launch {
            val errorMsg = validateInputs()
            if (errorMsg != null) {
                // Nếu có lỗi, cập nhật state và thoát hàm
                _signupState.value = Pair(false, errorMsg)
                return@launch
            }

            // Nếu không có lỗi (errorMsg == null), tiến hành signup
            repository.signup(
                email = email.value,
                password = password.value,
                name = name.value,
                phone = phone.value
            ) { success, error ->
                _signupState.value = Pair(success, error)
                if (!success) {
                    Toast.makeText(getApplication(), "Signup Error: $error", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(getApplication(), "Please check your email for verification", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun validateInputs(): String? {
        val emailValue = email.value.trim()
        val passwordValue = password.value
        val confirmPasswordValue = confirmPassword.value
        val nameValue = name.value.trim()
        val phoneValue = phone.value.trim()

        if (emailValue.isEmpty()){
            _emailError.value = true
            return "Vui lòng nhập email"
        }
        else{
            _emailError.value = false
        }

        if (passwordValue.isEmpty()){
            _passwordError.value = true
            return "Vui lòng nhập mật khẩu"
        }
        else{
            _passwordError.value = false
        }

        if (passwordValue.length < 6){
            _passwordError.value = true
            return "Mật khẩu phải có ít nhất 6 ký tự"
        }
        else{
            _passwordError.value = false
        }

        if (passwordValue.contains(emailValue)){
            _passwordError.value = true
            return "Mật khẩu không được chứa email"
        }
        else{
            _passwordError.value = false
        }

        if (passwordValue.contains(" ")){
            _passwordError.value = true
            return "Mật khẩu không được chứa khoảng trắng"
        }
        else{
            _passwordError.value = false
        }

        if (confirmPasswordValue.isEmpty()){
            _confirmPasswordError.value = true
            return "Vui lòng xác nhận mật khẩu"
        }
        else{
            _confirmPasswordError.value = false
        }

        if (confirmPasswordValue != passwordValue){
            _confirmPasswordError.value = true
            return "Mật khẩu xác nhận không khớp"
        }
        else{
            _confirmPasswordError.value = false
        }
        if (!(emailValue.endsWith("@tlu.edu.vn") || emailValue.endsWith("@e.tlu.edu.vn"))){
            if (nameValue.isEmpty()){
                _nameError.value = true
                return "Vui lòng nhập tên"
            }
            else{
                _nameError.value = false
            }

            if (phoneValue.isEmpty()){
                _phoneError.value = true
                return "Vui lòng nhập số điện thoại"
            }
            else{
                _phoneError.value = false
            }

            if (phoneValue.length < 10){
                _phoneError.value = true
                return "Số điện thoại không hợp lệ"
            }
            else{
                _phoneError.value = false
            }

            if (!phoneValue.startsWith("0")){
                _phoneError.value = true
                return "Số điện thoại không hợp lệ"
            }
            else{
                _phoneError.value = false
            }
        }
        return null
    }

    fun onNameChange(newName: String) {
        state["name"] = newName
    }

    fun onPhoneChange(newPhone: String) {
        state["phone"] = newPhone
    }

    fun onEmailChange(newEmail: String) {
        state["email"] = newEmail
        _isEmailInvalid.value = !newEmail.endsWith("@tlu.edu.vn") && !newEmail.endsWith("@e.tlu.edu.vn")
    }

    fun onPasswordChange(newPassword: String) {
        state["password"] = newPassword
    }

    fun onConfirmPasswordChange(newConfirmPassword: String) {
        state["confirmPassword"] = newConfirmPassword
    }
}