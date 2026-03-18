package es.uc3m.android.newscroll.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel

class UserViewModel : ViewModel() {
    private val _isLoggedIn = mutableStateOf(false)
    val isLoggedIn: State<Boolean> = _isLoggedIn

    private val _username = mutableStateOf("Guest User")
    val username: State<String> = _username

    fun login(name: String) {
        _username.value = name
        _isLoggedIn.value = true
    }

    fun logout() {
        _username.value = "Guest User"
        _isLoggedIn.value = false
    }
}