package es.uc3m.android.newscroll.viewmodel

import android.app.Application
import android.content.Context
import android.location.Location
import android.location.LocationManager
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import es.uc3m.android.newscroll.model.NewsArticle

import android.util.Log
import kotlinx.coroutines.*
import androidx.lifecycle.viewModelScope

class NewsViewModel(application: Application) : AndroidViewModel(application) {

    // =========================
    // Saved Articles
    // =========================

    // Holds saved article IDs
    private val _savedArticles = mutableStateOf<Set<String>>(emptySet())
    val savedArticles: State<Set<String>> = _savedArticles

    // Loads saved articles from Firebase
    fun loadSavedArticlesFromFirebase(userViewModel: UserViewModel) {
        userViewModel.getSavedArticles { ids ->
            _savedArticles.value = ids.toSet()
        }
    }

    // Adds or removes an article from saved list
    fun toggleSaved(articleId: String, userViewModel: UserViewModel) {
        val current = _savedArticles.value

        if (current.contains(articleId)) {
            userViewModel.removeArticle(articleId)
            _savedArticles.value = current - articleId
        } else {
            userViewModel.saveArticle(articleId)
            _savedArticles.value = current + articleId
        }
    }

    // =========================
    // LOCATION SERVICES
    // =========================

    // System location service
    private val locationManager =
        application.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    // Current user location (reactive)
    private val _userLocation = MutableStateFlow<Location?>(null)
    val userLocation: StateFlow<Location?> = _userLocation

    // Listener reference to stop updates later
    private var listener: ((Location) -> Unit)? = null

    fun startLocationUpdates() {
        try {
            val provider = LocationManager.GPS_PROVIDER

            // Check if GPS is enabled
            val isEnabled = locationManager.isProviderEnabled(provider)
            Log.d("LOCATION_DEBUG", "GPS enabled: $isEnabled")

            if (!isEnabled) {
                Log.d("LOCATION_DEBUG", "GPS is disabled")
                return
            }

            // Get last known location (may be null)
            val lastLocation = locationManager.getLastKnownLocation(provider)
            _userLocation.value = lastLocation

            // Create listener to receive updates
            listener = { location ->
                _userLocation.value = location
            }

            // Request periodic updates
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                30000L,   // update every 30 seconds
                100f,     // only if user moved 100 meters
                listener!!
            )

            startLoggingLocation()

        } catch (e: SecurityException) {
            Log.e("LOCATION_DEBUG", "Location permission not granted", e)
        }
    }

    // Stop receiving location updates
    fun stopLocationUpdates() {
        listener?.let {
            locationManager.removeUpdates(it)
        }
    }

    // =========================
    // Debug logging
    // =========================

    private var loggingJob: Job? = null

    // Logs current location every 10 seconds
    fun startLoggingLocation() {
        loggingJob?.cancel()

        loggingJob = viewModelScope.launch {
            while (isActive) {
                val loc = _userLocation.value
                if (loc != null) {
                    Log.d("LOCATION_DEBUG", "Lat: ${loc.latitude}, Lon: ${loc.longitude}")
                } else {
                    Log.d("LOCATION_DEBUG", "Location = null")
                }
                delay(10000)
            }
        }
    }

    // =========================
    // Proximity filter
    // =========================

    // Returns articles within a given radius from user location
    fun filterNearby(
        articles: List<NewsArticle>,
        location: Location,
        radiusMeters: Float = 10000f // default 10 km
    ): List<NewsArticle> {
        return articles.filter { article ->

            val lat = article.latitude
            val lon = article.longitude

            // Skip articles without coordinates
            if (lat == null || lon == null) return@filter false

            val results = FloatArray(1)

            // Compute distance between user and article
            Location.distanceBetween(
                location.latitude,
                location.longitude,
                lat,
                lon,
                results
            )

            // Keep only articles inside radius
            results[0] <= radiusMeters
        }
    }
}