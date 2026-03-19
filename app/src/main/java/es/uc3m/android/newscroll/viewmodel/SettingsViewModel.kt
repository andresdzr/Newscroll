package es.uc3m.android.newscroll.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel


enum class LocationMode {
    LOCAL, NATIONAL, GLOBAL
}

class SettingsViewModel : ViewModel() {

    // -------------------------
    // FILTERS
    // -------------------------
    private val allFilters = setOf(
        "Technology, ML & AI",
        "Finance & Investing",
        "Politics & Geopolitics",
        "History & Philosophy",
        "Survival Knowledge"
    )

    private val _selectedFilters = mutableStateOf<Set<String>>(allFilters)
    val selectedFilters: State<Set<String>> = _selectedFilters

    fun toggleFilter(filter: String) {
        val current = _selectedFilters.value

        _selectedFilters.value =
            if (current.contains(filter)) {
                if (current.size > 1) current - filter else current
            } else {
                current + filter
            }
    }

    // -------------------------
    // LOCATION
    // -------------------------
    private val _locationMode = mutableStateOf<LocationMode>(LocationMode.GLOBAL)
    val locationMode: State<LocationMode> = _locationMode

    private val _radiusKm = mutableFloatStateOf(25f)
    val radiusKm: State<Float> = _radiusKm

    fun setLocationMode(mode: LocationMode) {
        if (_locationMode.value != mode) {
            _locationMode.value = mode
        }
    }

    fun setRadius(radius: Float) {
        if (_radiusKm.floatValue != radius) {
            _radiusKm.floatValue = radius
        }
    }
}