package com.library.android.presentation.nav

/** Navigation routes as constants (no stringly-typed literals scattered through the tree). */
object Routes {
    const val CATALOG = "catalog"
    const val ARG_BOOK_ID = "id"
    const val BOOK_DETAIL = "book/{$ARG_BOOK_ID}"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val PROFILE = "profile"
    const val LENDING = "lending"
    const val RECOMMENDATIONS = "recommendations"
    const val CHAT = "chat-room"
    const val REMINDERS = "reminders"
    const val ACCESS_CARD = "access-card"
    const val WIFI = "wifi"

    // IAM v2
    const val ARG_TOKEN = "token"
    const val ACCOUNT = "account"
    const val MANAGE_USERS = "manage-users"
    const val FORGOT_PASSWORD = "forgot-password"
    // Composable routes (with the optional token arg the deep links carry).
    const val RESET_PASSWORD = "reset-password?$ARG_TOKEN={$ARG_TOKEN}"
    const val VERIFY_EMAIL = "verify-email?$ARG_TOKEN={$ARG_TOKEN}"
    // In-app navigate targets (no token — manual entry).
    const val RESET_PASSWORD_NAV = "reset-password"
    const val VERIFY_EMAIL_NAV = "verify-email"

    fun bookDetail(id: String): String = "book/$id"
}
