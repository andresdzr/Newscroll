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

import android.location.Location
import android.location.LocationManager
import android.util.Log
import kotlinx.coroutines.*

@Composable
fun NewsFeedScreen(
    modifier: Modifier,
    viewModel: NewsViewModel,
    userViewModel: UserViewModel
) {

    val savedIds by viewModel.savedArticles
    val articles = remember { NewsRepository.articles }

    //  estado share
    var selectedArticle by remember { mutableStateOf<NewsArticle?>(null) }

    val showShareSheet = selectedArticle != null

    val context = LocalContext.current
    val location by viewModel.userLocation.collectAsState()

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
        if (granted) {
            viewModel.startLocationUpdates()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadSavedArticlesFromFirebase(userViewModel)

        if (!hasPermission) {
            launcher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
        } else {
            viewModel.startLocationUpdates()
        }
    }

    val pagerState = rememberPagerState(
        pageCount = { articles.size }
    )

    LaunchedEffect(location) {
        if (location != null) {
            Log.d("LOCATION_DEBUG", "UI: ${location!!.latitude}, ${location!!.longitude}")
        } else {
            Log.d("LOCATION_DEBUG", "UI: location = null")
        }
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
            onToggleSaved = {
                viewModel.toggleSaved(currentArticle.id, userViewModel)
            },
            onShareClick = {
                selectedArticle = currentArticle
            }
        )
    }

    // SHARE SHEET
    if (showShareSheet) {
        ShareBottomSheet(
            article = selectedArticle!!,
            userViewModel = userViewModel, // AÑADIR
            onDismiss = {
                selectedArticle = null
            }
        )
    }
}