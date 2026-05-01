package es.uc3m.android.newscroll.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import es.uc3m.android.newscroll.viewmodel.UserViewModel
import es.uc3m.android.newscroll.viewmodel.ChatPreview
import es.uc3m.android.newscroll.ui.components.ChatItem
import es.uc3m.android.newscroll.R

@Composable
fun MessagesScreen(
    userViewModel: UserViewModel,
    navController: NavHostController
) {

    var chats by remember { mutableStateOf<List<ChatPreview>>(emptyList()) }

    LaunchedEffect(Unit) {
        userViewModel.getChats {
            chats = it
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp) // más aire lateral
    ) {

        // 🔹 HEADER REDISEÑADO
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Mensajes",
                style = MaterialTheme.typography.headlineSmall // más controlado que Medium
            )

            IconButton(
                onClick = {
                    navController.navigate("add_friend")
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.add_friend),
                    contentDescription = "Añadir amigo"
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // CONTENIDO
        if (chats.isEmpty()) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 40.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Text(
                    text = "No tienes chats",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

        } else {

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp), // separación entre items
                contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp)
            ) {
                items(chats) { chat ->

                    ChatItem(
                        username = chat.username,
                        photoUrl = chat.photoUrl,
                        onClick = {
                            navController.navigate("chat/${chat.chatId}")
                        }
                    )
                }
            }
        }
    }
}