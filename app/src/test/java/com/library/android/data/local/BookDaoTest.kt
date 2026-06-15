package com.library.android.data.local

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

/** Room DAO tests (JVM/Robolectric, in-memory DB) for the catalog cache. */
@RunWith(RobolectricTestRunner::class)
class BookDaoTest {

    private lateinit var database: CatalogDatabase
    private lateinit var dao: BookDao

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

    private fun entity(id: String, title: String) =
        BookEntity(id, title, "Author $id", "978$id", "R0$id", id.toInt(), available = true)

    @Test
    fun `upsertAll then getAll returns rows ordered by title`() = runTest {
        dao.upsertAll(listOf(entity("2", "Zeta"), entity("1", "Alpha")))

        val all = dao.getAll()

        assertEquals(listOf("Alpha", "Zeta"), all.map { it.title })
    }

    @Test
    fun `replaceAll evicts the previous catalog`() = runTest {
        dao.upsertAll(listOf(entity("1", "Alpha"), entity("2", "Beta")))

        dao.replaceAll(listOf(entity("3", "Gamma")))

        assertEquals(listOf("Gamma"), dao.getAll().map { it.title })
    }

    @Test
    fun `observeAll emits the current cache`() = runTest {
        dao.replaceAll(listOf(entity("1", "Alpha")))

        val emitted = dao.observeAll().first()

        assertEquals(1, emitted.size)
        assertEquals("Alpha", emitted.first().title)
    }
}
