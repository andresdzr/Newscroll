package es.uc3m.android.newscroll.data

import es.uc3m.android.newscroll.model.NewsArticle

object FeedShuffler {

    /**
     * Arranges articles so no single category appears more than [maxConsecutive] times in a row.
     * Input should already be filtered to only unseen articles.
     */
    fun arrange(articles: List<NewsArticle>, maxConsecutive: Int = 3): List<NewsArticle> {
        val pool = articles.toMutableList().also { it.shuffle() }
        val result = mutableListOf<NewsArticle>()
        var lastCategory: String? = null
        var consecutive = 0

        while (pool.isNotEmpty()) {
            val pick = if (consecutive >= maxConsecutive && lastCategory != null) {
                pool.firstOrNull { it.category != lastCategory } ?: pool.first()
            } else {
                pool.random()
            }

            result.add(pick)
            pool.remove(pick)

            if (pick.category == lastCategory) {
                consecutive++
            } else {
                consecutive = 1
                lastCategory = pick.category
            }
        }

        return result
    }
}
