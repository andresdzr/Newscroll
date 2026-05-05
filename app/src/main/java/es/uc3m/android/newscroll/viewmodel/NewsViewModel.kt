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
import es.uc3m.android.newscroll.data.FeedShuffler
import es.uc3m.android.newscroll.data.NewsRepository
import es.uc3m.android.newscroll.model.NewsArticle
import android.util.Log
import kotlinx.coroutines.*

class NewsViewModel(application: Application) : AndroidViewModel(application) {

    // =========================
    // Articles feed
    // =========================

    private val prefs = application.getSharedPreferences("newscroll_feed_prefs", Context.MODE_PRIVATE)

    private val _articles = MutableStateFlow<List<NewsArticle>>(emptyList())
    val articles: StateFlow<List<NewsArticle>> = _articles

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private fun loadSeenIds(): MutableSet<String> =
        prefs.getStringSet("seen_article_ids", emptySet())?.toMutableSet() ?: mutableSetOf()

    private fun saveSeenIds(ids: Set<String>) {
        prefs.edit().putStringSet("seen_article_ids", ids).apply()
    }

    fun markSeen(articleId: String) {
        val seen = loadSeenIds()
        seen.add(articleId)
        saveSeenIds(seen)
    }

    // Fetch articles, filter unseen, shuffle with category interleaving
    fun fetchNews(filters: Set<String>) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val raw = NewsRepository.fetchFromApis(filters)
                val seen = loadSeenIds()
                val unseen = raw.filter { it.id !in seen }
                val pool = if (unseen.isEmpty()) {
                    // Cycled through everything — reset and start fresh
                    saveSeenIds(emptySet())
                    raw
                } else {
                    unseen
                }
                _articles.value = FeedShuffler.arrange(pool)
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
