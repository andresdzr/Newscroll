package es.uc3m.android.newscroll.ui.components

import android.content.Intent
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

@Composable
fun NewsItem(article: NewsArticle, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    Box(modifier = modifier.fillMaxSize()) {

        // Background image
        AsyncImage(
            model = article.imageUrl,
            contentDescription = article.title,
            contentScale = ContentScale.Crop, // Crop the image to fill the whole screen
            modifier = Modifier.fillMaxSize()
        )

        // Dark gradient overlay at the bottom to make sure text is readable
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

        // Title, description and full article link
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

            Spacer(modifier = Modifier.height(8.dp)) // Add small vertical margin

            Text(
                text = article.description,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.LightGray
            )

            Spacer(modifier = Modifier.height(16.dp)) // Add small vertical margin

            Button(
                onClick = {
                    // Open the full article on the browser
                    val intent = Intent(Intent.ACTION_VIEW, article.articleUrl.toUri())
                    context.startActivity(intent)
                }
            ) {
                Text(text = stringResource(R.string.read_full_article))
            }
        }
    }
}