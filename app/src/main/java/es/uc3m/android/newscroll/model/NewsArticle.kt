package es.uc3m.android.newscroll.model

data class NewsArticle(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val articleUrl: String,
    val category: String? = null,
    val country: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val summary: String? = null,
    val publishedDate: String? = null,
    val source: String? = null,
)
