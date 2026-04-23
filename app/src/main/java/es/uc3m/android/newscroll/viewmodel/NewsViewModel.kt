package es.uc3m.android.newscroll.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel

class NewsViewModel : ViewModel() {

    private val _savedArticles = mutableStateOf<Set<String>>(emptySet())
    val savedArticles: State<Set<String>> = _savedArticles

    // 🔄 CARGAR DESDE FIREBASE
    fun loadSavedArticlesFromFirebase(userViewModel: UserViewModel) {
        userViewModel.getSavedArticles { ids ->
            _savedArticles.value = ids.toSet()
        }
    }

    // 🔁 TOGGLE (conectado a Firebase)
    fun toggleSaved(articleId: String, userViewModel: UserViewModel) {

        val current = _savedArticles.value

        if (current.contains(articleId)) {
            // ❌ eliminar
            userViewModel.removeArticle(articleId)
            _savedArticles.value = current - articleId
        } else {
            // 💾 guardar
            userViewModel.saveArticle(articleId)
            _savedArticles.value = current + articleId
        }
    }
}