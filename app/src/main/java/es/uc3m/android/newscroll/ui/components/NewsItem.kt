package es.uc3m.android.newscroll.ui.components

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
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
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import es.uc3m.android.newscroll.R
import es.uc3m.android.newscroll.model.NewsArticle
import androidx.core.net.toUri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.foundation.shape.CircleShape

@Composable
fun NewsItem(
    modifier: Modifier,
    article: NewsArticle,
    isSaved: Boolean,
    onToggleSaved: () -> Unit
) {

    val context = LocalContext.current

    Box(modifier = modifier.fillMaxSize()) {

        // Background image
        AsyncImage(
            model = article.imageUrl,
            contentDescription = article.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                        startY = 500f
                    )
                )
        )

        // Content
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {

            Text(
                text = article.title,
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = article.description,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.LightGray
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                // Botón leer artículo
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
                ) {
                    Text(text = stringResource(R.string.read_full_article))
                }

                // Botón corazón
                IconButton(
                    onClick = onToggleSaved,
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = Color.Black.copy(alpha = 0.3f),
                            shape = CircleShape
                        )
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