package com.library.android.domain.model

/**
 * Domain model for a catalog book. Plain immutable data — independent of the wire DTO
 * (`data/remote/dto/BookDto`) and of any UI concern. Mapped from the DTO at the data layer.
 */
data class Book(
    val id: String,
    val title: String,
    val author: String,
    val isbn: String,
    val shelf: String,
    val row: Int,
    val available: Boolean,
)
