package es.uc3m.android.newscroll.ui.components

import androidx.compose.runtime.Stable

@Stable
data class NewsItemActions(
    val onToggleSaved: () -> Unit,
    val onShareClick: () -> Unit
)
