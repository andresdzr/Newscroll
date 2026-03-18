package es.uc3m.android.newscroll.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel

class NewsViewModel : ViewModel() {


    private val _savedArticles = mutableStateOf<Set<String>>(emptySet())
    val savedArticles: State<Set<String>> = _savedArticles

    fun toggleSaved(articleId: String) {
        val current = _savedArticles.value

        _savedArticles.value =
            if (current.contains(articleId)) {
                current - articleId
            } else {
                current + articleId
            }
    }
}