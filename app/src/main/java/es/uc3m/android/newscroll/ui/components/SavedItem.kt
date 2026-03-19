package es.uc3m.android.newscroll.ui.components

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.rememberAsyncImagePainter
import es.uc3m.android.newscroll.R
import es.uc3m.android.newscroll.model.NewsArticle

@Composable
fun SavedItem(
    article: NewsArticle,
    onToggleSaved: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(8.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        val context = LocalContext.current

        Box {

            // Imagen de fondo
            Image(
                painter = rememberAsyncImagePainter(article.imageUrl),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            //  Overlay oscuro
            Box(
                modifier = Modifier
                    .fillMaxSize()
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

            // Botón arriba derecha
            IconButton(
                onClick = onToggleSaved,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .background(
                        Color.Black.copy(alpha = 0.3f),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = stringResource(R.string.remove_saved),
                    tint = Color.Red
                )
            }

            // Contenido
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
            ) {

                // Título
                Text(
                    text = article.title,
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .background(
                            Color.Black.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Botón "Leer"
                Button(
                    onClick = {
                        val url = article.articleUrl

                        if (url.isNotBlank()) {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Log.e("NewsApp", "Cannot open URL: $url", e)
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White.copy(alpha = 0.9f)
                    )
                ) {
                    Text(stringResource(id = R.string.read_full_article), color = Color.Black)
                }
            }
        }
    }
}