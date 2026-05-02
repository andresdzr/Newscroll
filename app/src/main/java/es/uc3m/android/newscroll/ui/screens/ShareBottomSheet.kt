package es.uc3m.android.newscroll.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import es.uc3m.android.newscroll.R
import es.uc3m.android.newscroll.model.NewsArticle
import es.uc3m.android.newscroll.viewmodel.UserViewModel
import es.uc3m.android.newscroll.viewmodel.ChatPreview
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.Icons

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareBottomSheet(
    article: NewsArticle,
    userViewModel: UserViewModel,
    onDismiss: () -> Unit
) {

    // Context and clipboard for system actions
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current

    // List of user chats
    var chats by remember { mutableStateOf<List<ChatPreview>>(emptyList()) }

    // Load chats when bottom sheet appears
    LaunchedEffect(Unit) {
        userViewModel.getChats {
            chats = it
        }
    }

    // Modal bottom sheet container
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {

            // Header title
            Text(
                text = "Compartir",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Top actions (copy link + external share)
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // Copy article URL to clipboard
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            clipboard.setText(AnnotatedString(article.articleUrl))
                            Toast.makeText(context, "Link copiado", Toast.LENGTH_SHORT).show()
                        }
                ) {

                    Surface(
                        shape = CircleShape,
                        tonalElevation = 2.dp,
                        modifier = Modifier.size(56.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.copy),
                            contentDescription = "Copy",
                            modifier = Modifier.padding(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text("Copiar")
                }

                // Share article externally using Android intent
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, article.articleUrl)
                            }

                            context.startActivity(
                                Intent.createChooser(intent, "Compartir noticia")
                            )
                        }
                ) {

                    Surface(
                        shape = CircleShape,
                        tonalElevation = 2.dp,
                        modifier = Modifier.size(56.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Share,
                            contentDescription = "Share",
                            modifier = Modifier.padding(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text("Compartir")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Section title for chat sharing
            Text(
                text = "Enviar a",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (chats.isEmpty()) {

                // Empty chat list state
                Text(
                    text = "No hay chats recientes",
                    style = MaterialTheme.typography.bodyMedium
                )

            } else {

                // Show up to 6 recent chats
                chats.take(6).forEach { chat ->

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {

                                // Send article message to selected chat
                                userViewModel.sendArticleMessage(chat.chatId, article.id)

                                Toast.makeText(
                                    context,
                                    "Enviado a ${chat.username}",
                                    Toast.LENGTH_SHORT
                                ).show()

                                onDismiss()
                            }
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        // Chat user avatar
                        AsyncImage(
                            model = if (chat.photoUrl.isNotEmpty()) chat.photoUrl
                            else "https://via.placeholder.com/150",
                            contentDescription = null,
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        // Chat username
                        Text(
                            text = chat.username,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}