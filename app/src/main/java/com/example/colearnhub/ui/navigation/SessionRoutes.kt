package com.example.colearnhub.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.colearnhub.ui.screen.session.DetailsOwnerScreen
import com.example.colearnhub.ui.screen.session.NSSScreen
import com.example.colearnhub.ui.screen.session.StudySessionDetailsScreen

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.sessionRoutes(
    navController: NavHostController,
){
    composable("new_session"){
        NSSScreen(navController = navController)
    }
    composable("session_details_owner"){
        DetailsOwnerScreen(navController = navController)
    }
    composable(
        "study_session_details/{sessionId}",
        arguments = listOf(navArgument("sessionId") { type = NavType.StringType })
    ) { backStackEntry ->
        val sessionId = backStackEntry.arguments?.getString("sessionId")
        if (sessionId != null) {
            StudySessionDetailsScreen(navController = navController, viewModel = viewModel())
        }
    }
}