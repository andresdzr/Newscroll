package es.uc3m.android.newscroll.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import es.uc3m.android.newscroll.viewmodel.UserViewModel
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape
import coil.compose.AsyncImage
import es.uc3m.android.newscroll.R
import androidx.compose.foundation.layout.fillMaxWidth

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

    var newName by remember { mutableStateOf("") }

    LaunchedEffect(username) {
        newName = username
    }

    LaunchedEffect(userViewModel.isLoggedIn.value) {
        if (userViewModel.isLoggedIn.value) {
            userViewModel.loadUserProfile()
        }
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
            text = "Perfil: $username",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))


        if (photoUrl.isNullOrEmpty()) {

            Image(
                painter = painterResource(id = R.drawable.default_avatar),
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
            )

        } else {

            AsyncImage(
                model = photoUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = newName,
            onValueChange = { newName = it },
            label = { Text("Nombre de usuario") },
            modifier = Modifier.fillMaxWidth()
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