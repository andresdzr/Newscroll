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
import es.uc3m.android.newscroll.viewmodel.NewsViewModel
import es.uc3m.android.newscroll.viewmodel.UserViewModel
import es.uc3m.android.newscroll.ui.components.SavedItem

@Composable
fun SavedScreen(
    viewModel: NewsViewModel,
    userViewModel: UserViewModel
) {

    // Reactive set of saved article IDs
    val savedIds by viewModel.savedArticles

    // All available articles from the live feed
    val allArticles by viewModel.articles.collectAsState()

    // Load saved articles from Firebase on start
    LaunchedEffect(Unit) {
        viewModel.loadSavedArticlesFromFirebase(userViewModel)
    }

    // Filter only saved articles
    val savedArticles = remember(savedIds, allArticles) {
        allArticles.filter { savedIds.contains(it.id) }
    }

    if (savedArticles.isEmpty()) {

        // Empty state
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = stringResource(R.string.no_saved_articles))
        }

    } else {

        // List of saved articles
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(savedArticles) { article ->

                SavedItem(
                    article = article,
                    onToggleSaved = {
                        // Toggle saved state in ViewModel
                        viewModel.toggleSaved(article, userViewModel)
                    },
                )
            }
        }
    }
}