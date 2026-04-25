package es.uc3m.android.newscroll.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import es.uc3m.android.newscroll.R
import es.uc3m.android.newscroll.viewmodel.UserViewModel
import es.uc3m.android.newscroll.viewmodel.UserPreview

@Composable
fun MessagesScreen(
    userViewModel: UserViewModel,
    navController: NavHostController
) {

    // estado correcto (ahora con datos completos)
    var friends by remember { mutableStateOf<List<UserPreview>>(emptyList()) }

    // cargar desde Firebase
    LaunchedEffect(Unit) {
        userViewModel.getChats {
            friends = it
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // HEADER
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Mensajes",
                style = MaterialTheme.typography.headlineMedium
            )

            IconButton(
                onClick = {
                    navController.navigate("add_friend")
                }
            ) {
                Text("+")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // LISTA
        if (friends.isEmpty()) {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No tienes chats")
            }

        } else {

            LazyColumn {
                items(friends) { user ->

                    ChatItem(
                        user = user,
                        onClick = {
                            // siguiente paso: abrir chat
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ChatItem(
    user: UserPreview,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // 🔹 FOTO REAL o fallback
        if (user.photoUrl.isNotEmpty()) {
            AsyncImage(
                model = user.photoUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.default_avatar),
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // USERNAME
        Text(
            text = user.username,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge
        )

        // Flecha
        Text(">")
    }
}