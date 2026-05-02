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

    // Saved article IDs (reactive state)
    val savedIds by viewModel.savedArticles

    // Static list of articles (in-memory repository)
    val articles = remember { NewsRepository.articles }

    // Share bottom sheet state
    var selectedArticle by remember { mutableStateOf<NewsArticle?>(null) }
    val showShareSheet = selectedArticle != null

    // Context and location state
    val context = LocalContext.current
    val location by viewModel.userLocation.collectAsState()

    // Check if location permission is granted
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Launcher to request location permission
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
        if (granted) {
            viewModel.startLocationUpdates()
        }
    }

    // Initial setup: load saved articles and request location
    LaunchedEffect(Unit) {
        viewModel.loadSavedArticlesFromFirebase(userViewModel)

        if (!hasPermission) {
            launcher.launch(Manifest.permission.ACCESS_COARSE_LOCATION) // should match FINE permission
        } else {
            viewModel.startLocationUpdates()
        }
    }

    // Pager state for vertical scrolling
    val pagerState = rememberPagerState(
        pageCount = { articles.size }
    )

    // Debug location updates in UI layer
    LaunchedEffect(location) {
        if (location != null) {
            Log.d("LOCATION_DEBUG", "UI: ${location!!.latitude}, ${location!!.longitude}")
        } else {
            Log.d("LOCATION_DEBUG", "UI: location = null")
        }
    }

    // Main vertical feed (like TikTok style)
    VerticalPager(
        state = pagerState,
        modifier = modifier.fillMaxSize()
    ) { page ->

        val currentArticle = articles[page]

        NewsItem(
            modifier = Modifier,
            article = currentArticle,
            isSaved = currentArticle.id in savedIds,
            onToggleSaved = {
                viewModel.toggleSaved(currentArticle.id, userViewModel)
            },
            onShareClick = {
                selectedArticle = currentArticle
            }
        )
    }

    // Share bottom sheet
    if (showShareSheet) {
        ShareBottomSheet(
            article = selectedArticle!!,
            userViewModel = userViewModel,
            onDismiss = {
                selectedArticle = null
            }
        )
    }
}