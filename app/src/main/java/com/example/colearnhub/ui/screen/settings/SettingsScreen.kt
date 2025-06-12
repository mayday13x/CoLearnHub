@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.colearnhub.ui.screen.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.colearnhub.R
import com.example.colearnhub.ui.utils.logoSize
import com.example.colearnhub.ui.utils.sbutton
import com.example.colearnhub.ui.utils.spacer3
import com.example.colearnhub.ui.utils.titleFontSize
import com.example.colearnhub.ui.utils.txtSize
import com.example.colearnhub.ui.utils.verticalSpacing
import com.example.colearnhub.viewmodel.AuthViewModel

@Composable
fun TopSettingsBar(onBack: () -> Unit) {
    val txtSize = (txtSize().value + 4).sp
    val barSize = spacer3()
    val logoSize = logoSize() - 13.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(barSize)
            .background(Color(0xFF395174)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.settings),
            color = Color.White,
            fontSize = txtSize,
            fontWeight = FontWeight.Medium
        )

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(logoSize)
                )
            }
        }
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    titleColor: Color = Color.Black,
    showArrow: Boolean = true,
    onClick: () -> Unit
) {
    val textSize = txtSize()
    val logoSize = sbutton() + 10.dp
    val spacer = sbutton() + 2.dp
    val spacer2 = sbutton() + 6.dp
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .clickable { onClick() }
            .padding(horizontal = spacer2, vertical = spacer),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (titleColor == Color.Red) Color.Red else Color.Gray,
            modifier = Modifier.size(logoSize)
        )

        Spacer(modifier = Modifier.width(spacer))

        Text(
            text = title,
            fontSize = textSize,
            color = titleColor,
            modifier = Modifier.weight(1f)
        )

        if (showArrow) {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Arrow",
                tint = Color.Gray,
                modifier = Modifier.size(spacer2)
            )
        }
    }
}

@Composable
fun SettingsList(
    authViewModel: AuthViewModel = viewModel()
){
    val padding = logoSize() - 10.dp
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = padding),
        verticalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        item {
            SettingsItem(
                icon = Icons.Default.Language,
                title = stringResource(R.string.language2),
                onClick = { /* Handle language click */ }
            )
        }

        item {
            SettingsItem(
                icon = Icons.Default.Lock,
                title = stringResource(R.string.change_password),
                onClick = { /* Handle change password click */ }
            )
        }

        item {
            SettingsItem(
                icon = Icons.Default.Email,
                title = stringResource(R.string.change_email),
                onClick = { /* Handle change email click */ }
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            SettingsItem(
                icon = Icons.Default.Feedback,
                title = stringResource(R.string.Provide_Feedback),
                onClick = { /* Handle feedback click */ }
            )
        }

        item {
            SettingsItem(
                icon = Icons.Default.Info,
                title = stringResource(R.string.about),
                onClick = { /* Handle about click */ }
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            SettingsItem(
                icon = Icons.Default.DeleteForever,
                title = stringResource(R.string.Delete_Account),
                titleColor = Color.Red,
                onClick = { /* Handle delete account click */ }
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            SettingsItem(
                icon = Icons.Default.Logout,
                title = stringResource(R.string.Logout),
                showArrow = false,
                onClick = { authViewModel.signOut() }
            )
        }
    }
}

@Composable
fun VersionApp(){
    val logoSize = logoSize()
    val logoSize2 = verticalSpacing() + 8.dp
    val textSize = titleFontSize()
    val textSize2 = (titleFontSize().value + 2).sp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = logoSize),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(logoSize2)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.cube),
                contentDescription = "Logo",
                modifier = Modifier.size(logoSize)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = stringResource(R.string.app_name2),
            fontSize = textSize2,
            color = Color.Gray
        )

        Text(
            text = "v1.0.0",
            fontSize = textSize,
            color = Color.Gray
        )
    }
}

@Composable
fun SettingsIndice(onBack: () -> Unit, authViewModel: AuthViewModel = viewModel()){
    TopSettingsBar(onBack = onBack)
    SettingsList(authViewModel = authViewModel)
    VersionApp()
}

@Composable
fun SettingsScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel = viewModel()
) {
    val authState by authViewModel.authState.collectAsState()

    LaunchedEffect(authState.isAuthenticated) {
        if (!authState.isAuthenticated) {
            navController.navigate("login") {
                popUpTo("MainScreen") { inclusive = true }
            }
        }
    }

    val navigateBack = {
        navController.navigate("MainScreen?selectedItem=4") {
            popUpTo("group_details") { inclusive = true }
            launchSingleTop = true
        }
    }

    BackHandler {
        navigateBack()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            SettingsIndice(onBack = navigateBack, authViewModel = authViewModel)
        }
    }
}