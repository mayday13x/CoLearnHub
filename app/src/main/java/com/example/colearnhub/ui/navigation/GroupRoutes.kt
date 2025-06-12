package com.example.colearnhub.ui.navigation

import com.example.colearnhub.ui.screen.group.CreateGroupScreen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.colearnhub.ui.screen.group.GroupDetailsContent
import com.example.colearnhub.ui.screen.group.GroupDetailsScreen
import com.example.colearnhub.ui.screen.group.GroupParticipantsScreen
import com.example.colearnhub.ui.screen.group.InviteDetailsScreen
import com.example.colearnhub.ui.screen.group.InvitesScreen
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
    composable("invites") {
        InvitesScreen(navController = navController)
    }

    composable(
        "invite_details/{groupId}",
        arguments = listOf(navArgument("groupId") { type = NavType.StringType })
    ) { backStackEntry ->
        val groupId = backStackEntry.arguments?.getString("groupId") ?: ""
        InviteDetailsScreen(
            navController = navController,
            groupId = groupId
        )
    }
    composable(
        "group_details/{groupId}",
        arguments = listOf(navArgument("groupId") { type = NavType.LongType })
    ) { backStackEntry ->
        val groupId = backStackEntry.arguments?.getLong("groupId") ?: 0L
        GroupDetailsScreen(
            navController = navController,
            groupId = groupId
        )
    }

    composable("group_participants/{groupId}") { backStackEntry ->
        val groupId = backStackEntry.arguments?.getString("groupId")?.toLongOrNull() ?: 0L
        GroupParticipantsScreen(navController, groupId)
    }

}