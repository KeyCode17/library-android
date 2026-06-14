package com.library.android.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Wire DTOs mirrored **exactly** from the single source of truth:
 * `library-backend/contract/openapi.yaml` (components.schemas). Field names, types and
 * nullability match the contract — do not rename or invent fields (anti-drift).
 *
 * snake_case wire keys (`page_size`, `total_pages`) are remapped with [SerialName]; the
 * Kotlin properties stay idiomatic camelCase.
 */
@Serializable
data class BookDto(
    val id: String,        // format: uuid
    val title: String,
    val author: String,
    val isbn: String,
    val shelf: String,
    val row: Int,          // int32
    val available: Boolean,
)

@Serializable
data class PaginationDto(
    val page: Int,                              // int32
    @SerialName("page_size") val pageSize: Int, // int32
    val total: Long,                            // int64
    @SerialName("total_pages") val totalPages: Int, // int32
)

@Serializable
data class BookListDto(
    val data: List<BookDto>,
    val pagination: PaginationDto,
)

/** Flat error body returned by 4xx/5xx responses (contract: components.schemas.Error). */
@Serializable
data class ErrorDto(
    val code: String,     // stable machine-readable code, e.g. "not_found"
    val message: String,  // human-readable explanation
)
