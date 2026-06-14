package com.library.android.presentation.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.library.android.presentation.screens.catalog.CatalogScreen
import com.library.android.presentation.screens.detail.BookDetailScreen

/** App navigation graph: catalog list → book detail. State down (routes), events up (lambdas). */
@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.CATALOG) {
        composable(Routes.CATALOG) {
            CatalogScreen(
                onBookClick = { id -> navController.navigate(Routes.bookDetail(id)) },
            )
        }
        composable(
            route = Routes.BOOK_DETAIL,
            arguments = listOf(navArgument(Routes.ARG_BOOK_ID) { type = NavType.StringType }),
        ) {
            BookDetailScreen(onBack = { navController.popBackStack() })
        }
    }
}
