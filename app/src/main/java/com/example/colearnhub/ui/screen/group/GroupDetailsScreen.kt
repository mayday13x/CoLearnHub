package com.example.colearnhub.ui.screen.group

import android.app.Application
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.colearnhub.R
import com.example.colearnhub.ui.utils.logoSize
import com.example.colearnhub.ui.utils.sbutton
import com.example.colearnhub.ui.utils.spacer3
import com.example.colearnhub.ui.utils.txtSize
import com.example.colearnhub.viewModelLayer.GroupViewModel
import com.example.colearnhub.repositoryLayer.UserRepository
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun GDTopBar(onBack: () -> Unit){
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
            text = stringResource(R.string.group_details),
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
fun GDName(groupName: String?){
    val paddingValue = logoSize() + 10.dp
    val titleFontSize = (txtSize().value + 1).sp
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = paddingValue)
            .padding(top = paddingValue)
            .padding(bottom = 8.dp),
    ){
        Text(
            text = stringResource(R.string.name),
            fontSize = titleFontSize,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF395174),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = groupName ?: stringResource(R.string.loading_group),
            fontSize = (titleFontSize.value - 2).sp,
            color = Color.Black,
            fontWeight = FontWeight.Normal
        )
    }
}

@Composable
fun GDDescription(groupDescription: String?){
    val paddingValue = logoSize() + 10.dp
    val titleFontSize = (txtSize().value + 1).sp
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = paddingValue)
            .padding(top = paddingValue)
            .padding(bottom = 8.dp),
    ){
        Text(
            text = stringResource(R.string.description),
            fontSize = titleFontSize,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF395174),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = if (groupDescription.isNullOrBlank()) stringResource(R.string.no_description) else groupDescription,
            fontSize = (titleFontSize.value - 2).sp,
            color = Color.Black,
            fontWeight = FontWeight.Normal
        )
    }
}

@Composable
fun GDOwner(ownerUsername: String?){
    val paddingValue = logoSize() + 10.dp
    val titleFontSize = (txtSize().value + 1).sp
    val sizeValue = sbutton() + 8.dp
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = paddingValue)
            .padding(top = paddingValue)
            .padding(bottom = 8.dp),
    ) {
        Text(
            text = stringResource(R.string.owner),
            fontSize = titleFontSize,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF395174),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row {
            Box(
                modifier = Modifier
                    .size(sizeValue)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    tint = Color.Black,
                    modifier = Modifier.size(sizeValue)
                )
            }
            Text(
                text = ownerUsername ?: stringResource(R.string.unknown_owner),
                fontSize = (titleFontSize.value - 2).sp,
                color = Color.Black,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
fun GDParticipants(memberCount: Int, onParticipantsClick: () -> Unit = {}){
    val paddingValue = logoSize() + 10.dp
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = paddingValue)
            .padding(top = paddingValue - 20.dp)
            .clickable { onParticipantsClick() },
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.participants_count, memberCount),
                fontSize = 16.sp,
                color = Color.Black
            )
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "View participants",
                tint = Color.Gray
            )
        }
    }
}

@Composable
fun GDLeaveBtn(onLeaveClick: () -> Unit, isLoading: Boolean = false){
    val paddingValue = logoSize() + 10.dp
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = paddingValue)
        .padding(top = paddingValue + 30.dp)
        .padding(bottom = 8.dp)
    ){
        Button(
            onClick = { if (!isLoading) onLeaveClick() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isLoading) Color.Gray else Color(0xFFE53E3E)
            ),
            shape = RoundedCornerShape(8.dp),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Text(
                    text = stringResource(R.string.leave),
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun LeaveGroupDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = stringResource(R.string.leave_group),
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.leave_group_confirmation),
                    lineHeight = 20.sp,
                    color = Color.Black
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDismiss()
                        onConfirm()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color(0xFFE53E3E)
                    )
                ) {
                    Text(
                        text = stringResource(R.string.leave),
                        fontWeight = FontWeight.Medium
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismiss
                ) {
                    Text(
                        text = stringResource(R.string.cancel),
                        color = Color.Black
                    )
                }
            },
            containerColor = Color.White
        )
    }
}

@Composable
fun GDIndice(
    groupName: String?,
    groupDescription: String?,
    ownerUsername: String?,
    memberCount: Int,
    onBack: () -> Unit,
    onLeaveGroup: () -> Unit,
    onParticipantsClick: () -> Unit,
    isLeavingGroup: Boolean = false
){
    var showLeaveDialog by remember { mutableStateOf(false) }

    Column{
        GDTopBar(onBack = onBack)
        GDName(groupName = groupName)
        GDDescription(groupDescription = groupDescription)
        GDOwner(ownerUsername = ownerUsername)
        GDParticipants(memberCount = memberCount, onParticipantsClick = onParticipantsClick)
        GDLeaveBtn(
            onLeaveClick = { showLeaveDialog = true },
            isLoading = isLeavingGroup
        )
    }

    LeaveGroupDialog(
        showDialog = showLeaveDialog,
        onDismiss = { showLeaveDialog = false },
        onConfirm = onLeaveGroup
    )
}

@Composable
fun GroupDetailsScreen(
    navController: NavHostController,
    groupId: Long,
    viewModel: GroupViewModel = remember { GroupViewModel() }
){
    val context = LocalContext.current.applicationContext
    val groupDetailsState by viewModel.groupDetailsUiState.collectAsState()
    val userRepository = remember { UserRepository() }
    var ownerUsername by remember { mutableStateOf<String?>(null) }
    var isLeavingGroup by remember { mutableStateOf(false) }

    // Load group details when screen loads
    LaunchedEffect(groupId) {
        viewModel.loadGroupDetails(groupId)
    }

    // Load owner username when group details are loaded
    LaunchedEffect(groupDetailsState.groupDetails?.group?.owner_id) {
        groupDetailsState.groupDetails?.group?.owner_id?.let { ownerId ->
            try {
                val owner = userRepository.getUserById(ownerId)
                ownerUsername = owner?.username
            } catch (e: Exception) {
                ownerUsername = "Unknown"
            }
        }
    }

    val navigateBack = {
        navController.navigate("MainScreen?selectedItem=3") {
            popUpTo("group_details") { inclusive = true }
            launchSingleTop = true
        }
    }

    val handleLeaveGroup = {
        isLeavingGroup = true
        viewModel.leaveGroup(groupId) { success ->
            isLeavingGroup = false
            if (success) {
                navigateBack()
            } else {
                // Handle error - you might want to show a toast or error message
            }
        }
    }

    val handleParticipantsClick = {
        // Navigate to participants screen
        navController.navigate("group_participants/$groupId")
    }

    BackHandler(onBack = navigateBack)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .fillMaxSize()
            .background(Color.White)
    ) {
        when {
            groupDetailsState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF395174))
                }
            }
            groupDetailsState.errorMessage != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.error_loading_group),
                            color = Color.Red,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.loadGroupDetails(groupId) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF395174)
                            )
                        ) {
                            Text(stringResource(R.string.retry), color = Color.White)
                        }
                    }
                }
            }
            groupDetailsState.groupDetails != null -> {
                val groupDetails = groupDetailsState.groupDetails
                val acceptedMembers = groupDetails?.members?.filter { it.accept == true }

                Column {
                    GDIndice(
                        groupName = groupDetails?.group?.name,
                        groupDescription = groupDetails?.group?.description,
                        ownerUsername = ownerUsername,
                        memberCount = acceptedMembers?.size ?: 0,
                        onBack = navigateBack,
                        onLeaveGroup = handleLeaveGroup,
                        onParticipantsClick = handleParticipantsClick,
                        isLeavingGroup = isLeavingGroup
                    )
                }
            }
        }
    }
}