package es.uc3m.android.newscroll.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import es.uc3m.android.newscroll.model.NewsArticle

@Entity(tableName = "articles")
data class ArticleEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val articleUrl: String,
    val category: String,
    val summary: String?,
    val publishedDate: String?,
    val source: String?,
    val latitude: Double?,
    val longitude: Double?
)

fun ArticleEntity.toDomain(): NewsArticle = NewsArticle(
    id = id,
    title = title,
    description = description,
    imageUrl = imageUrl,
    articleUrl = articleUrl,
    category = category,
    summary = summary,
    publishedDate = publishedDate,
    source = source,
    latitude = latitude,
    longitude = longitude
)

fun NewsArticle.toEntity(): ArticleEntity = ArticleEntity(
    id = id,
    title = title,
    description = description,
    imageUrl = imageUrl,
    articleUrl = articleUrl,
    category = category ?: "",
    summary = summary,
    publishedDate = publishedDate,
    source = source,
    latitude = latitude,
    longitude = longitude
)
