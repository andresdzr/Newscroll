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
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import android.content.Intent
import androidx.compose.ui.graphics.Brush
import coil.compose.rememberAsyncImagePainter
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.foundation.background
import androidx.compose.material.icons.filled.Share

@Composable
fun MessageItem(
    message: Message,
    isMe: Boolean,
    article: NewsArticle? = null,
    isSaved: Boolean = false, // external saved state (Firebase)
    onToggleSaved: () -> Unit = {} // external action handler
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (isMe)
            Arrangement.End // align right for current user
        else
            Arrangement.Start // align left for others
    ) {

        when (message.type) {

            // Text message bubble
            "text" -> {

                Surface(
                    color = if (isMe)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.large,
                    tonalElevation = if (isMe) 2.dp else 0.dp,
                    modifier = Modifier.widthIn(max = 280.dp)
                ) {
                    Text(
                        text = message.text ?: "",
                        modifier = Modifier.padding(12.dp),
                        color = if (isMe)
                            Color.White
                        else
                            MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Article preview (same pattern as feed)
            "article" -> {

                if (article != null) {

                    val context = LocalContext.current

                    Card(
                        modifier = Modifier.width(260.dp),
                        shape = MaterialTheme.shapes.medium
                    ) {

                        Box {

                            // Article image
                            Image(
                                painter = rememberAsyncImagePainter(article.imageUrl),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(160.dp)
                            )

                            // Gradient overlay for readability
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                Color.Black.copy(alpha = 0.7f)
                                            ),
                                            startY = 200f
                                        )
                                    )
                            )

                            // Bottom content (title + actions)
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(12.dp)
                            ) {

                                Text(
                                    text = article.title,
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodyLarge,
                                    maxLines = 2
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {

                                    // Open article in browser
                                    Button(
                                        onClick = {
                                            val url = article.articleUrl
                                            if (url.isNotBlank()) {
                                                try {
                                                    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                                                    context.startActivity(intent)
                                                } catch (_: Exception) {}
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color.White.copy(alpha = 0.9f)
                                        ),
                                        modifier = Modifier.height(36.dp)
                                    ) {
                                        Text("Read Full Article", color = Color.Black)
                                    }

                                    // Share and save actions
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {

                                        // Share article link
                                        IconButton(
                                            onClick = {
                                                val url = article.articleUrl
                                                if (url.isNotBlank()) {
                                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                                        type = "text/plain"
                                                        putExtra(Intent.EXTRA_TEXT, url)
                                                    }
                                                    context.startActivity(
                                                        Intent.createChooser(shareIntent, "Share via")
                                                    )
                                                }
                                            },
                                            colors = IconButtonDefaults.iconButtonColors(
                                                containerColor = Color.Black.copy(alpha = 0.3f)
                                            ),
                                            modifier = Modifier.size(40.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.Share,
                                                contentDescription = "Share",
                                                tint = Color.White
                                            )
                                        }

                                        // Save / unsave article
                                        IconButton(
                                            onClick = onToggleSaved,
                                            colors = IconButtonDefaults.iconButtonColors(
                                                containerColor = Color.Black.copy(alpha = 0.3f)
                                            ),
                                            modifier = Modifier.size(40.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (isSaved)
                                                    Icons.Filled.Favorite
                                                else
                                                    Icons.Outlined.FavoriteBorder,
                                                contentDescription = "Save",
                                                tint = if (isSaved) Color.Red else Color.LightGray
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}