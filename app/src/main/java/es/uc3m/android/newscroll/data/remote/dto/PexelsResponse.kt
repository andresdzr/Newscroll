package es.uc3m.android.newscroll.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PexelsResponse(
    @Json(name = "photos") val photos: List<PexelsPhoto>
)

@JsonClass(generateAdapter = true)
data class PexelsPhoto(
    @Json(name = "src") val src: PexelsSrc
)

@JsonClass(generateAdapter = true)
data class PexelsSrc(
    @Json(name = "medium") val medium: String,
    @Json(name = "large") val large: String
)
