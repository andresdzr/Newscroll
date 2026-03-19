package es.uc3m.android.newscroll.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import es.uc3m.android.newscroll.ui.components.NewsItem
import es.uc3m.android.newscroll.viewmodel.NewsViewModel
import es.uc3m.android.newscroll.data.NewsRepository

@Composable
fun NewsFeedScreen(modifier: Modifier, viewModel: NewsViewModel) {
    // Dummy data, later this will take a list created dynamically
    val savedIds = viewModel.savedArticles.value

    val dummyArticles = NewsRepository.articles

    // Remember the state of the pager (which page we are currently on)
    val pagerState = rememberPagerState(pageCount = { dummyArticles.size })

    // VerticalPager acts just like a TikTok feed
    VerticalPager(
        state = pagerState,
        modifier = modifier.fillMaxSize()
    ) { page ->
        // Take the article for the current page
        val currentArticle = dummyArticles[page]

        // Display it
        NewsItem(
            modifier = Modifier,
            article = currentArticle,
            isSaved = currentArticle.id in savedIds,
            onToggleSaved = {
                viewModel.toggleSaved(currentArticle.id)
            }
        )
    }
}