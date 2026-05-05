package es.uc3m.android.newscroll.data.remote

import android.util.Xml
import es.uc3m.android.newscroll.model.NewsArticle
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser
import java.io.InputStream

import java.util.regex.Pattern

class RssFeedParser(private val client: OkHttpClient) {

    private fun stripHtml(html: String): String {
        return Pattern.compile("<[^>]*>").matcher(html).replaceAll("").trim()
    }

    suspend fun fetchRssFeed(url: String, category: String, source: String = ""): List<NewsArticle> = withContext(Dispatchers.IO) {
        val request = Request.Builder().url(url).build()
        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext emptyList<NewsArticle>()
                val inputStream = response.body?.byteStream() ?: return@withContext emptyList<NewsArticle>()
                parseRss(inputStream, category, source)
            }
        } catch (e: Exception) {
            emptyList<NewsArticle>()
        }
    }

    private fun parseRss(inputStream: InputStream, category: String, source: String): List<NewsArticle> {
        val parser: XmlPullParser = Xml.newPullParser()
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
        parser.setInput(inputStream, null)

        val articles = mutableListOf<NewsArticle>()
        var eventType = parser.eventType
        var currentArticle: NewsArticle? = null
        var text = ""

        while (eventType != XmlPullParser.END_DOCUMENT) {
            val tagName = parser.name
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    if (tagName == "item") {
                        currentArticle = NewsArticle(
                            id = java.util.UUID.randomUUID().toString(),
                            title = "",
                            description = "",
                            imageUrl = "",
                            articleUrl = "",
                            category = category,
                            source = source.ifBlank { null }
                        )
                    } else if (currentArticle != null) {
                        if (tagName == "media:content" || tagName == "enclosure") {
                            val url = parser.getAttributeValue(null, "url")
                            if (url != null && currentArticle.imageUrl.isBlank()) {
                                currentArticle = currentArticle.copy(imageUrl = url)
                            }
                        }
                    }
                }
                XmlPullParser.TEXT -> {
                    text = parser.text
                }
                XmlPullParser.END_TAG -> {
                    if (currentArticle != null) {
                        when (tagName) {
                            "item" -> {
                                articles.add(currentArticle)
                                currentArticle = null
                            }
                            "title" -> currentArticle = currentArticle.copy(title = text)
                            "link" -> currentArticle = currentArticle.copy(articleUrl = text)
                            "description" -> currentArticle = currentArticle.copy(description = stripHtml(text), summary = stripHtml(text))
                            "pubDate" -> currentArticle = currentArticle.copy(publishedDate = text)
                        }
                    }
                }
            }
            eventType = parser.next()
        }
        return articles
    }
}
