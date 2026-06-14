package com.library.android.presentation.screens.catalog

import com.library.android.MainDispatcherRule
import com.library.android.domain.model.Book
import com.library.android.domain.repo.CatalogRepository
import com.library.android.domain.usecase.GetBooksUseCase
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class CatalogViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    /** Fake port — returns whatever [result] is configured with, no network. */
    private class FakeCatalogRepository(private val result: Result<List<Book>>) : CatalogRepository {
        override suspend fun getBooks(): Result<List<Book>> = result
    }

    private fun viewModel(result: Result<List<Book>>): CatalogViewModel =
        CatalogViewModel(GetBooksUseCase(FakeCatalogRepository(result)))

    @Test
    fun `starts loading then emits content`() = runTest(mainDispatcherRule.testDispatcher) {
        val viewModel = viewModel(Result.success(sampleBooks))

        // init{} queued load on the test dispatcher — not run yet, so still Loading.
        assertEquals(CatalogUiState.Loading, viewModel.state.value)

        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue("expected Content but was $state", state is CatalogUiState.Content)
        assertEquals(sampleBooks, (state as CatalogUiState.Content).books)
    }

    @Test
    fun `emits empty when the catalog is empty`() = runTest(mainDispatcherRule.testDispatcher) {
        val viewModel = viewModel(Result.success(emptyList()))

        advanceUntilIdle()

        assertEquals(CatalogUiState.Empty, viewModel.state.value)
    }

    @Test
    fun `emits error with the failure message`() = runTest(mainDispatcherRule.testDispatcher) {
        val viewModel = viewModel(Result.failure(RuntimeException("network down")))

        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue("expected Error but was $state", state is CatalogUiState.Error)
        assertEquals("network down", (state as CatalogUiState.Error).message)
    }
}
