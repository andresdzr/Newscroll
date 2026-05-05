package es.uc3m.android.newscroll.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FinnhubArticle(
    @Json(name = "id") val id: Long,
    @Json(name = "headline") val title: String,
    @Json(name = "url") val url: String,
    @Json(name = "datetime") val timestamp: Long,
    @Json(name = "image") val image: String?,
    @Json(name = "summary") val summary: String?
)
