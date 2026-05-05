package es.uc3m.android.newscroll.data.remote

import es.uc3m.android.newscroll.data.remote.dto.HackerNewsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface HackerNewsApiService {
    @GET("v1/search_by_date?tags=story")
    suspend fun getLatestStories(
        @Query("hitsPerPage") hitsPerPage: Int = 20
    ): HackerNewsResponse
}
