package es.uc3m.android.newscroll.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import es.uc3m.android.newscroll.viewmodel.UserViewModel
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.draw.clip
import coil.compose.AsyncImage
import es.uc3m.android.newscroll.R

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
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {

        // 🔹 HEADER (AVATAR + NAME)
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            if (photoUrl.isNullOrEmpty()) {
                Image(
                    painter = painterResource(id = R.drawable.default_avatar),
                    contentDescription = null,
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                )
            } else {
                AsyncImage(
                    model = photoUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = username,
                style = MaterialTheme.typography.titleLarge
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // 🔹 CARD PERFIL
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Text(
                    text = "Perfil",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    placeholder = { Text("Nombre de usuario") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        userViewModel.updateUsername(newName)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Guardar cambios")
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 🔹 CARD ACCIONES
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Button(
                    onClick = {
                        imagePickerLauncher.launch("image/*")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Cambiar foto")
                }

                OutlinedButton(
                    onClick = { userViewModel.logout() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Cerrar sesión")
                }
            }
        }
    }
}