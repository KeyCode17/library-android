package com.library.android.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.library.android.domain.model.Book

/**
 * Room row for a cached catalog [Book] (ADR 0002 — Room, not Rust, for on-device data). Mirrors
 * the domain model 1:1; the entity stays a data-layer concern and never leaks into the domain.
 */
@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey val id: String,
    val title: String,
    val author: String,
    val isbn: String,
    val shelf: String,
    val row: Int,
    val available: Boolean,
)

/** Cache row -> domain. */
fun BookEntity.toDomain(): Book = Book(
    id = id,
    title = title,
    author = author,
    isbn = isbn,
    shelf = shelf,
    row = row,
    available = available,
)

/** Domain -> cache row. */
fun Book.toEntity(): BookEntity = BookEntity(
    id = id,
    title = title,
    author = author,
    isbn = isbn,
    shelf = shelf,
    row = row,
    available = available,
)
