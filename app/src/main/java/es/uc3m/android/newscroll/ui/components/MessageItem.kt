package es.uc3m.android.newscroll.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import es.uc3m.android.newscroll.viewmodel.Message
import es.uc3m.android.newscroll.model.NewsArticle

@Composable
fun MessageItem(
    message: Message,
    isMe: Boolean,
    article: NewsArticle? = null
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (isMe)
            Arrangement.End
        else
            Arrangement.Start
    ) {

        when (message.type) {

            // 🔹 MENSAJE DE TEXTO
            "text" -> {

                Surface(
                    color = if (isMe)
                        MaterialTheme.colorScheme.primary
                    else
                        Color.LightGray,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier
                        .widthIn(max = 280.dp) // 🔥 límite tipo WhatsApp
                ) {
                    Text(
                        text = message.text ?: "",
                        modifier = Modifier.padding(10.dp),
                        color = if (isMe) Color.White else Color.Black
                    )
                }
            }

            // 🔹 MENSAJE DE NOTICIA
            "article" -> {

                if (article != null) {

                    Box(
                        modifier = Modifier
                            .width(260.dp) // 🔥 tamaño controlado
                    ) {
                        SavedItem(
                            article = article,
                            onToggleSaved = {} // no aplica en chat
                        )
                    }

                } else {
                    // 🔴 fallback si no se encuentra el artículo
                    Surface(
                        color = Color.Gray,
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.widthIn(max = 250.dp)
                    ) {
                        Text(
                            text = "Artículo no disponible",
                            modifier = Modifier.padding(10.dp),
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}