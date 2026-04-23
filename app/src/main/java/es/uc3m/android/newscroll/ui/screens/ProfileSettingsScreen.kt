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

    val isLoggedIn = userViewModel.isLoggedIn.value

    if (isLoggedIn) {
        ProfileView(userViewModel)
    } else {
        AuthScreen(userViewModel)
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