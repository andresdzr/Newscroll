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
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

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

    val username by userViewModel.displayName
    val photoUrl by userViewModel.photoUrl

    var newName by remember { mutableStateOf(username) }

    LaunchedEffect(Unit) {
        userViewModel.loadUserProfile()
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            userViewModel.uploadProfileImage(it)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Perfil",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (photoUrl != null && photoUrl!!.isNotEmpty()) {
            coil.compose.AsyncImage(
                model = photoUrl,
                contentDescription = null,
                modifier = Modifier.size(100.dp)
            )
        } else {
            Text("Sin foto")
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = newName,
            onValueChange = { newName = it },
            label = { Text("Nombre de usuario") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                userViewModel.updateUsername(newName)
            }
        ) {
            Text("Guardar nombre")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                imagePickerLauncher.launch("image/*")
            }
        ) {
            Text("Cambiar foto")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { userViewModel.logout() }
        ) {
            Text("Cerrar sesión")
        }
    }
}