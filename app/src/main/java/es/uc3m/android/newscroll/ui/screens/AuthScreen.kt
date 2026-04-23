package es.uc3m.android.newscroll.ui.screens

import androidx.compose.runtime.*
import es.uc3m.android.newscroll.viewmodel.UserViewModel

@Composable
fun AuthScreen(userViewModel: UserViewModel) {
    var showRegister by remember { mutableStateOf(false) }

    if (showRegister) {
        RegisterScreen(
            userViewModel = userViewModel,
            onBack = { showRegister = false }
        )
    } else {
        LoginScreen(
            userViewModel = userViewModel,
            onNavigateToRegister = { showRegister = true }
        )
    }
}