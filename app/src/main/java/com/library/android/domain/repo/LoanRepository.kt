package com.library.android.domain.repo

import com.library.android.domain.model.Loan

/**
 * Port for lending. All calls require a bearer token (attached by the networking interceptor).
 * The server scopes `listLoans` by role (member: own; staff: all) and enforces permissions.
 */
interface LoanRepository {
    suspend fun listLoans(): Result<List<Loan>>
    suspend fun borrow(bookId: String): Result<Loan>
    suspend fun returnLoan(loanId: String): Result<Loan>
    suspend fun approve(loanId: String): Result<Loan>
}
