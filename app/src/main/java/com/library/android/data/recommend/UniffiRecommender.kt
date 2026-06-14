package com.library.android.data.recommend

import com.library.android.domain.model.Book
import com.library.android.domain.model.RecommendationPreferences
import com.library.android.domain.recommend.Recommender
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import uniffi.recommender_ffi.CandidateBookDto
import uniffi.recommender_ffi.PreferencesDto
import uniffi.recommender_ffi.RecommenderEngine
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Production [Recommender] that runs the Rust recommender **on-device** through the UniFFI
 * binding (NOT the REST `/recommend` path). Ranking is CPU work, so it runs on
 * [Dispatchers.Default]. The native engine is created lazily on first use (so the native lib
 * loads only on a real device, never during Hilt graph creation or JVM tests).
 */
@Singleton
class UniffiRecommender @Inject constructor() : Recommender {

    private val engine: RecommenderEngine by lazy { RecommenderEngine() }

    override suspend fun rank(
        preferences: RecommendationPreferences,
        candidates: List<Book>,
    ): Result<List<Book>> = withContext(Dispatchers.Default) {
        runCatching {
            val prefs = PreferencesDto(
                preferredShelves = preferences.preferredShelves,
                preferredAuthors = preferences.preferredAuthors,
                availableOnly = preferences.availableOnly,
            )
            val candidateDtos = candidates.map {
                CandidateBookDto(id = it.id, shelf = it.shelf, author = it.author, available = it.available)
            }
            val rankedIds = engine.rank(prefs, candidateDtos)
            val booksById = candidates.associateBy { it.id }
            rankedIds.mapNotNull { booksById[it] }
        }
    }
}
