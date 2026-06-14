package com.library.android.domain.recommend

import com.library.android.domain.model.Book
import com.library.android.domain.model.RecommendationPreferences

/**
 * Port for ranking candidate books by user preferences. The production adapter runs the Rust
 * recommender **on-device** via the UniFFI binding; tests use a pure-Kotlin fake. Keeping it an
 * interface means the domain never references the FFI/native types.
 */
interface Recommender {
    /** Ranks [candidates] for [preferences], best-first. */
    suspend fun rank(
        preferences: RecommendationPreferences,
        candidates: List<Book>,
    ): Result<List<Book>>
}
