package es.uc3m.android.newscroll.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()



    var isLoggedIn = mutableStateOf(auth.currentUser != null)
        private set

    var username = mutableStateOf(auth.currentUser?.email ?: "")
        private set

    fun login(email: String, password: String, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                username.value = auth.currentUser?.email ?: ""
                isLoggedIn.value = true
            } catch (e: Exception) {
                onError("Usuario o contraseña incorrectos")
            }
        }
    }

    fun register(email: String, password: String, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                auth.createUserWithEmailAndPassword(email, password).await()
                username.value = auth.currentUser?.email ?: ""
                isLoggedIn.value = true
            } catch (e: Exception) {
                onError("No se pudo crear la cuenta")
            }
        }
    }

    fun logout() {
        auth.signOut()
        isLoggedIn.value = false
        username.value = ""
    }
}