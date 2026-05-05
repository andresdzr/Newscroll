package es.uc3m.android.newscroll.viewmodel

import android.app.Application
import android.content.Context
import android.location.Location
import android.location.LocationManager
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import es.uc3m.android.newscroll.data.NewsRepository
import es.uc3m.android.newscroll.model.NewsArticle
import android.util.Log
import kotlinx.coroutines.*

class NewsViewModel(application: Application) : AndroidViewModel(application) {

    // =========================
    // Articles feed
    // =========================

    private val _articles = MutableStateFlow<List<NewsArticle>>(emptyList())
    val articles: StateFlow<List<NewsArticle>> = _articles

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Fetch articles from APIs for the given filters and update the feed
    fun fetchNews(filters: Set<String>) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val fetched = NewsRepository.fetchFromApis(filters)
                _articles.value = fetched
            } catch (e: Exception) {
                // Keep showing current articles on error
            } finally {
                _isLoading.value = false
            }
        }
    }

    // =========================
    // Saved Articles
    // =========================

    private val _savedArticles = mutableStateOf<Set<String>>(emptySet())
    val savedArticles: State<Set<String>> = _savedArticles

    fun loadSavedArticlesFromFirebase(userViewModel: UserViewModel) {
        userViewModel.getSavedArticles { ids ->
            _savedArticles.value = ids.toSet()
        }
    }

    fun toggleSaved(article: NewsArticle, userViewModel: UserViewModel) {

        val current = _savedArticles.value

        if (current.contains(article.id)) {

            userViewModel.removeArticle(article.id)
            _savedArticles.value = current - article.id

        } else {

            userViewModel.saveArticle(article.id)
            _savedArticles.value = current + article.id
        }
    }

    // =========================
    // Location services
    // =========================

    private val locationManager =
        application.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private val _userLocation = MutableStateFlow<Location?>(null)
    val userLocation: StateFlow<Location?> = _userLocation

    private var locationListener: ((Location) -> Unit)? = null

    fun startLocationUpdates() {
        try {
            val provider = LocationManager.GPS_PROVIDER
            if (!locationManager.isProviderEnabled(provider)) return

            _userLocation.value = locationManager.getLastKnownLocation(provider)

            locationListener = { location -> _userLocation.value = location }

            locationManager.requestLocationUpdates(
                provider,
                30000L,
                100f,
                locationListener!!
            )
        } catch (e: SecurityException) {
            Log.e("NewsViewModel", "Location permission not granted", e)
        }
    }

    fun stopLocationUpdates() {
        locationListener?.let { locationManager.removeUpdates(it) }
    }

    // =========================
    // Proximity filter
    // =========================

    fun filterNearby(
        articles: List<NewsArticle>,
        location: Location,
        radiusMeters: Float = 10000f
    ): List<NewsArticle> {
        return articles.filter { article ->
            val lat = article.latitude ?: return@filter false
            val lon = article.longitude ?: return@filter false
            val results = FloatArray(1)
            Location.distanceBetween(location.latitude, location.longitude, lat, lon, results)
            results[0] <= radiusMeters
        }
    }
}
