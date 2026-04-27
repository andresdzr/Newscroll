package es.uc3m.android.newscroll.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import es.uc3m.android.newscroll.model.NewsArticle
import es.uc3m.android.newscroll.viewmodel.UserViewModel
import es.uc3m.android.newscroll.viewmodel.ChatPreview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareBottomSheet(
    article: NewsArticle,
    userViewModel: UserViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current

    var chats by remember { mutableStateOf<List<ChatPreview>>(emptyList()) }

    // 🔹 Cargar chats reales
    LaunchedEffect(Unit) {
        userViewModel.getChats {
            chats = it
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            // 🔹 Copiar enlace
            Button(
                onClick = {
                    clipboard.setText(AnnotatedString(article.articleUrl))
                    Toast.makeText(context, "Link copiado", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Copiar enlace")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 🔹 Compartir fuera de la app
            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, article.articleUrl)
                    }

                    context.startActivity(
                        Intent.createChooser(intent, "Compartir noticia")
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Compartir fuera de la app")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 🔹 Enviar a chats
            Text(
                text = "Enviar a:",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (chats.isEmpty()) {
                Text("No hay chats recientes")
            } else {

                chats.take(3).forEach { chat ->

                    Button(
                        onClick = {
                            userViewModel.sendArticleMessage(chat.chatId, article.id)

                            Toast.makeText(
                                context,
                                "Enviado a ${chat.username}",
                                Toast.LENGTH_SHORT
                            ).show()

                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(chat.username)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}