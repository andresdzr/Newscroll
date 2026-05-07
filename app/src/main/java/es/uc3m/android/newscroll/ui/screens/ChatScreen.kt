package es.uc3m.android.newscroll.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import es.uc3m.android.newscroll.viewmodel.NewsViewModel
import es.uc3m.android.newscroll.viewmodel.UserViewModel
import es.uc3m.android.newscroll.viewmodel.Message
import es.uc3m.android.newscroll.ui.components.MessageItem
import androidx.compose.ui.res.painterResource
import es.uc3m.android.newscroll.R
import coil.compose.AsyncImage
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.layout.ContentScale

@Composable
fun ChatScreen(
    userViewModel: UserViewModel,
    newsViewModel: NewsViewModel,
    chatId: String
) {
    val feedArticles by newsViewModel.articles.collectAsState()

    // Current logged user ID
    val currentUserId = userViewModel.getCurrentUserId()

    // Messages state and input text
    var messages by remember { mutableStateOf<List<Message>>(emptyList()) }
    var input by remember { mutableStateOf("") }

    // Listen to messages in real time
    LaunchedEffect(chatId) {
        userViewModel.listenMessages(chatId) {
            messages = it
        }
    }

    // Chat user info (name + profile image)
    var chatUserName by remember { mutableStateOf("") }
    var chatPhotoUrl by remember { mutableStateOf("") }

    LaunchedEffect(chatId) {
        userViewModel.getChats { chats ->
            val chat = chats.find { it.chatId == chatId }
            if (chat != null) {
                chatUserName = chat.username
                chatPhotoUrl = chat.photoUrl
            }
        }
    }

    // Saved articles (used for like state)
    var savedArticles by remember { mutableStateOf<List<String>>(emptyList()) }

    LaunchedEffect(Unit) {
        userViewModel.getSavedArticles {
            savedArticles = it
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        // Top bar with chat info
        Surface(
            tonalElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                // User avatar
                AsyncImage(
                    model = if (chatPhotoUrl.isNotEmpty()) chatPhotoUrl else "https://via.placeholder.com/150",
                    contentDescription = null,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Username
                Text(
                    text = if (chatUserName.isNotEmpty()) chatUserName else "Loading...",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        // Messages list
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {

            items(messages) { msg ->

                // Check if message is from current user
                val isMe = msg.senderId == currentUserId

                // Resolve article if message contains one
                val article = if (msg.articleId != null) {
                    feedArticles.find { it.id == msg.articleId }
                } else null

                // Check if article is saved
                val isSaved = article?.id in savedArticles

                MessageItem(
                    message = msg,
                    isMe = isMe,
                    article = article,
                    isSaved = isSaved,
                    onToggleSaved = {
                        article?.let {
                            if (isSaved) {
                                userViewModel.removeArticle(it.id)
                            } else {
                                userViewModel.saveArticle(it.id)
                            }

                            // Refresh saved articles state
                            userViewModel.getSavedArticles {
                                savedArticles = it
                            }
                        }
                    }
                )
            }
        }

        // Input area for sending messages
        Surface(
            tonalElevation = 3.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                // Message input field
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Type a message...") },
                    shape = MaterialTheme.shapes.large
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Send message button
                IconButton(
                    onClick = {
                        if (input.isNotBlank()) {
                            userViewModel.sendTextMessage(chatId, input)
                            input = ""
                        }
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_send),
                        contentDescription = "Send"
                    )
                }
            }
        }
    }
}