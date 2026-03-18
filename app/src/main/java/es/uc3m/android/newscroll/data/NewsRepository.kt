package es.uc3m.android.newscroll.data
import es.uc3m.android.newscroll.model.NewsArticle

object NewsRepository {

    val articles = listOf(
        NewsArticle(
            id = "1",
            title = "Android Studio Adds New AI Tools",
            description = "Google's latest update brings powerful new features to help developers write code faster.",
            imageUrl = "https://picsum.photos/seed/android/600/800",
            articleUrl = "https://developer.android.com/studio"
        ),
        NewsArticle(
            id = "2",
            title = "The Rise of Jetpack Compose",
            description = "More developers are switching to declarative UI for their Android applications.",
            imageUrl = "https://picsum.photos/seed/compose/600/800",
            articleUrl = "https://developer.android.com/compose"
        ),
        NewsArticle(
            id = "3",
            title = "Kotlin 2.0 is Here",
            description = "The new Kotlin compiler promises faster build times and better performance.",
            imageUrl = "https://picsum.photos/seed/kotlin/600/800",
            articleUrl = "https://kotlinlang.org/"
        )
    )
}