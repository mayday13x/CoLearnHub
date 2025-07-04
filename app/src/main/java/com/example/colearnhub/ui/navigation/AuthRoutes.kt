package com.example.colearnhub.ui.navigation

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.colearnhub.ui.screen.login.LoginScreen
import com.example.colearnhub.ui.screen.signup.SignupStep1Screen
import com.example.colearnhub.ui.screen.signup.SignupStep2Screen
import com.example.colearnhub.viewModelLayer.SignupViewModelFactory
import com.example.colearnhub.viewmodel.AuthViewModel
import com.example.colearnhub.viewmodel.SignupViewModel

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.authRoutes(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    // ===== AUTH ROUTES =====
    composable("login") {
        LoginScreen(
            navController = navController,
            authViewModel = authViewModel
        )
    }

    // Signup flow with shared ViewModel
    composable("signup_step1") { backStackEntry ->
        val parentEntry = remember(backStackEntry) {
            navController.getBackStackEntry("signup_step1")
        }
        val signupViewModel: SignupViewModel = viewModel(
            viewModelStoreOwner = parentEntry,
            factory = SignupViewModelFactory(LocalContext.current.applicationContext as Application)
        )

        SignupStep1Screen(
            navController = navController,
            viewModel = signupViewModel
        )
    }

    composable("signup_step2") { backStackEntry ->
        val parentEntry = remember(backStackEntry) {
            navController.getBackStackEntry("signup_step1")
        }
        val signupViewModel: SignupViewModel = viewModel(
            viewModelStoreOwner = parentEntry,
            factory = SignupViewModelFactory(LocalContext.current.applicationContext as Application)
        )

        SignupStep2Screen(
            navController = navController,
            viewModel = signupViewModel
        )
    }
}