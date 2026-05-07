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
import androidx.compose.ui.res.stringResource

@Composable
fun ProfileSettingsScreen(userViewModel: UserViewModel) {

    val isLoggedIn = userViewModel.isLoggedIn.value

    // Show profile or auth screen depending on login state
    if (isLoggedIn) {
        ProfileView(userViewModel)
    } else {
        AuthScreen(userViewModel)
    }
}

@Composable
fun ProfileView(userViewModel: UserViewModel) {

    // Reactive user data
    val username by userViewModel.displayName
    val photoUrl by userViewModel.photoUrl

    // Local editable username state
    var newName by remember { mutableStateOf("") }

    // Sync input field with current username
    LaunchedEffect(username) {
        newName = username
    }

    // Reload profile when user logs in
    LaunchedEffect(userViewModel.isLoggedIn.value) {
        if (userViewModel.isLoggedIn.value) {
            userViewModel.loadUserProfile()
        }
    }

    // Image picker launcher for profile photo
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

        // Header with avatar and username
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Show default avatar if no photo exists
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

            // Display username
            Text(
                text = username,
                style = MaterialTheme.typography.titleLarge
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Profile editing card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Text(
                    text = stringResource(R.string.profile),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Username input field
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    placeholder = {
                        Text(stringResource(R.string.username))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Save updated username
                Button(
                    onClick = {
                        userViewModel.updateUsername(newName)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(stringResource(R.string.save_changes))
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Actions card (photo + logout)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                // Open image picker to change profile picture
                Button(
                    onClick = {
                        imagePickerLauncher.launch("image/*")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(stringResource(R.string.change_photo))
                }

                // Logout action
                OutlinedButton(
                    onClick = { userViewModel.logout() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(stringResource(R.string.logout))
                }
            }
        }
    }
}