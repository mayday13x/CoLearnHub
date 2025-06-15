package com.example.colearnhub.ui.navigation

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.colearnhub.ui.screen.main.FavouritesScreen
import com.example.colearnhub.ui.screen.others.EditProfileScreen
import com.example.colearnhub.ui.screen.settings.SettingsScreen
import com.example.colearnhub.ui.utils.SharedPreferenceHelper
import com.example.colearnhub.viewmodel.AuthViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val authViewModel: AuthViewModel = viewModel { AuthViewModel(context) }
    val sharedPreferenceHelper = SharedPreferenceHelper(context)

    Log.d("NavGraph", "isUserLoggedIn: ${authViewModel.isUserLoggedIn()}")
    Log.d("NavGraph", "hasSeenOnboarding: ${sharedPreferenceHelper.hasSeenOnboarding()}")

    val startDestination = when {
        !sharedPreferenceHelper.hasSeenOnboarding() -> "colearnhub_onboarding"
        authViewModel.isUserLoggedIn() -> "MainScreen"
        else -> "login"
    }

    NavHost(navController = navController, startDestination = startDestination) {
        onboardingRoutes(navController, sharedPreferenceHelper)
        authRoutes(navController, authViewModel)
        mainRoutes(navController)
        settingsRoutes(navController, authViewModel)
        profileRoutes(navController)
        groupRoutes(navController)
        favouritesRoutes(navController)
        sessionRoutes(navController)
    }
}
