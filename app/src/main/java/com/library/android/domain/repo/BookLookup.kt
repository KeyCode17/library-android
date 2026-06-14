package com.library.android.domain.repo

import com.library.android.domain.model.Book

/**
 * Outcome of a single-book lookup. A 404 is an expected result, not an exception, so it is
 * modelled explicitly ([NotFound]) rather than thrown — keeping control flow out of catch blocks.
 */
sealed interface BookLookup {
    data class Found(val book: Book) : BookLookup
    data object NotFound : BookLookup
    data class Failed(val message: String) : BookLookup
}
