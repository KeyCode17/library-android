package com.library.android.presentation.nav

/** Navigation routes as constants (no stringly-typed literals scattered through the tree). */
object Routes {
    const val CATALOG = "catalog"
    const val ARG_BOOK_ID = "id"
    const val BOOK_DETAIL = "book/{$ARG_BOOK_ID}"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val PROFILE = "profile"

    fun bookDetail(id: String): String = "book/$id"
}
