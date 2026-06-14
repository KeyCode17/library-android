package com.library.android.domain.model

/** Loan lifecycle status (contract: LoanStatus). Overdue is derived from [Loan.dueAt]. */
enum class LoanStatus {
    BORROWED,
    RETURNED,
    APPROVED,
}

/**
 * A loan (contract: Loan). Date-times are kept as ISO-8601 strings to avoid pulling java.time
 * onto minSdk 24 without desugaring; the UI shows the date portion.
 */
data class Loan(
    val id: String,
    val bookId: String,
    val userId: String,
    val status: LoanStatus,
    val borrowedAt: String,
    val dueAt: String,
    val returnedAt: String? = null,
    val approvedBy: String? = null,
    val approvedAt: String? = null,
)
