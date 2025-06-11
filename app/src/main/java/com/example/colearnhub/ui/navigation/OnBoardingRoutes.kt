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
import com.example.colearnhub.ui.screen.sliders.CoLearnHubOnboardingScreen
import com.example.colearnhub.ui.screen.sliders.GroupsOnboardingScreen
import com.example.colearnhub.ui.screen.sliders.SessionsOnboardingScreen
import com.example.colearnhub.ui.screen.sliders.ShareOnboardingScreen
import com.example.colearnhub.ui.utils.SharedPreferenceHelper

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.onboardingRoutes(
    navController: NavHostController,
    sharedPreferenceHelper: SharedPreferenceHelper
) {
    composable("colearnhub_onboarding") {
        CoLearnHubOnboardingScreen(
            navController = navController,
            onNext = { navController.navigate("sessions_onboarding") },
            onSkip = {
                sharedPreferenceHelper.setOnboardingSeen()
                navController.navigate("login") {
                    popUpTo("colearnhub_onboarding") { inclusive = true }
                }
            }
        )
    }

    composable("sessions_onboarding") {
        SessionsOnboardingScreen(
            navController = navController,
            onNext = { navController.navigate("share_onboarding") },
            onSkip = {
                sharedPreferenceHelper.setOnboardingSeen()
                navController.navigate("login") {
                    popUpTo("colearnhub_onboarding") { inclusive = true }
                }
            }
        )
    }

    composable("share_onboarding") {
        ShareOnboardingScreen(
            navController = navController,
            onNext = { navController.navigate("groups_onboarding") },
            onSkip = {
                sharedPreferenceHelper.setOnboardingSeen()
                navController.navigate("login") {
                    popUpTo("colearnhub_onboarding") { inclusive = true }
                }
            }
        )
    }

    composable("groups_onboarding") {
        GroupsOnboardingScreen(
            navController = navController,
            onDone = {
                sharedPreferenceHelper.setOnboardingSeen()
                navController.navigate("login") {
                    popUpTo("colearnhub_onboarding") { inclusive = true }
                }
            }
        )
    }
}