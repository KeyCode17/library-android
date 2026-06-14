package com.library.android.presentation.screens.detail

import androidx.lifecycle.SavedStateHandle
import com.library.android.MainDispatcherRule
import com.library.android.domain.model.Book
import com.library.android.domain.repo.BookLookup
import com.library.android.domain.repo.CatalogRepository
import com.library.android.domain.usecase.GetBookUseCase
import com.library.android.presentation.nav.Routes
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class BookDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private class FakeCatalogRepository(private val lookup: BookLookup) : CatalogRepository {
        override suspend fun getBooks(shelf: String?, row: Int?): Result<List<Book>> =
            Result.success(emptyList())

        override suspend fun getBook(id: String): BookLookup = lookup
        override suspend fun findByIsbn(isbn: String): Result<Book?> = Result.success(null)
    }

    private fun viewModel(lookup: BookLookup, id: String = "1"): BookDetailViewModel =
        BookDetailViewModel(
            getBook = GetBookUseCase(FakeCatalogRepository(lookup)),
            savedStateHandle = SavedStateHandle(mapOf(Routes.ARG_BOOK_ID to id)),
        )

    @Test
    fun `starts loading then emits content`() = runTest(mainDispatcherRule.testDispatcher) {
        val book = Book("1", "Dune", "Frank Herbert", "9780441013593", "R07", 2, true)
        val viewModel = viewModel(BookLookup.Found(book))

        assertEquals(BookDetailUiState.Loading, viewModel.state.value)

        advanceUntilIdle()

        assertEquals(BookDetailUiState.Content(book), viewModel.state.value)
    }

    @Test
    fun `maps not found to NotFound`() = runTest(mainDispatcherRule.testDispatcher) {
        val viewModel = viewModel(BookLookup.NotFound)

        advanceUntilIdle()

        assertEquals(BookDetailUiState.NotFound, viewModel.state.value)
    }

    @Test
    fun `maps failure to Error with message`() = runTest(mainDispatcherRule.testDispatcher) {
        val viewModel = viewModel(BookLookup.Failed("Request failed (500)"))

        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue("expected Error but was $state", state is BookDetailUiState.Error)
        assertEquals("Request failed (500)", (state as BookDetailUiState.Error).message)
    }
}
