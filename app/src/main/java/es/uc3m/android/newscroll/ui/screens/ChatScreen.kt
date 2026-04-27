package es.uc3m.android.newscroll.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import es.uc3m.android.newscroll.viewmodel.UserViewModel
import es.uc3m.android.newscroll.viewmodel.Message
import es.uc3m.android.newscroll.ui.components.MessageItem
import es.uc3m.android.newscroll.data.NewsRepository

@Composable
fun ChatScreen(
    userViewModel: UserViewModel,
    chatId: String
) {

    val currentUserId = userViewModel.getCurrentUserId()

    var messages by remember { mutableStateOf<List<Message>>(emptyList()) }
    var input by remember { mutableStateOf("") }

    // 🔹 escuchar mensajes en tiempo real
    LaunchedEffect(chatId) {
        userViewModel.listenMessages(chatId) {
            messages = it
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {

        Text(
            text = "Chat",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 🔹 MENSAJES
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {

            items(messages) { msg ->

                val isMe = msg.senderId == currentUserId

                val article = if (msg.articleId != null) {
                    NewsRepository.articles.find { it.id == msg.articleId }
                } else null

                MessageItem(
                    message = msg,
                    isMe = isMe,
                    article = article
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 🔹 INPUT
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Escribe un mensaje...") }
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    if (input.isNotBlank()) {
                        userViewModel.sendTextMessage(chatId, input)
                        input = ""
                    }
                }
            ) {
                Text("Enviar")
            }
        }
    }
}