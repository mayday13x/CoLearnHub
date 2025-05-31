package com.example.colearnhub.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.colearnhub.ui.screen.authTest.SignUpScreen
import com.example.colearnhub.ui.screen.login.LoginScreen
import com.example.colearnhub.ui.screen.signup.SignupStep1Screen
import com.example.colearnhub.ui.screen.signup.SignupStep2Screen
import com.example.colearnhub.viewmodel.SignupViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavGraph(startDestination: String = "login") {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = startDestination) {

        composable("login") {
            LoginScreen(navController)
        }

        composable("signup") {
            SignUpScreen(navController = navController)
        }

        // Signup flow with shared ViewModel
        composable("signup_step1") { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry("signup_step1")
            }
            val signupViewModel: SignupViewModel = viewModel(parentEntry)

            SignupStep1Screen(
                navController = navController,
                viewModel = signupViewModel
            )
        }

        composable("signup_step2") { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry("signup_step1")
            }
            val signupViewModel: SignupViewModel = viewModel(parentEntry)

            SignupStep2Screen(
                navController = navController,
                viewModel = signupViewModel
            )
        }
    }
}