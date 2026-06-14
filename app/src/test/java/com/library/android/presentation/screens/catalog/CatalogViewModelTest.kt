package com.library.android.presentation.screens.catalog

import com.library.android.MainDispatcherRule
import com.library.android.domain.model.Book
import com.library.android.domain.repo.BookLookup
import com.library.android.domain.repo.CatalogRepository
import com.library.android.domain.usecase.GetBooksUseCase
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

    /** Fake port — returns [booksResult] and records the last finder args it received. */
    private class FakeCatalogRepository : CatalogRepository {
        var booksResult: Result<List<Book>> = Result.success(emptyList())
        var lastShelf: String? = null
        var lastRow: Int? = null

        override suspend fun getBooks(shelf: String?, row: Int?): Result<List<Book>> {
            lastShelf = shelf
            lastRow = row
            return booksResult
        }

        override suspend fun getBook(id: String): BookLookup = BookLookup.NotFound
    }

    private fun viewModel(fake: FakeCatalogRepository): CatalogViewModel =
        CatalogViewModel(GetBooksUseCase(fake))

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
    fun `emits empty when the catalog is empty`() = runTest(mainDispatcherRule.testDispatcher) {
        val fake = FakeCatalogRepository().apply { booksResult = Result.success(emptyList()) }
        val viewModel = viewModel(fake)

        advanceUntilIdle()

        assertEquals(CatalogUiState.Empty, viewModel.state.value)
    }

    @Test
    fun `emits error with the failure message`() = runTest(mainDispatcherRule.testDispatcher) {
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
            // initial load has no filter
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
    fun `clearFinder resets the filter and reloads`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val fake = FakeCatalogRepository().apply { booksResult = Result.success(sampleBooks) }
            val viewModel = viewModel(fake)
            advanceUntilIdle()
            viewModel.applyFinder("R12", 3)
            advanceUntilIdle()

            viewModel.clearFinder()
            advanceUntilIdle()

            assertNull(fake.lastShelf)
            assertNull(fake.lastRow)
            assertTrue(!viewModel.filter.value.isActive)
        }
}
