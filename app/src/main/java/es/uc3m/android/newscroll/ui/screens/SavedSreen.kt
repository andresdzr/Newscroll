package es.uc3m.android.newscroll.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import es.uc3m.android.newscroll.R
import es.uc3m.android.newscroll.model.NewsArticle
import es.uc3m.android.newscroll.viewmodel.NewsViewModel
import es.uc3m.android.newscroll.viewmodel.UserViewModel
import es.uc3m.android.newscroll.ui.components.SavedItem

@Composable
fun SavedScreen(
    viewModel: NewsViewModel,
    userViewModel: UserViewModel
) {
    var savedArticles by remember { mutableStateOf<List<NewsArticle>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Load full articles from Firestore when screen opens
    LaunchedEffect(Unit) {
        isLoading = true
        userViewModel.getSavedArticlesFull { articles ->
            savedArticles = articles
            isLoading = false
        }
    }

    when {
        isLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        savedArticles.isEmpty() -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = stringResource(R.string.no_saved_articles))
            }
        }

        else -> {
            // List of saved articles
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(savedArticles) { article ->

                    SavedItem(
                        article = article,
                        onToggleSaved = {
                            // Toggle saved state in ViewModel
                            viewModel.toggleSaved(article.id, userViewModel)
                        },
                    )
                }
            }
        }
    }
}
