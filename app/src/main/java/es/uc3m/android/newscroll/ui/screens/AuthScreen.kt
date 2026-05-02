package es.uc3m.android.newscroll.ui.screens

import androidx.compose.runtime.*
import es.uc3m.android.newscroll.viewmodel.UserViewModel

@Composable
fun AuthScreen(userViewModel: UserViewModel) {

    // Controls whether to show login or register screen
    var showRegister by remember { mutableStateOf(false) }

    if (showRegister) {
        // Show registration screen
        RegisterScreen(
            userViewModel = userViewModel,
            onBack = { showRegister = false }
        )
    } else {
        // Show login screen
        LoginScreen(
            userViewModel = userViewModel,
            onNavigateToRegister = { showRegister = true }
        )
    }
}