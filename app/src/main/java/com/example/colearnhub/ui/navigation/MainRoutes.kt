package com.example.colearnhub.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.colearnhub.ui.screen.main.MainScreen

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.mainRoutes(
    navController: NavHostController
) {
    // ===== AUTH ROUTES =====
    composable("MainScreen") {
        MainScreen(navController)
    }

    composable(
        route = "MainScreen?selectedItem={selectedItem}",
        arguments = listOf(
            navArgument("selectedItem") {
                type = NavType.IntType
                defaultValue = 0
            }
        )
    ) { backStackEntry ->
        val selectedItem = backStackEntry.arguments?.getInt("selectedItem") ?: 0
        MainScreen(navController = navController, initialSelectedItem = selectedItem)
    }
}