package com.library.android.data.remote

import com.library.android.data.remote.dto.LoanDto
import com.library.android.data.remote.dto.LoanStatusDto
import com.library.android.domain.model.Loan
import com.library.android.domain.model.LoanStatus

/** Maps lending wire DTOs to domain models. */
fun LoanDto.toDomain(): Loan = Loan(
    id = id,
    bookId = bookId,
    userId = userId,
    status = status.toDomain(),
    borrowedAt = borrowedAt,
    dueAt = dueAt,
    returnedAt = returnedAt,
    approvedBy = approvedBy,
    approvedAt = approvedAt,
)

fun LoanStatusDto.toDomain(): LoanStatus = when (this) {
    LoanStatusDto.BORROWED -> LoanStatus.BORROWED
    LoanStatusDto.RETURNED -> LoanStatus.RETURNED
    LoanStatusDto.APPROVED -> LoanStatus.APPROVED
}
