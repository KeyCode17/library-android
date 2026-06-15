package com.library.android.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

/** DAO for the cached catalog. Reads are `Flow`/`suspend`; the full catalog is replaced on refresh. */
@Dao
interface BookDao {
    @Query("SELECT * FROM books ORDER BY title")
    fun observeAll(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books ORDER BY title")
    suspend fun getAll(): List<BookEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(books: List<BookEntity>)

    @Query("DELETE FROM books")
    suspend fun clear()

    /** Atomically replaces the cached catalog with [books] (clear + insert in one transaction). */
    @Transaction
    suspend fun replaceAll(books: List<BookEntity>) {
        clear()
        upsertAll(books)
    }
}
