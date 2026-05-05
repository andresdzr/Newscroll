package es.uc3m.android.newscroll.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import es.uc3m.android.newscroll.BuildConfig
import es.uc3m.android.newscroll.data.remote.FinnhubApiService
import es.uc3m.android.newscroll.data.remote.GuardianApiService
import es.uc3m.android.newscroll.data.remote.HackerNewsApiService
import es.uc3m.android.newscroll.data.remote.RetrofitFactory
import es.uc3m.android.newscroll.data.remote.RssFeedParser
import es.uc3m.android.newscroll.model.NewsArticle
import kotlinx.coroutines.tasks.await

private const val TAG = "NewsRepository"

object NewsRepository {

    private val db = FirebaseFirestore.getInstance()
    private val rssParser = RssFeedParser(RetrofitFactory.okHttpClient)

    suspend fun fetchFromApis(filters: Set<String>): List<NewsArticle> {
        Log.d(TAG, "fetchFromApis called with filters: $filters")
        val result = mutableListOf<NewsArticle>()

        // --- HackerNews: Technology, ML & AI (no API key required) ---
        if (filters.isEmpty() || "Technology, ML & AI" in filters) {
            try {
                Log.d(TAG, "Fetching HackerNews...")
                val service = RetrofitFactory.createService(
                    HackerNewsApiService::class.java,
                    "https://hn.algolia.com/api/"
                )
                val hits = service.getLatestStories(20).hits
                    .filter { !it.title.isNullOrBlank() && !it.url.isNullOrBlank() }
                val fetched = hits.map { hit ->
                    NewsArticle(
                        id = "hn_${hit.id}",
                        title = hit.title!!,
                        description = "By ${hit.author ?: "unknown"} · Hacker News",
                        imageUrl = "https://picsum.photos/seed/hn${hit.id}/600/800",
                        articleUrl = hit.url!!,
                        category = "Technology, ML & AI",
                        publishedDate = hit.createdAt,
                        source = "Hacker News"
                    )
                }
                Log.d(TAG, "HackerNews: fetched ${fetched.size} articles")
                result.addAll(fetched)
                storeArticles(fetched)
            } catch (e: Exception) {
                Log.e(TAG, "HackerNews fetch failed: ${e.message}", e)
            }
        }

        // --- Guardian: Politics & Geopolitics (requires GUARDIAN_API_KEY in local.properties) ---
        if (filters.isEmpty() || "Politics & Geopolitics" in filters) {
            val key = BuildConfig.GUARDIAN_API_KEY
            if (key.isBlank()) {
                Log.w(TAG, "Guardian: skipped — add GUARDIAN_API_KEY to local.properties")
            } else {
                try {
                    Log.d(TAG, "Fetching Guardian...")
                    val service = RetrofitFactory.createService(
                        GuardianApiService::class.java,
                        "https://content.guardianapis.com/"
                    )
                    val articles = service.searchArticles(
                        section = "politics",
                        apiKey = key,
                        pageSize = 20
                    ).response.results.map { item ->
                        NewsArticle(
                            id = "guardian_${item.id.replace("/", "_")}",
                            title = item.title,
                            description = item.fields?.trailText ?: item.category,
                            imageUrl = item.fields?.thumbnail
                                ?: "https://picsum.photos/seed/guardian${item.id.hashCode()}/600/800",
                            articleUrl = item.url,
                            category = "Politics & Geopolitics",
                            publishedDate = item.date,
                            source = "The Guardian"
                        )
                    }
                    Log.d(TAG, "Guardian: fetched ${articles.size} articles")
                    result.addAll(articles)
                    storeArticles(articles)
                } catch (e: Exception) {
                    Log.e(TAG, "Guardian fetch failed: ${e.message}", e)
                }
            }
        }

        // --- Finnhub: Finance & Investing (requires FINNHUB_API_KEY in local.properties) ---
        if (filters.isEmpty() || "Finance & Investing" in filters) {
            val key = BuildConfig.FINNHUB_API_KEY
            if (key.isBlank()) {
                Log.w(TAG, "Finnhub: skipped — add FINNHUB_API_KEY to local.properties")
            } else {
                try {
                    Log.d(TAG, "Fetching Finnhub...")
                    val service = RetrofitFactory.createService(
                        FinnhubApiService::class.java,
                        "https://finnhub.io/api/"
                    )
                    val articles = service.getGeneralNews(
                        category = "general",
                        token = key
                    ).filter { it.title.isNotBlank() && it.url.isNotBlank() }
                        .take(20)
                        .map { item ->
                            NewsArticle(
                                id = "finnhub_${item.id}",
                                title = item.title,
                                description = item.summary ?: "Finance & investing news",
                                imageUrl = item.image?.takeIf { it.isNotBlank() }
                                    ?: "https://picsum.photos/seed/finnhub${item.id}/600/800",
                                articleUrl = item.url,
                                category = "Finance & Investing",
                                publishedDate = item.timestamp.toString(),
                                source = "Finnhub"
                            )
                        }
                    Log.d(TAG, "Finnhub: fetched ${articles.size} articles")
                    result.addAll(articles)
                    storeArticles(articles)
                } catch (e: Exception) {
                    Log.e(TAG, "Finnhub fetch failed: ${e.message}", e)
                }
            }
        }

        // --- RSS: History & Philosophy ---
        if (filters.isEmpty() || "History & Philosophy" in filters) {
            try {
                Log.d(TAG, "Fetching History & Philosophy RSS...")
                val items = rssParser.fetchRssFeed(
                    url = "https://aeon.co/feed.rss",
                    category = "History & Philosophy",
                    source = "Aeon"
                ).take(15).map { article ->
                    if (article.imageUrl.isBlank())
                        article.copy(imageUrl = "https://picsum.photos/seed/hist${article.id.hashCode()}/600/800")
                    else article
                }
                Log.d(TAG, "History RSS: fetched ${items.size} articles")
                result.addAll(items)
                storeArticles(items)
            } catch (e: Exception) {
                Log.e(TAG, "History RSS fetch failed: ${e.message}", e)
            }
        }

        // --- RSS: Survival Knowledge ---
        if (filters.isEmpty() || "Survival Knowledge" in filters) {
            try {
                Log.d(TAG, "Fetching Survival Knowledge RSS...")
                val items = rssParser.fetchRssFeed(
                    url = "https://www.artofmanliness.com/feed/",
                    category = "Survival Knowledge",
                    source = "Art of Manliness"
                ).take(15).map { article ->
                    if (article.imageUrl.isBlank())
                        article.copy(imageUrl = "https://picsum.photos/seed/surv${article.id.hashCode()}/600/800")
                    else article
                }
                Log.d(TAG, "Survival RSS: fetched ${items.size} articles")
                result.addAll(items)
                storeArticles(items)
            } catch (e: Exception) {
                Log.e(TAG, "Survival RSS fetch failed: ${e.message}", e)
            }
        }

        Log.d(TAG, "Total fetched: ${result.size} articles.")
        return result
    }

    // Stores each article as a flat document so saved IDs can be resolved in Phase 3

    private suspend fun storeArticles(articles: List<NewsArticle>) {

        val batch = db.batch()

        articles.forEach { article ->

            val ref = db.collection("articles").document(article.id)

            batch.set(ref, mapOf(
                "id" to article.id,
                "title" to article.title,
                "description" to article.description,
                "imageUrl" to article.imageUrl,
                "articleUrl" to article.articleUrl,
                "category" to (article.category ?: ""),
                "source" to (article.source ?: ""),
                "publishedDate" to (article.publishedDate ?: "")
            ))
        }

        batch.commit().await()

        Log.d(TAG, "Stored ${articles.size} articles (batch)")
    }
}
