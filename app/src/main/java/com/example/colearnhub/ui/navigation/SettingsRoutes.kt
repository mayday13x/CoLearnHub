package com.example.colearnhub.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.colearnhub.ui.screen.settings.SettingsScreen
import com.example.colearnhub.viewmodel.AuthViewModel

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.settingsRoutes(
    navController: NavHostController,
    authViewModel: AuthViewModel
){

    composable("settings") {
        SettingsScreen(
            navController = navController,
            authViewModel = authViewModel
        )
    }
}