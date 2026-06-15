package com.library.android.presentation.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.library.android.presentation.screens.account.AccountScreen
import com.library.android.presentation.screens.admin.ManageUsersScreen
import com.library.android.presentation.screens.auth.ForgotPasswordScreen
import com.library.android.presentation.screens.auth.LoginScreen
import com.library.android.presentation.screens.auth.ProfileActions
import com.library.android.presentation.screens.auth.ProfileScreen
import com.library.android.presentation.screens.auth.RegisterScreen
import com.library.android.presentation.screens.auth.ResetPasswordScreen
import com.library.android.presentation.screens.auth.VerifyEmailScreen
import com.library.android.presentation.screens.card.AccessCardScreen
import com.library.android.presentation.screens.catalog.CatalogScreen
import com.library.android.presentation.screens.chat.ChatScreen
import com.library.android.presentation.screens.detail.BookDetailScreen
import com.library.android.presentation.screens.lending.LendingScreen
import com.library.android.presentation.screens.recommend.RecommendationsScreen
import com.library.android.presentation.screens.reminders.RemindersScreen
import com.library.android.presentation.screens.wifi.WifiScreen

/** App navigation graph. Catalog is public; account + feature screens gate on auth. */
@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.CATALOG) {
        catalogDestinations(navController)
        accountDestinations(navController)
        featureDestinations(navController)
        iamDestinations(navController)
    }
}

private fun NavGraphBuilder.catalogDestinations(navController: NavHostController) {
    composable(Routes.CATALOG) {
        CatalogScreen(
            onBookClick = { id -> navController.navigate(Routes.bookDetail(id)) },
            onProfileClick = { navController.navigate(Routes.PROFILE) },
            onLoginClick = { navController.navigate(Routes.LOGIN) },
            onBorrowedClick = { navController.navigate(Routes.LENDING) },
            onRecommendationsClick = { navController.navigate(Routes.RECOMMENDATIONS) },
            onChatClick = { navController.navigate(Routes.CHAT) },
        )
    }
    composable(
        route = Routes.BOOK_DETAIL,
        arguments = listOf(navArgument(Routes.ARG_BOOK_ID) { type = NavType.StringType }),
    ) {
        BookDetailScreen(
            onBack = { navController.popBackStack() },
            onLoginClick = { navController.navigate(Routes.LOGIN) },
        )
    }
}

private fun NavGraphBuilder.accountDestinations(navController: NavHostController) {
    composable(Routes.PROFILE) {
        ProfileScreen(
            onLogin = { navController.navigate(Routes.LOGIN) },
            onBack = { navController.popBackStack() },
            actions = ProfileActions(
                onReminders = { navController.navigate(Routes.REMINDERS) },
                onAccessCard = { navController.navigate(Routes.ACCESS_CARD) },
                onWifi = { navController.navigate(Routes.WIFI) },
                onAccount = { navController.navigate(Routes.ACCOUNT) },
                onManageUsers = { navController.navigate(Routes.MANAGE_USERS) },
                onVerifyEmail = { navController.navigate(Routes.VERIFY_EMAIL_NAV) },
            ),
        )
    }
    composable(Routes.LOGIN) {
        LoginScreen(
            onLoggedIn = { navController.navigate(Routes.PROFILE) { popUpTo(Routes.CATALOG) } },
            onRegisterClick = { navController.navigate(Routes.REGISTER) },
            onForgotPassword = { navController.navigate(Routes.FORGOT_PASSWORD) },
            onBack = { navController.popBackStack() },
        )
    }
    composable(Routes.REGISTER) {
        RegisterScreen(
            onRegistered = { navController.navigate(Routes.PROFILE) { popUpTo(Routes.CATALOG) } },
            onBack = { navController.popBackStack() },
        )
    }
}

private fun NavGraphBuilder.featureDestinations(navController: NavHostController) {
    composable(Routes.LENDING) {
        LendingScreen(
            onLogin = { navController.navigate(Routes.LOGIN) },
            onBack = { navController.popBackStack() },
        )
    }
    composable(Routes.RECOMMENDATIONS) {
        RecommendationsScreen(
            onBack = { navController.popBackStack() },
            onBookClick = { id -> navController.navigate(Routes.bookDetail(id)) },
        )
    }
    composable(Routes.CHAT) {
        ChatScreen(
            onLogin = { navController.navigate(Routes.LOGIN) },
            onBack = { navController.popBackStack() },
        )
    }
    composable(Routes.REMINDERS) {
        RemindersScreen(
            onLogin = { navController.navigate(Routes.LOGIN) },
            onBack = { navController.popBackStack() },
        )
    }
    composable(Routes.ACCESS_CARD) {
        AccessCardScreen(
            onLogin = { navController.navigate(Routes.LOGIN) },
            onBack = { navController.popBackStack() },
        )
    }
    composable(Routes.WIFI) {
        WifiScreen(onBack = { navController.popBackStack() })
    }
}

private fun NavGraphBuilder.iamDestinations(navController: NavHostController) {
    composable(Routes.ACCOUNT) {
        AccountScreen(
            onBack = { navController.popBackStack() },
            onDeleted = {
                navController.navigate(Routes.CATALOG) { popUpTo(Routes.CATALOG) { inclusive = true } }
            },
        )
    }
    composable(Routes.MANAGE_USERS) {
        ManageUsersScreen(onBack = { navController.popBackStack() })
    }
    composable(Routes.FORGOT_PASSWORD) {
        ForgotPasswordScreen(
            onBack = { navController.popBackStack() },
            onProceedToReset = { navController.navigate(Routes.RESET_PASSWORD_NAV) },
        )
    }
    composable(
        route = Routes.RESET_PASSWORD,
        arguments = listOf(navArgument(Routes.ARG_TOKEN) { nullable = true; defaultValue = null }),
        deepLinks = listOf(
            navDeepLink { uriPattern = "https://stacks.app/reset?${Routes.ARG_TOKEN}={${Routes.ARG_TOKEN}}" },
            navDeepLink { uriPattern = "library://reset?${Routes.ARG_TOKEN}={${Routes.ARG_TOKEN}}" },
        ),
    ) {
        ResetPasswordScreen(
            onBack = { navController.popBackStack() },
            onDone = { navController.navigate(Routes.LOGIN) { popUpTo(Routes.CATALOG) } },
        )
    }
    composable(
        route = Routes.VERIFY_EMAIL,
        arguments = listOf(navArgument(Routes.ARG_TOKEN) { nullable = true; defaultValue = null }),
        deepLinks = listOf(
            navDeepLink { uriPattern = "https://stacks.app/verify?${Routes.ARG_TOKEN}={${Routes.ARG_TOKEN}}" },
            navDeepLink { uriPattern = "library://verify?${Routes.ARG_TOKEN}={${Routes.ARG_TOKEN}}" },
        ),
    ) {
        VerifyEmailScreen(onBack = { navController.popBackStack() })
    }
}
