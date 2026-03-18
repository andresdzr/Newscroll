package es.uc3m.android.newscroll.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import es.uc3m.android.newscroll.data.NewsRepository
import es.uc3m.android.newscroll.model.NewsArticle
import es.uc3m.android.newscroll.viewmodel.NewsViewModel
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun SavedScreen(viewModel: NewsViewModel) {

    val savedIds by viewModel.savedArticles
    val allArticles = NewsRepository.articles

    // Filtrar artículos guardados
    val savedArticles = remember(savedIds, allArticles) {
        allArticles.filter { savedIds.contains(it.id) }
    }

    if (savedArticles.isEmpty()) {
        // Estado vacío
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No saved articles yet")
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(savedArticles) { article ->
                SavedItem(
                    article = article,
                    onToggleSaved = {
                        viewModel.toggleSaved(article.id)
                    },
                    onReadMore = {
                        // abrir URL
                    }
                )
            }
        }
    }
}

@Composable
fun SavedItem(
    article: NewsArticle,
    onToggleSaved: () -> Unit,
    onReadMore: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(8.dp),
        shape = MaterialTheme.shapes.medium
    ) {
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
                    contentDescription = "Remove",
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
                    onClick = onReadMore,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White.copy(alpha = 0.9f)
                    )
                ) {
                    Text("Read full article", color = Color.Black)
                }
            }
        }
    }
}