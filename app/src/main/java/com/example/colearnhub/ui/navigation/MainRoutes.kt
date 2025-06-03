package com.example.colearnhub.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.colearnhub.ui.screen.login.LoginScreen
import com.example.colearnhub.ui.screen.main.MainScreen
import com.example.colearnhub.viewmodel.AuthViewModel

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.mainRoutes(
    navController: NavHostController
) {
    // ===== AUTH ROUTES =====
    composable("MainScreen") {
        MainScreen()
    }
}