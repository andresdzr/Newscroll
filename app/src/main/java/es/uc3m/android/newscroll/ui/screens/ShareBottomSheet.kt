package es.uc3m.android.newscroll.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import es.uc3m.android.newscroll.model.NewsArticle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareBottomSheet(
    article: NewsArticle,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current

    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            // Copiar enlace
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

            // Compartir fuera de la app
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

            // Sección chats
            Text(
                text = "Enviar a:",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            val recentChats = listOf<String>() // luego vendrá de Firebase

            if (recentChats.isEmpty()) {
                Text("No hay chats recientes")
            } else {
                recentChats.take(3).forEach { chat ->
                    Text(chat)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}