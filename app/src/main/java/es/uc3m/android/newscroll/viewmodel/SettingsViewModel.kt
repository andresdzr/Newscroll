package es.uc3m.android.newscroll.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

// ENUMS (Placed outside the class so they can be easily imported)
enum class LocationMode {
    LOCAL, NATIONAL, GLOBAL
}

enum class AppTheme {
    SYSTEM, LIGHT, DARK
}

class SettingsViewModel(private val prefs: SharedPreferences) : ViewModel() {

    companion object {
        // Keys for SharedPreferences
        private const val KEY_FILTERS = "settings_filters"
        private const val KEY_LOCATION_MODE = "settings_location_mode"
        private const val KEY_RADIUS = "settings_radius"
        private const val KEY_THEME = "settings_theme"
    }

    // THEME
    private val _appTheme = mutableStateOf(
        AppTheme.valueOf(prefs.getString(KEY_THEME, AppTheme.SYSTEM.name) ?: AppTheme.SYSTEM.name)
    )
    val appTheme: State<AppTheme> = _appTheme

    fun setTheme(theme: AppTheme) {
        _appTheme.value = theme
        prefs.edit().putString(KEY_THEME, theme.name).apply()
    }

    // FILTERS
    private val allFilters = setOf(
        "Technology, ML & AI",
        "Finance & Investing",
        "Politics & Geopolitics",
        "History & Philosophy",
        "Survival Knowledge"
    )

    private val _selectedFilters = mutableStateOf(
        prefs.getStringSet(KEY_FILTERS, allFilters) ?: allFilters
    )
    val selectedFilters: State<Set<String>> = _selectedFilters

    fun toggleFilter(filter: String) {
        val current = _selectedFilters.value
        val newSet = if (current.contains(filter)) {
            if (current.size > 1) current - filter else current
        } else {
            current + filter
        }

        _selectedFilters.value = newSet
        prefs.edit().putStringSet(KEY_FILTERS, newSet).apply()
    }

    // LOCATION
    private val _locationMode = mutableStateOf(
        LocationMode.valueOf(prefs.getString(KEY_LOCATION_MODE, LocationMode.GLOBAL.name) ?: LocationMode.GLOBAL.name)
    )
    val locationMode: State<LocationMode> = _locationMode

    private val _radiusKm = mutableFloatStateOf(
        prefs.getFloat(KEY_RADIUS, 25f)
    )
    val radiusKm: State<Float> = _radiusKm

    fun setLocationMode(mode: LocationMode) {
        if (_locationMode.value != mode) {
            _locationMode.value = mode
            prefs.edit().putString(KEY_LOCATION_MODE, mode.name).apply()
        }
    }

    fun setRadius(radius: Float) {
        if (_radiusKm.floatValue != radius) {
            _radiusKm.floatValue = radius
            prefs.edit().putFloat(KEY_RADIUS, radius).apply()
        }
    }
}

// FACTORY (Tells Android how to build the ViewModel with SharedPreferences)
class SettingsViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val prefs = context.getSharedPreferences("newscroll_prefs", Context.MODE_PRIVATE)
        @Suppress("UNCHECKED_CAST")
        return SettingsViewModel(prefs) as T
    }
}