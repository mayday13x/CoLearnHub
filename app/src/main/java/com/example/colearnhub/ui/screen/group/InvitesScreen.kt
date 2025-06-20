package com.example.colearnhub.ui.screen.group
import android.os.Build
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.colearnhub.R
import com.example.colearnhub.modelLayer.GroupResponse
import com.example.colearnhub.repositoryLayer.GroupRepository
import com.example.colearnhub.ui.utils.logoSize
import com.example.colearnhub.ui.utils.spacer3
import com.example.colearnhub.ui.utils.txtSize
import com.example.colearnhub.viewModelLayer.AuthViewModelFactory
import com.example.colearnhub.viewmodel.AuthViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Tela com lista de convites
@Composable
fun InvitesScreen(navController: NavController) {
    val context = LocalContext.current
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(context)
    )
    val currentUser by authViewModel.currentUser.collectAsState()
    val currentUserId = currentUser?.id

    var pendingInvites by remember { mutableStateOf<List<GroupResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val groupRepository = remember { GroupRepository() }

    // Carregar convites pendentes
    LaunchedEffect(currentUserId) {
        if (currentUserId != null) {
            isLoading = true
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val invites = groupRepository.getUserPendingInvites(currentUserId)
                    pendingInvites = invites
                } catch (e: Exception) {
                    // Handle error
                } finally {
                    isLoading = false
                }
            }
        } else {
            isLoading = false
        }
    }

    BackHandler {
        navController.navigate("MainScreen?selectedItem=3") {
            popUpTo("invites") { inclusive = true }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        InvitesHeader(
            onBack = { navController.navigate("MainScreen?selectedItem=3") {
                popUpTo("invites") { inclusive = true }
            } }
        )

        if (isLoading) {
            // Loading state
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = Color(0xFF395174)
                )
            }
        } else if (pendingInvites.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.no_pending_invites),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.pending_invites_message),
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            // Lista de convites
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = stringResource(R.string.new_invites),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(pendingInvites) { groupResponse ->
                        InviteCard(
                            groupResponse = groupResponse,
                            onClick = {
                                navController.navigate("invite_details/${groupResponse.group.id}")
                            }
                        )
                    }
                }
            }
        }
    }
}

// Header da tela de convites
@Composable
fun InvitesHeader(onBack: () -> Unit) {
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
            text = stringResource(R.string.Invites),
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

// Card individual de convite
@Composable
fun InviteCard(
    groupResponse: GroupResponse,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar do grupo (círculo com ícone)
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        Color(0xFF395174).copy(alpha = 0.1f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Group Avatar",
                    tint = Color(0xFF395174),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Informações do grupo
            Column(
                modifier = Modifier.weight(1f)
            ) {
                groupResponse.group.name?.let { name ->
                    Text(
                        text = name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Members",
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${groupResponse.members.size}",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }

            // Seta para indicar que é clicável
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Ver detalhes",
                tint = Color(0xFF395174),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// Tela de detalhes do convite
// Updated InviteDetailsScreen with proper navigation back to groups tab

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun InviteDetailsScreen(
    navController: NavController,
    groupId: String
) {
    val context = LocalContext.current
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(context)
    )
    val currentUser by authViewModel.currentUser.collectAsState()
    val currentUserId = currentUser?.id

    var groupResponse by remember { mutableStateOf<GroupResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isProcessing by remember { mutableStateOf(false) }
    val groupRepository = remember { GroupRepository() }

    // Carregar detalhes do grupo
    LaunchedEffect(groupId) {
        isLoading = true
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val group = groupRepository.getGroupById(groupId.toLong())
                group?.let {
                    val members = groupRepository.getGroupMembers(groupId.toLong())
                    groupResponse = GroupResponse(
                        group = it,
                        members = members
                    )
                }
            } catch (e: Exception) {
                Log.e("InviteDetailsScreen", "Erro ao carregar grupo: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        // Header
        InviteDetailsHeader(
            onBack = { navController.popBackStack() }
        )

        if (isLoading) {
            // Loading state
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = Color(0xFF395174)
                )
            }
        } else {
            groupResponse?.let { response ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Informações do grupo
                    GroupDetailsContent(
                        groupResponse = response,
                        modifier = Modifier.weight(1f)
                    )

                    // Botão Accept Invite
                    Button(
                        onClick = {
                            if (currentUserId != null && !isProcessing) {
                                isProcessing = true
                                CoroutineScope(Dispatchers.IO).launch {
                                    try {
                                        val success = groupRepository.acceptGroupInvite(
                                            currentUserId,
                                            groupId.toLong()
                                        )
                                        if (success) {
                                            // Navigate back to groups tab and clear back stack
                                            withContext(Dispatchers.Main) {
                                                navController.navigate("MainScreen?selectedItem=3") {
                                                    popUpTo("groups") { inclusive = true }
                                                }
                                            }
                                        }
                                    } catch (e: Exception) {
                                        Log.e("InviteDetailsScreen", "Erro ao aceitar convite: ${e.message}")
                                    } finally {
                                        isProcessing = false
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF395174),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp),
                        enabled = !isProcessing
                    ) {
                        if (isProcessing) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Text(
                                text = stringResource(R.string.accept_invite),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Botão Decline
                    OutlinedButton(
                        onClick = {
                            if (currentUserId != null && !isProcessing) {
                                isProcessing = true
                                CoroutineScope(Dispatchers.IO).launch {
                                    try {
                                        val success = groupRepository.rejectGroupInvite(
                                            currentUserId,
                                            groupId.toLong()
                                        )
                                        if (success) {
                                            // Navigate back to groups tab and clear back stack
                                            withContext(Dispatchers.Main) {
                                                navController.navigate("MainScreen?selectedItem=3") {
                                                    popUpTo("groups") { inclusive = true }
                                                }
                                            }
                                        }
                                    } catch (e: Exception) {
                                        Log.e("InviteDetailsScreen", "Erro ao rejeitar convite: ${e.message}")
                                    } finally {
                                        isProcessing = false
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF395174)
                        ),
                        border = BorderStroke(
                            width = 1.dp,
                            color = Color(0xFF395174)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        enabled = !isProcessing
                    ) {
                        if (isProcessing) {
                            CircularProgressIndicator(
                                color = Color(0xFF395174),
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Text(
                                text = stringResource(R.string.decline),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

// Header da tela de detalhes do convite
@Composable
fun InviteDetailsHeader(onBack: () -> Unit) {
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
            text = stringResource(R.string.invite_details),
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

// Conteúdo com detalhes do grupo
@Composable
fun GroupDetailsContent(
    groupResponse: GroupResponse,
    modifier: Modifier = Modifier
) {
    val txtSize = (txtSize().value - 2).sp
    val txtSize2 = (txtSize().value + 2).sp
    Column(
        modifier = modifier
    ) {
        // Nome do grupo
        groupResponse.group.name?.let { name ->
            Text(
                text = stringResource(R.string.name),
                fontSize = txtSize,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF395174),
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = name,
                fontSize = txtSize2,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 20.dp)
            )
        }

        // Descrição
        Text(
            text = stringResource(R.string.description),
            fontSize = txtSize,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF395174),
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = groupResponse.group.description ?: stringResource(R.string.no_description),
            fontSize = txtSize(),
            color = Color.Black,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        // Owner
        Text(
            text = stringResource(R.string.owner),
            fontSize = txtSize,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF395174),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 20.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(Color.Black, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Owner Avatar",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Owner", // Aqui você pode buscar o nome real do owner
                fontSize = txtSize(),
                color = Color.Black
            )
        }

        // Participantes
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .clickable { /* Navegar para lista de participantes */ },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource (R.string.participants) + " (${groupResponse.members.size})",
                fontSize = txtSize(),
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )

            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Ver participantes",
                tint = Color(0xFF395174),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}