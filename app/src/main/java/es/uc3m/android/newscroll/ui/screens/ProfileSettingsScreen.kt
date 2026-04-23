package es.uc3m.android.newscroll.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import es.uc3m.android.newscroll.R
import es.uc3m.android.newscroll.viewmodel.UserViewModel
import es.uc3m.android.newscroll.ui.screens.AuthScreen

@Composable
fun ProfileSettingsScreen(userViewModel: UserViewModel) {

    val isLoggedIn by userViewModel.isLoggedIn

    if (userViewModel.isLoggedIn.value) {
        ProfileView(userViewModel)
    } else {
        AuthScreen(userViewModel)
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
            text = stringResource(id = R.string.login),
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text(text = stringResource(id = R.string.username)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(text = stringResource(id = R.string.password)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                userViewModel.login(username, password)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(id = R.string.login))
        }
        Button(
            onClick = {
                userViewModel.register(username, password)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
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
            text = stringResource(id = R.string.profile),
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
            Text(text = stringResource(id = R.string.logout))
        }
    }
}