package com.example.colearnhub.ui.navigation

import com.example.colearnhub.ui.screen.group.CreateGroupScreen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.colearnhub.ui.screen.others.EditProfileScreen
import com.example.colearnhub.ui.screen.settings.SettingsScreen
import com.example.colearnhub.viewmodel.AuthViewModel

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.groupRoutes(
    navController: NavHostController
){
    composable("creategroup") {
        CreateGroupScreen(
            onNavigateBack = { navController.popBackStack() },
            onGroupCreated = { navController.popBackStack() }
        )
    }
}