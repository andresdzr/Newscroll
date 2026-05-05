package es.uc3m.android.newscroll.data.remote

import es.uc3m.android.newscroll.data.remote.dto.FinnhubArticle
import retrofit2.http.GET
import retrofit2.http.Query

interface FinnhubApiService {
    @GET("v1/news")
    suspend fun getGeneralNews(
        @Query("category") category: String = "general",
        @Query("token") token: String
    ): List<FinnhubArticle>
}
