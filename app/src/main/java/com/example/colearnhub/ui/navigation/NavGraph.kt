package com.example.colearnhub.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.platform.LocalContext
import com.example.colearnhub.viewmodel.AuthViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val authViewModel: AuthViewModel = viewModel { AuthViewModel(context) }

    val startDestination = if (authViewModel.isUserLoggedIn()) "MainScreen" else "login"

    NavHost(navController = navController, startDestination = startDestination) {
        authRoutes(navController, authViewModel)
        mainRoutes(navController)
        testRoutes(navController)
    }
}