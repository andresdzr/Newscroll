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
    // FAVORITOS (LO TUYO)
    // =========================
    private val _savedArticles = mutableStateOf<Set<String>>(emptySet())
    val savedArticles: State<Set<String>> = _savedArticles

    fun loadSavedArticlesFromFirebase(userViewModel: UserViewModel) {
        userViewModel.getSavedArticles { ids ->
            _savedArticles.value = ids.toSet()
        }
    }

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
    // LOCALIZACIÓN (NUEVO)
    // =========================

    private val locationManager =
        application.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private val _userLocation = MutableStateFlow<Location?>(null)
    val userLocation: StateFlow<Location?> = _userLocation

    fun startLocationUpdates() {
        try {
            val provider = LocationManager.NETWORK_PROVIDER

            val lastLocation = locationManager.getLastKnownLocation(provider)
            _userLocation.value = lastLocation

            locationManager.requestLocationUpdates(
                provider,
                5000L,
                50f
            ) { location ->
                _userLocation.value = location
            }

            startLoggingLocation()

        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    private var loggingJob: Job? = null

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
                delay(10000) // cada 10 segundos
            }
        }
    }

    // =========================
    // FILTRO POR PROXIMIDAD
    // =========================

    fun filterNearby(
        articles: List<NewsArticle>,
        location: Location,
        radiusMeters: Float = 10000f // 10 km
    ): List<NewsArticle> {
        return articles.filter { article ->

            val lat = article.latitude
            val lon = article.longitude

            // Ignorar artículos sin coordenadas
            if (lat == null || lon == null) return@filter false

            val results = FloatArray(1)

            Location.distanceBetween(
                location.latitude,
                location.longitude,
                lat,
                lon,
                results
            )

            results[0] <= radiusMeters
        }
    }
}