package es.uc3m.android.newscroll.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import es.uc3m.android.newscroll.viewmodel.UserViewModel

@Composable
fun ProfileSettingsScreen(userViewModel: UserViewModel) {

    val isLoggedIn by userViewModel.isLoggedIn

    if (isLoggedIn) {
        ProfileView(userViewModel)
    } else {
        LoginView(userViewModel)
    }
}

@Composable
fun LoginView(userViewModel: UserViewModel) {

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Login",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                // Login simple (sin validación)
                userViewModel.login(
                    username.ifBlank { "User" }
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }
    }
}

@Composable
fun ProfileView(userViewModel: UserViewModel) {

    val username by userViewModel.username

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Profile",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Username: $username",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { userViewModel.logout() }
        ) {
            Text("Logout")
        }
    }
}