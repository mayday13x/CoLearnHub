package com.example.colearnhub.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.colearnhub.ui.screen.main.MainScreen
import com.example.colearnhub.ui.screen.main.MaterialDetailsScreen

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.mainRoutes(
    navController: NavHostController
) {
    // ===== MAIN ROUTES =====
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

    // Material Details Screen
    composable(
        route = "material_details/{materialId}",
        arguments = listOf(
            navArgument("materialId") {
                type = NavType.StringType
            }
        )
    ) { backStackEntry ->
        val materialId = backStackEntry.arguments?.getString("materialId") ?: "1"
        MaterialDetailsScreen(
            navController = navController,
            materialId = materialId
        )
    }
}