package es.uc3m.android.newscroll.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SavedArticlesViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private fun getCurrentUserId(): String = auth.currentUser?.uid ?: ""

    fun saveArticle(articleId: String) {
        val userId = getCurrentUserId()
        if (userId.isBlank()) return
        db.collection("users").document(userId).collection("saved_articles").document(articleId).set(mapOf("id" to articleId))
    }

    fun getSavedArticles(onResult: (List<String>) -> Unit) {
        val userId = getCurrentUserId()
        if (userId.isBlank()) return
        db.collection("users").document(userId).collection("saved_articles").get().addOnSuccessListener { result ->
            onResult(result.map { it.id })
        }
    }

    fun removeArticle(articleId: String) {
        val userId = getCurrentUserId()
        if (userId.isBlank()) return
        db.collection("users").document(userId).collection("saved_articles").document(articleId).delete()
    }
}
