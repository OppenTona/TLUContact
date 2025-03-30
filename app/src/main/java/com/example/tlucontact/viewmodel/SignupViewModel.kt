package com.example.tlucontact.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tlucontact.data.model.User
import com.example.tlucontact.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SignupViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AuthRepository(application)

    private val _signupState = MutableStateFlow<Pair<Boolean, String?>>(Pair(false, null))
    val signupState = _signupState.asStateFlow()

    var phone = MutableStateFlow("")
    var email = MutableStateFlow("")
    var password = MutableStateFlow("")
    var confirmPassword = MutableStateFlow("")

    fun signup(user: User, password: String, confirmPassword: String) {
        viewModelScope.launch {
            repository.signup(user) { success, error ->
                _signupState.value = Pair(success, error)
            }
        }
    }
}