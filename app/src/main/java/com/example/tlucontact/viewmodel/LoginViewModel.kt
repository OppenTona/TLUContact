package com.example.tlucontact.viewmodel

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

    var email = MutableStateFlow("")
    var password = MutableStateFlow("")

    fun login(email: String, password: String) {
        viewModelScope.launch {
            repository.login(email, password) { success, error ->
                _loginState.value = Pair(success, error)
            }
        }
    }
}
