package com.library.android.data.remote

import android.content.Context
import androidx.room.Room
import com.library.android.data.local.BookDao
import com.library.android.data.local.CatalogDatabase
import com.library.android.data.remote.dto.BookDto
import com.library.android.data.remote.dto.BookListDto
import com.library.android.data.remote.dto.PaginationDto
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import java.io.IOException

/** Offline-first behaviour of [CatalogRemoteRepository] over a real in-memory Room cache. */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class CatalogRepositoryOfflineTest {

    private lateinit var database: CatalogDatabase
    private lateinit var dao: BookDao

    private fun dto(id: String, title: String, author: String = "A", shelf: String = "R01", row: Int = 1) =
        BookDto(id, title, author, "978$id", shelf, row, available = true)

    private class FakeCatalogApi : CatalogApi {
        var fail = false
        var books: List<BookDto> = emptyList()
        var lastQuery: String? = null

        override suspend fun listBooks(
            page: Int,
            pageSize: Int,
            shelf: String?,
            row: Int?,
            isbn: String?,
            query: String?,
        ): BookListDto {
            if (fail) throw IOException("offline")
            lastQuery = query
            return BookListDto(books, PaginationDto(1, pageSize, books.size.toLong(), 1))
        }

        override suspend fun getBook(id: String): BookDto = books.first { it.id == id }
    }

    @Before
    fun setUp() {
        val context = RuntimeEnvironment.getApplication() as Context
        database = Room.inMemoryDatabaseBuilder(context, CatalogDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.bookDao()
    }

    @After
    fun tearDown() = database.close()

    private fun repository(api: CatalogApi) =
        CatalogRemoteRepository(api, dao, UnconfinedTestDispatcher())

    @Test
    fun `unfiltered load caches the catalog`() = runTest {
        val api = FakeCatalogApi().apply { books = listOf(dto("1", "Alpha"), dto("2", "Beta")) }
        val repo = repository(api)

        val fresh = repo.getBooks()

        assertEquals(2, fresh.getOrThrow().size)
        assertEquals(2, repo.cachedBooks().size) // mirrored into Room
    }

    @Test
    fun `offline falls back to the cached catalog`() = runTest {
        val api = FakeCatalogApi().apply { books = listOf(dto("1", "Alpha")) }
        val repo = repository(api)
        repo.getBooks() // warm the cache

        api.fail = true
        val result = repo.getBooks()

        assertTrue("network should fail", result.isFailure)
        assertEquals(1, repo.cachedBooks().size) // cache survives the offline read
    }

    @Test
    fun `offline cache applies the text filter in-memory`() = runTest {
        val api = FakeCatalogApi().apply {
            books = listOf(dto("1", "Pale Fire", author = "Nabokov"), dto("2", "Dune", author = "Herbert"))
        }
        val repo = repository(api)
        repo.getBooks() // cache the full catalog
        api.fail = true

        val hits = repo.cachedBooks(query = "pale")

        assertEquals(listOf("Pale Fire"), hits.map { it.title })
    }

    @Test
    fun `a filtered load does not evict the cached full catalog`() = runTest {
        val api = FakeCatalogApi().apply { books = listOf(dto("1", "Alpha"), dto("2", "Beta")) }
        val repo = repository(api)
        repo.getBooks() // full catalog cached (2)

        api.books = listOf(dto("1", "Alpha")) // server returns a filtered subset
        repo.getBooks(query = "alpha")

        assertEquals("alpha", api.lastQuery)
        assertEquals(2, repo.cachedBooks().size) // cache still holds the full catalog
    }
}
