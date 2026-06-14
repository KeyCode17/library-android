package com.library.android.data.remote

import com.library.android.data.remote.dto.BorrowRequestDto
import com.library.android.domain.model.Loan
import com.library.android.domain.repo.LoanException
import com.library.android.domain.repo.LoanRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * REST-backed [LoanRepository]. Maps HTTP failures to user-facing [LoanException] messages
 * (typed catches only). The server scopes and authorizes; this layer just surfaces the result.
 */
@Singleton
class LoanRemoteRepository @Inject constructor(
    private val api: LoanApi,
    private val ioDispatcher: CoroutineDispatcher,
) : LoanRepository {

    override suspend fun listLoans(): Result<List<Loan>> = withContext(ioDispatcher) {
        guarded({ code ->
            if (code == HTTP_UNAUTHORIZED) "Sign in to see your loans" else "Couldn't load loans (code $code)"
        }) { api.listLoans().data.map { it.toDomain() } }
    }

    override suspend fun borrow(bookId: String): Result<Loan> = withContext(ioDispatcher) {
        guarded({ code ->
            when (code) {
                HTTP_UNAUTHORIZED -> "Sign in to borrow"
                HTTP_NOT_FOUND -> "That book no longer exists"
                HTTP_CONFLICT -> "That book isn't available to borrow"
                else -> "Couldn't borrow (code $code)"
            }
        }) { api.borrow(BorrowRequestDto(bookId)).toDomain() }
    }

    override suspend fun returnLoan(loanId: String): Result<Loan> = withContext(ioDispatcher) {
        guarded({ code ->
            when (code) {
                HTTP_UNAUTHORIZED -> "Sign in to return"
                HTTP_FORBIDDEN -> "You can only return your own loans"
                HTTP_NOT_FOUND -> "That loan no longer exists"
                HTTP_CONFLICT -> "That loan can't be returned"
                else -> "Couldn't return (code $code)"
            }
        }) { api.returnLoan(loanId).toDomain() }
    }

    override suspend fun approve(loanId: String): Result<Loan> = withContext(ioDispatcher) {
        guarded({ code ->
            when (code) {
                HTTP_UNAUTHORIZED -> "Sign in to approve"
                HTTP_FORBIDDEN -> "Only staff can approve loans"
                HTTP_NOT_FOUND -> "That loan no longer exists"
                HTTP_CONFLICT -> "That loan isn't awaiting approval"
                else -> "Couldn't approve (code $code)"
            }
        }) { api.approve(loanId).toDomain() }
    }

    // Runs [block], turning a typed HTTP/IO failure into a Result.failure with a mapped message.
    private inline fun <T> guarded(httpMessage: (Int) -> String, block: () -> T): Result<T> =
        try {
            Result.success(block())
        } catch (e: HttpException) {
            Result.failure(LoanException(httpMessage(e.code()), e))
        } catch (e: IOException) {
            Result.failure(LoanException(NETWORK_ERROR, e))
        }

    private companion object {
        const val HTTP_UNAUTHORIZED = 401
        const val HTTP_FORBIDDEN = 403
        const val HTTP_NOT_FOUND = 404
        const val HTTP_CONFLICT = 409
        const val NETWORK_ERROR = "Network error — check your connection"
    }
}
