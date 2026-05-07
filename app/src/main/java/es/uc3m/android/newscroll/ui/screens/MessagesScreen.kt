package es.uc3m.android.newscroll.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import es.uc3m.android.newscroll.viewmodel.UserViewModel
import es.uc3m.android.newscroll.viewmodel.ChatPreview
import es.uc3m.android.newscroll.ui.components.ChatItem
import androidx.compose.ui.res.stringResource
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


    Scaffold(

        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("add_friend") },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Friend"
                )
            }
        }

    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = stringResource(R.string.messages),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            // Content
            if (chats.isEmpty()) {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 80.dp),
                    contentAlignment = Alignment.TopCenter
                ) {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        Text(
                            text = stringResource(R.string.no_chats),
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            text = stringResource(R.string.start_adding_friends),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

            } else {

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        horizontal = 16.dp,
                        vertical = 8.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
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
}
