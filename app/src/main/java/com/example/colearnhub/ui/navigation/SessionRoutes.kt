package com.example.colearnhub.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.colearnhub.ui.screen.session.DetailsOwnerScreen
import com.example.colearnhub.ui.screen.session.NSSScreen

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
}