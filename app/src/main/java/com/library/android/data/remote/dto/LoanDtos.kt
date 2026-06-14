package com.library.android.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Lending wire DTOs mirrored **exactly** from `library-backend/contract/openapi.yaml`
 * (components.schemas). Date-times stay as ISO-8601 strings; snake_case keys via [SerialName].
 */

@Serializable
enum class LoanStatusDto {
    @SerialName("borrowed") BORROWED,
    @SerialName("returned") RETURNED,
    @SerialName("approved") APPROVED,
}

@Serializable
data class BorrowRequestDto(
    @SerialName("book_id") val bookId: String,
)

@Serializable
data class LoanDto(
    val id: String,
    @SerialName("book_id") val bookId: String,
    @SerialName("user_id") val userId: String,
    val status: LoanStatusDto,
    @SerialName("borrowed_at") val borrowedAt: String,
    @SerialName("due_at") val dueAt: String,
    @SerialName("returned_at") val returnedAt: String? = null,
    @SerialName("approved_by") val approvedBy: String? = null,
    @SerialName("approved_at") val approvedAt: String? = null,
)

@Serializable
data class LoanListDto(
    val data: List<LoanDto>,
    val pagination: PaginationDto,
)
