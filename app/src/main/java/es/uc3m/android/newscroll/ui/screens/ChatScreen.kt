package es.uc3m.android.newscroll.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.layout.ContentScale
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import es.uc3m.android.newscroll.viewmodel.NewsViewModel
import es.uc3m.android.newscroll.viewmodel.UserViewModel
import es.uc3m.android.newscroll.viewmodel.Message
import es.uc3m.android.newscroll.ui.components.MessageItem
import es.uc3m.android.newscroll.R
import androidx.compose.foundation.background
import androidx.compose.ui.text.font.FontWeight
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    userViewModel: UserViewModel,
    newsViewModel: NewsViewModel,
    chatId: String,
    navController: NavHostController
) {
    val feedArticles by newsViewModel.articles.collectAsState()

    val currentUserId = userViewModel.getCurrentUserId()

    var messages by remember { mutableStateOf<List<Message>>(emptyList()) }
    var input by remember { mutableStateOf("") }

    val listState = rememberLazyListState()

    // Listen messages
    LaunchedEffect(chatId) {
        userViewModel.listenMessages(chatId) {
            messages = it
        }
    }

    // Scroll to last message
    LaunchedEffect(messages) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    // Info user
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

    // Saved articles
    var savedArticles by remember { mutableStateOf<List<String>>(emptyList()) }

    LaunchedEffect(Unit) {
        userViewModel.getSavedArticles {
            savedArticles = it
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        // TOP BAR
        TopAppBar(
            windowInsets = WindowInsets(0),

            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    AsyncImage(
                        model = if (chatPhotoUrl.isNotEmpty()) chatPhotoUrl else "https://via.placeholder.com/150",
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp) // 🔹 más grande
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = if (chatUserName.isNotEmpty()) chatUserName else "Loading...",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            },

            navigationIcon = {
                Box(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(40.dp)
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "Back"
                        )
                    }
                }
            }
        )

        // Messages
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {

            items(messages) { msg ->

                val isMe = msg.senderId == currentUserId

                val article = if (msg.articleId != null) {
                    feedArticles.find { it.id == msg.articleId }
                } else null

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

                            userViewModel.getSavedArticles {
                                savedArticles = it
                            }
                        }
                    }
                )
            }
        }

        // INPUT BAR
        Surface(tonalElevation = 3.dp) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Type a message...") },
                    shape = MaterialTheme.shapes.large
                )

                Spacer(modifier = Modifier.width(8.dp))

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