package es.uc3m.android.newscroll.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import es.uc3m.android.newscroll.model.NewsArticle
import es.uc3m.android.newscroll.ui.components.NewsItem

@Composable
fun SavedScreen(modifier: Modifier = Modifier) {

    val savedArticles = listOf(
        NewsArticle(
            id = "1",
            title = "Saved Article 1",
            description = "Example saved article",
            imageUrl = "",
            articleUrl = ""
        ),
        NewsArticle(
            id = "2",
            title = "Saved Article 2",
            description = "Example saved article",
            imageUrl = "",
            articleUrl = ""
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Saved Articles",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(savedArticles) { article ->
                NewsItem(
                    modifier = Modifier,
                    article = article,
                    isSaved = true,
                    onToggleSaved = {}
                )
            }
        }
    }
}