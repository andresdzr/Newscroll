package es.uc3m.android.newscroll.data.remote

import es.uc3m.android.newscroll.data.remote.dto.GuardianResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GuardianApiService {
    @GET("search")
    suspend fun searchArticles(
        @Query("section") section: String,
        @Query("api-key") apiKey: String,
        @Query("page-size") pageSize: Int = 20,
        @Query("show-fields") showFields: String = "trailText,thumbnail"
    ): GuardianResponse
}
