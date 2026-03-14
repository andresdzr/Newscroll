package es.uc3m.android.newscroll.model

/**
 * Represents a single news item in the scrolling feed.
 */
data class NewsArticle(
    val id: String,             // A unique identifier
    val title: String,          // The headline of the news
    val description: String,    // A short summary
    val imageUrl: String,       // The link to the header image
    val articleUrl: String      // The link to the full news article on the web
)
