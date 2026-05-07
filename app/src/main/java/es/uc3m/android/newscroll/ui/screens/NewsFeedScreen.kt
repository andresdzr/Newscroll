package es.uc3m.android.newscroll.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import es.uc3m.android.newscroll.ui.components.NewsItem
import es.uc3m.android.newscroll.viewmodel.NewsViewModel
import es.uc3m.android.newscroll.viewmodel.UserViewModel
import es.uc3m.android.newscroll.model.NewsArticle
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.ui.platform.LocalContext
import android.util.Log

@Composable
fun NewsFeedScreen(
    modifier: Modifier,
    viewModel: NewsViewModel,
    userViewModel: UserViewModel
) {
    val savedIds by viewModel.savedArticles
    val articles by viewModel.articles.collectAsState()

    var selectedArticle by remember { mutableStateOf<NewsArticle?>(null) }

    val context = LocalContext.current
    val location by viewModel.userLocation.collectAsState()

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
        if (granted) viewModel.startLocationUpdates()
    }

    LaunchedEffect(Unit) {
        viewModel.loadSavedArticlesFromFirebase(userViewModel)
        if (!hasPermission) {
            launcher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
        } else {
            viewModel.startLocationUpdates()
        }
    }

    LaunchedEffect(location) {
        if (location != null) {
            Log.d("LOCATION_DEBUG", "UI: ${location!!.latitude}, ${location!!.longitude}")
        }
    }

    val pagerState = rememberPagerState(pageCount = { articles.size })

    // Reset to first page when article list is replaced after a fetch
    LaunchedEffect(articles) {
        if (articles.isNotEmpty() && pagerState.currentPage >= articles.size) {
            pagerState.scrollToPage(0)
        }
    }

    // Mark as seen and trigger infinite scroll append when near the end
    LaunchedEffect(pagerState.currentPage) {
        if (articles.isEmpty()) return@LaunchedEffect
        val page = pagerState.currentPage
        if (page < articles.size) viewModel.markSeen(articles[page].id)
        if (page >= articles.size - 5) viewModel.appendNextCycle()
    }

    VerticalPager(
        state = pagerState,
        modifier = modifier.fillMaxSize()
    ) { page ->
        val currentArticle = articles[page]
        NewsItem(
            modifier = Modifier,
            article = currentArticle,
            isSaved = currentArticle.id in savedIds,
            onToggleSaved = { viewModel.toggleSaved(currentArticle.id, userViewModel) },
            onShareClick = { selectedArticle = currentArticle }
        )
    }

    if (selectedArticle != null) {
        ShareBottomSheet(
            article = selectedArticle!!,
            userViewModel = userViewModel,
            onDismiss = { selectedArticle = null }
        )
    }
}
