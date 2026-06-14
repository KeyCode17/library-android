package com.library.android.data.recommend

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.library.android.domain.model.Book
import com.library.android.domain.model.RecommendationPreferences
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

/**
 * Instrumented test for the REAL UniFFI recommender — loads the Android `.so` via JNA on a
 * device/emulator, so it is CI-only (connectedAndroidTest / Gradle Managed Device) and is NOT
 * part of the JVM pre-push gate (which uses a fake recommender).
 */
@RunWith(AndroidJUnit4::class)
class RecommenderInstrumentedTest {

    @Test
    fun ranksCandidatesOnDevice() = runBlocking {
        val recommender = UniffiRecommender()
        val preferred = UUID.randomUUID().toString()
        val other = UUID.randomUUID().toString()
        val candidates = listOf(
            Book(other, "Other", "Someone", "isbn-1", "R01", 1, true),
            Book(preferred, "Preferred", "Someone", "isbn-2", "R12", 2, true),
        )

        val result = recommender.rank(
            RecommendationPreferences(preferredShelves = listOf("R12")),
            candidates,
        )

        assertTrue("rank failed: ${result.exceptionOrNull()}", result.isSuccess)
        assertEquals(candidates.size, result.getOrThrow().size)
    }
}
