package com.library.android.presentation.screens.catalog

import com.library.android.MainDispatcherRule
import com.library.android.domain.model.Book
import com.library.android.domain.repo.BookLookup
import com.library.android.domain.repo.CatalogRepository
import com.library.android.domain.usecase.GetBooksUseCase
import com.library.android.domain.usecase.GetCachedBooksUseCase
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class CatalogViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    /** Fake port — returns [booksResult]/[cached] and records the last filter args it received. */
    private class FakeCatalogRepository : CatalogRepository {
        var booksResult: Result<List<Book>> = Result.success(emptyList())
        var cached: List<Book> = emptyList()
        var lastShelf: String? = null
        var lastRow: Int? = null
        var lastQuery: String? = null

        override suspend fun getBooks(shelf: String?, row: Int?, query: String?): Result<List<Book>> {
            lastShelf = shelf
            lastRow = row
            lastQuery = query
            return booksResult
        }

        override suspend fun cachedBooks(shelf: String?, row: Int?, query: String?): List<Book> = cached

        override suspend fun getBook(id: String): BookLookup = BookLookup.NotFound
        override suspend fun findByIsbn(isbn: String): Result<Book?> = Result.success(null)
    }

    private fun viewModel(fake: FakeCatalogRepository): CatalogViewModel =
        CatalogViewModel(GetBooksUseCase(fake), GetCachedBooksUseCase(fake))

    @Test
    fun `starts loading then emits content`() = runTest(mainDispatcherRule.testDispatcher) {
        val fake = FakeCatalogRepository().apply { booksResult = Result.success(sampleBooks) }
        val viewModel = viewModel(fake)

        assertEquals(CatalogUiState.Loading, viewModel.state.value)

        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue("expected Content but was $state", state is CatalogUiState.Content)
        assertEquals(sampleBooks, (state as CatalogUiState.Content).books)
    }

    @Test
    fun `serves the cache immediately before the network refresh`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val fresh = sampleBooks
            val fake = FakeCatalogRepository().apply {
                cached = listOf(sampleBooks.first())
                booksResult = Result.success(fresh)
            }
            val viewModel = viewModel(fake)

            advanceUntilIdle()

            // After reconciliation the fresh network result wins.
            assertEquals(fresh, (viewModel.state.value as CatalogUiState.Content).books)
        }

    @Test
    fun `offline with a warm cache keeps showing cached books`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val fake = FakeCatalogRepository().apply {
                cached = sampleBooks
                booksResult = Result.failure(RuntimeException("offline"))
            }
            val viewModel = viewModel(fake)

            advanceUntilIdle()

            val state = viewModel.state.value
            assertTrue("expected Content from cache but was $state", state is CatalogUiState.Content)
            assertEquals(sampleBooks, (state as CatalogUiState.Content).books)
        }

    @Test
    fun `emits empty when the catalog is empty`() = runTest(mainDispatcherRule.testDispatcher) {
        val fake = FakeCatalogRepository().apply { booksResult = Result.success(emptyList()) }
        val viewModel = viewModel(fake)

        advanceUntilIdle()

        assertEquals(CatalogUiState.Empty, viewModel.state.value)
    }

    @Test
    fun `emits error when offline with no cache`() = runTest(mainDispatcherRule.testDispatcher) {
        val fake = FakeCatalogRepository().apply {
            booksResult = Result.failure(RuntimeException("network down"))
        }
        val viewModel = viewModel(fake)

        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue("expected Error but was $state", state is CatalogUiState.Error)
        assertEquals("network down", (state as CatalogUiState.Error).message)
    }

    @Test
    fun `applyFinder forwards shelf and row and reloads filtered`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val fake = FakeCatalogRepository().apply { booksResult = Result.success(sampleBooks) }
            val viewModel = viewModel(fake)
            advanceUntilIdle()
            assertNull(fake.lastShelf)
            assertNull(fake.lastRow)

            fake.booksResult = Result.success(listOf(sampleBooks.first()))
            viewModel.applyFinder("R12", 3)
            advanceUntilIdle()

            assertEquals("R12", fake.lastShelf)
            assertEquals(3, fake.lastRow)
            assertEquals(CatalogFilter("R12", 3), viewModel.filter.value)
            assertEquals(
                listOf(sampleBooks.first()),
                (viewModel.state.value as CatalogUiState.Content).books,
            )
        }

    @Test
    fun `search forwards the q param and is combinable with the finder`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val fake = FakeCatalogRepository().apply { booksResult = Result.success(sampleBooks) }
            val viewModel = viewModel(fake)
            advanceUntilIdle()

            viewModel.applyFinder("R12", null)
            advanceUntilIdle()
            viewModel.search("pale fire")
            advanceUntilIdle()

            assertEquals("pale fire", fake.lastQuery)
            assertEquals("R12", fake.lastShelf) // finder preserved alongside the search
            assertEquals("pale fire", viewModel.filter.value.query)
        }

    @Test
    fun `search treats a blank query as no filter`() = runTest(mainDispatcherRule.testDispatcher) {
        val fake = FakeCatalogRepository().apply { booksResult = Result.success(sampleBooks) }
        val viewModel = viewModel(fake)
        advanceUntilIdle()

        viewModel.search("   ")
        advanceUntilIdle()

        assertNull(fake.lastQuery)
        assertNull(viewModel.filter.value.query)
    }

    @Test
    fun `applyFinder treats a blank shelf as no filter`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val fake = FakeCatalogRepository().apply { booksResult = Result.success(sampleBooks) }
            val viewModel = viewModel(fake)
            advanceUntilIdle()

            viewModel.applyFinder("   ", null)
            advanceUntilIdle()

            assertNull(fake.lastShelf)
            assertNull(fake.lastRow)
        }

    @Test
    fun `clearFinder resets every filter and reloads`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val fake = FakeCatalogRepository().apply { booksResult = Result.success(sampleBooks) }
            val viewModel = viewModel(fake)
            advanceUntilIdle()
            viewModel.applyFinder("R12", 3)
            viewModel.search("pale")
            advanceUntilIdle()

            viewModel.clearFinder()
            advanceUntilIdle()

            assertNull(fake.lastShelf)
            assertNull(fake.lastRow)
            assertNull(fake.lastQuery)
            assertTrue(!viewModel.filter.value.isActive)
        }
}
