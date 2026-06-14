package com.library.android.presentation.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.library.android.presentation.screens.auth.LoginScreen
import com.library.android.presentation.screens.auth.ProfileScreen
import com.library.android.presentation.screens.auth.RegisterScreen
import com.library.android.presentation.screens.catalog.CatalogScreen
import com.library.android.presentation.screens.chat.ChatScreen
import com.library.android.presentation.screens.detail.BookDetailScreen
import com.library.android.presentation.screens.lending.LendingScreen
import com.library.android.presentation.screens.recommend.RecommendationsScreen
import com.library.android.presentation.screens.reminders.RemindersScreen

/** App navigation graph. Catalog is public; account + feature screens gate on auth. */
@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.CATALOG) {
        catalogDestinations(navController)
        accountDestinations(navController)
        featureDestinations(navController)
    }
}

private fun NavGraphBuilder.catalogDestinations(navController: NavHostController) {
    composable(Routes.CATALOG) {
        CatalogScreen(
            onBookClick = { id -> navController.navigate(Routes.bookDetail(id)) },
            onProfileClick = { navController.navigate(Routes.PROFILE) },
            onBorrowedClick = { navController.navigate(Routes.LENDING) },
            onRecommendationsClick = { navController.navigate(Routes.RECOMMENDATIONS) },
            onChatClick = { navController.navigate(Routes.CHAT) },
        )
    }
    composable(
        route = Routes.BOOK_DETAIL,
        arguments = listOf(navArgument(Routes.ARG_BOOK_ID) { type = NavType.StringType }),
    ) {
        BookDetailScreen(onBack = { navController.popBackStack() })
    }
}

private fun NavGraphBuilder.accountDestinations(navController: NavHostController) {
    composable(Routes.PROFILE) {
        ProfileScreen(
            onLogin = { navController.navigate(Routes.LOGIN) },
            onBack = { navController.popBackStack() },
            onReminders = { navController.navigate(Routes.REMINDERS) },
        )
    }
    composable(Routes.LOGIN) {
        LoginScreen(
            onLoggedIn = { navController.navigate(Routes.PROFILE) { popUpTo(Routes.CATALOG) } },
            onRegisterClick = { navController.navigate(Routes.REGISTER) },
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
}
