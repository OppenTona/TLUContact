package com.example.tlucontact.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.tlucontact.data.model.Guest
import com.example.tlucontact.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SignupViewModel(application: Application, private val state: SavedStateHandle) : AndroidViewModel(application) {
    private val repository = AuthRepository(application)

    private val _signupState = MutableStateFlow<Pair<Boolean, String?>>(Pair(false, null))
    val signupState = _signupState.asStateFlow()


    var name = state.getStateFlow("name", "")
    var phone = state.getStateFlow("phone", "")
    var email = state.getStateFlow("email", "")
    var password = state.getStateFlow("password", "")
    var confirmPassword = state.getStateFlow("confirmPassword", "")

    fun signup() {
        val guest = Guest(email.value, phone.value,name.value)
        viewModelScope.launch {
            repository.signup(guest, password.value, confirmPassword.value) { success, error ->
                _signupState.value = Pair(success, error)
                if (!success) {
                    Toast.makeText(getApplication(), "Signup Error: $error", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(getApplication(), "Please check your email for verification", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun onNameChange(newName: String) {
        state["name"] = newName
    }

    fun onPhoneChange(newPhone: String) {
        state["phone"] = newPhone
    }

    fun onEmailChange(newEmail: String) {
        state["email"] = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        state["password"] = newPassword
    }

    fun onConfirmPasswordChange(newConfirmPassword: String) {
        state["confirmPassword"] = newConfirmPassword
    }
}