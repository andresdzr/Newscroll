package es.uc3m.android.newscroll.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GuardianResponse(
    @Json(name = "response") val response: GuardianResultWrapper
)

@JsonClass(generateAdapter = true)
data class GuardianResultWrapper(
    @Json(name = "results") val results: List<GuardianArticle>
)

@JsonClass(generateAdapter = true)
data class GuardianArticle(
    @Json(name = "id") val id: String,
    @Json(name = "webTitle") val title: String,
    @Json(name = "webUrl") val url: String,
    @Json(name = "webPublicationDate") val date: String,
    @Json(name = "sectionName") val category: String,
    @Json(name = "fields") val fields: GuardianFields?
)

@JsonClass(generateAdapter = true)
data class GuardianFields(
    @Json(name = "trailText") val trailText: String?,
    @Json(name = "thumbnail") val thumbnail: String?
)
