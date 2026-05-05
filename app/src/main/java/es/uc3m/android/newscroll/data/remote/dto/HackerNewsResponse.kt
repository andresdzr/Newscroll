package es.uc3m.android.newscroll.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class HackerNewsResponse(
    @Json(name = "hits") val hits: List<HackerNewsHit>
)

@JsonClass(generateAdapter = true)
data class HackerNewsHit(
    @Json(name = "objectID") val id: String,
    @Json(name = "title") val title: String?,
    @Json(name = "url") val url: String?,
    @Json(name = "author") val author: String?,
    @Json(name = "created_at") val createdAt: String?
)
