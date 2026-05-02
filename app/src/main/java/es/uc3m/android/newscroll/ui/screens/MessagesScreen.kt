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

    // List of chats (preview data)
    var chats by remember { mutableStateOf<List<ChatPreview>>(emptyList()) }

    // Load chats when screen is created
    LaunchedEffect(Unit) {
        userViewModel.getChats {
            chats = it
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {

        // Header with title and add friend button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Mensajes",
                style = MaterialTheme.typography.headlineSmall
            )

            // Navigate to add friend screen
            IconButton(
                onClick = {
                    navController.navigate("add_friend")
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.add_friend),
                    contentDescription = "Add friend"
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Main content
        if (chats.isEmpty()) {

            // Empty state
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

            // List of chat previews
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp)
            ) {
                items(chats) { chat ->

                    ChatItem(
                        username = chat.username,
                        photoUrl = chat.photoUrl,
                        onClick = {
                            // Navigate to chat screen
                            navController.navigate("chat/${chat.chatId}")
                        }
                    )
                }
            }
        }
    }
}