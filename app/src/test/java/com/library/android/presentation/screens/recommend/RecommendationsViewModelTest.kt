package com.library.android.presentation.screens.recommend

import com.library.android.MainDispatcherRule
import com.library.android.domain.model.Book
import com.library.android.domain.model.RecommendationPreferences
import com.library.android.domain.repo.BookLookup
import com.library.android.domain.repo.CatalogRepository
import com.library.android.domain.recommend.Recommender
import com.library.android.domain.usecase.GetBooksUseCase
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class RecommendationsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val bookA = Book("a", "Alpha", "Author A", "isbn-a", "R01", 1, true)
    private val bookB = Book("b", "Bravo", "Author B", "isbn-b", "R12", 2, true)

    /** Pure-Kotlin recommender (the test adapter): scores shelf+author matches, best-first. */
    private class FakeRecommender : Recommender {
        override suspend fun rank(
            preferences: RecommendationPreferences,
            candidates: List<Book>,
        ): Result<List<Book>> {
            val pool = if (preferences.availableOnly) candidates.filter { it.available } else candidates
            return Result.success(pool.sortedByDescending { score(it, preferences) })
        }

        private fun score(book: Book, prefs: RecommendationPreferences): Int =
            (if (book.shelf in prefs.preferredShelves) 2 else 0) +
                (if (book.author in prefs.preferredAuthors) 1 else 0)
    }

    private class FakeCatalogRepository(private val result: Result<List<Book>>) : CatalogRepository {
        override suspend fun getBooks(shelf: String?, row: Int?): Result<List<Book>> = result
        override suspend fun getBook(id: String): BookLookup = BookLookup.NotFound
        override suspend fun findByIsbn(isbn: String): Result<Book?> = Result.success(null)
    }

    private fun viewModel(catalog: Result<List<Book>>): RecommendationsViewModel =
        RecommendationsViewModel(
            getBooks = GetBooksUseCase(FakeCatalogRepository(catalog)),
            recommender = FakeRecommender(),
        )

    @Test
    fun `starts idle`() {
        val viewModel = viewModel(Result.success(listOf(bookA, bookB)))
        assertEquals(RecommendationsUiState.Idle, viewModel.state.value)
    }

    @Test
    fun `recommend ranks the preferred shelf first`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val viewModel = viewModel(Result.success(listOf(bookA, bookB)))

            viewModel.recommend(RecommendationPreferences(preferredShelves = listOf("R12")))
            advanceUntilIdle()

            val state = viewModel.state.value
            assertTrue("expected Loaded but was $state", state is RecommendationsUiState.Loaded)
            assertEquals(listOf(bookB, bookA), (state as RecommendationsUiState.Loaded).books)
        }

    @Test
    fun `recommend with no candidates is empty`() = runTest(mainDispatcherRule.testDispatcher) {
        val viewModel = viewModel(Result.success(emptyList()))

        viewModel.recommend(RecommendationPreferences())
        advanceUntilIdle()

        assertEquals(RecommendationsUiState.Empty, viewModel.state.value)
    }

    @Test
    fun `catalog failure yields error`() = runTest(mainDispatcherRule.testDispatcher) {
        val viewModel = viewModel(Result.failure(RuntimeException("offline")))

        viewModel.recommend(RecommendationPreferences())
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue("expected Error but was $state", state is RecommendationsUiState.Error)
        assertEquals("offline", (state as RecommendationsUiState.Error).message)
    }
}
