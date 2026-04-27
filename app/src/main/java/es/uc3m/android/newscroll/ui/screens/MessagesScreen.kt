package es.uc3m.android.newscroll.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import es.uc3m.android.newscroll.viewmodel.UserViewModel
import es.uc3m.android.newscroll.viewmodel.ChatPreview
import es.uc3m.android.newscroll.ui.components.ChatItem

@Composable
fun MessagesScreen(
    userViewModel: UserViewModel,
    navController: NavHostController
) {

    // 🔹 SOLO chats (correcto)
    var chats by remember { mutableStateOf<List<ChatPreview>>(emptyList()) }

    // 🔹 cargar chats
    LaunchedEffect(Unit) {
        userViewModel.getChats {
            chats = it
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // 🔹 HEADER
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

        // 🔹 LISTA DE CHATS
        if (chats.isEmpty()) {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No tienes chats")
            }

        } else {

            LazyColumn {
                items(chats) { chat ->

                    ChatItem(
                        username = chat.username,
                        photoUrl = chat.photoUrl,
                        onClick = {
                            // navegación
                            navController.navigate("chat/${chat.chatId}")
                        }
                    )
                }
            }
        }
    }
}