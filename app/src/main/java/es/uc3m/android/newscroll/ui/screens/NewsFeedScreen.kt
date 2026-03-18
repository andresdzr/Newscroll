package es.uc3m.android.newscroll.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import es.uc3m.android.newscroll.model.NewsArticle
import es.uc3m.android.newscroll.ui.components.NewsItem
import es.uc3m.android.newscroll.viewmodel.NewsViewModel


@Composable
fun NewsFeedScreen(modifier: Modifier, viewModel: NewsViewModel) {
    // Dummy data, later this will take a list created dynamically
    val savedIds = viewModel.savedArticles.value
    val dummyArticles = listOf(
        NewsArticle(
            id = "1",
            title = "Android Studio Adds New AI Tools",
            description = "Google's latest update brings powerful new features to help developers write code faster.",
            imageUrl = "https://picsum.photos/seed/android/600/800",
            articleUrl = "https://developer.android.com/studio"
        ),
        NewsArticle(
            id = "2",
            title = "The Rise of Jetpack Compose",
            description = "More developers are switching to declarative UI for their Android applications.",
            imageUrl = "https://picsum.photos/seed/compose/600/800",
            articleUrl = "https://developer.android.com/compose"
        ),
        NewsArticle(
            id = "3",
            title = "Kotlin 2.0 is Here",
            description = "The new Kotlin compiler promises faster build times and better performance.",
            imageUrl = "https://picsum.photos/seed/kotlin/600/800",
            articleUrl = "https://kotlinlang.org/"
        )
    )

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