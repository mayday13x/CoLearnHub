package com.example.colearnhub.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.colearnhub.data.SignupData
import com.example.colearnhub.ui.screen.authTest.SignUpScreen
import com.example.colearnhub.ui.screen.login.LoginScreen
import com.example.colearnhub.ui.screen.signup.SignupStep1Screen
import com.example.colearnhub.ui.screen.signup.SignupStep2Screen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavGraph(startDestination: String = "login") {
    val navController = rememberNavController()
    val signupData = remember { SignupData() }
    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") {
            LoginScreen(navController)
        }
        composable("signup") {
            SignUpScreen(navController = navController)
        }
        composable("signup_step1") {
            SignupStep1Screen(
                navController = navController,
                signupData = signupData
            )
        }
        composable("signup_step2") {
            SignupStep2Screen(
                navController = navController,
                signupData = signupData
            )
        }
    }
}
