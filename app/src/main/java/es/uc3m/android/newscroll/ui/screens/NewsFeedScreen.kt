package es.uc3m.android.newscroll.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import es.uc3m.android.newscroll.ui.components.NewsItem
import es.uc3m.android.newscroll.viewmodel.NewsViewModel
import es.uc3m.android.newscroll.viewmodel.UserViewModel
import es.uc3m.android.newscroll.data.NewsRepository

@Composable
fun NewsFeedScreen(
    modifier: Modifier,
    viewModel: NewsViewModel,
    userViewModel: UserViewModel
) {

    val savedIds by viewModel.savedArticles
    val dummyArticles = NewsRepository.articles

    // 🔴 Cargar artículos guardados al entrar
    LaunchedEffect(Unit) {
        viewModel.loadSavedArticlesFromFirebase(userViewModel)
    }

    val pagerState = rememberPagerState(pageCount = { dummyArticles.size })

    VerticalPager(
        state = pagerState,
        modifier = modifier.fillMaxSize()
    ) { page ->

        val currentArticle = dummyArticles[page]

        NewsItem(
            modifier = Modifier,
            article = currentArticle,
            isSaved = currentArticle.id in savedIds,
            onToggleSaved = {
                viewModel.toggleSaved(currentArticle.id, userViewModel)
            }
        )
    }
}