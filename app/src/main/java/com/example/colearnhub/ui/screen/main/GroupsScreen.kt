package com.example.colearnhub.ui.screen.main

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.colearnhub.R
import com.example.colearnhub.modelLayer.GroupResponse
import com.example.colearnhub.repositoryLayer.GroupRepository
import com.example.colearnhub.ui.utils.Circles
import com.example.colearnhub.ui.utils.Nav
import com.example.colearnhub.ui.utils.SBar
import com.example.colearnhub.ui.utils.ScreenContent
import com.example.colearnhub.ui.utils.SearchBar
import com.example.colearnhub.ui.utils.animation
import com.example.colearnhub.ui.utils.dynamicPadding
import com.example.colearnhub.ui.utils.dynamicWidth
import com.example.colearnhub.ui.utils.txtSize
import com.example.colearnhub.ui.utils.verticalSpacing
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.colearnhub.viewModelLayer.AuthViewModelFactory
import com.example.colearnhub.viewmodel.AuthViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun InvitesLink(
    hasPendingInvites: Boolean,
    inviteCount: Int,
    onClick: () -> Unit
) {
    if (hasPendingInvites) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(top = 50.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = "Invites",
                tint = Color(0xFF395174),
                modifier = Modifier.size(23.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = stringResource(R.string.Invites),
                color = Color(0xFF395174),
                fontSize = 14.sp,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier
                    .clickable(onClick = onClick)
            )

            // Mostrar contador de convites pendentes
            if (inviteCount > 0) {
                Spacer(Modifier.width(4.dp))
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(
                            Color.Red,
                            shape = RoundedCornerShape(50)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = inviteCount.toString(),
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun GroupCard(
    groupResponse: GroupResponse,
    isOwner: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Nome do grupo e indicador de owner
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                groupResponse.group.name?.let {
                    Text(
                        text = it,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                if (isOwner) {
                    Text(
                        text = "Owner 👑",
                        fontSize = 12.sp,
                        color = Color(0xFF395174),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Número de membros
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Members",
                    tint = Color(0xFF666666),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${groupResponse.members.size}",
                    fontSize = 14.sp,
                    color = Color(0xFF666666)
                )
            }
        }
    }
}

@Composable
fun GroupsList(
    groups: List<GroupResponse>,
    currentUserId: String,
    onGroupClick: (GroupResponse) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        items(groups) { groupResponse ->
            val isOwner = groupResponse.group.owner_id == currentUserId
            GroupCard(
                groupResponse = groupResponse,
                isOwner = isOwner,
                onClick = { onGroupClick(groupResponse) }
            )
        }
    }
}

@Composable
fun ContentArea3(navController: NavController, currentUserId: String?) {
    val padding = dynamicPadding()
    val animationSize = animation()
    val titleFontSize = txtSize()
    val verticalSpacing = verticalSpacing()
    val btnHeight = verticalSpacing() + 10.dp

    var groups by remember { mutableStateOf<List<GroupResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var hasPendingInvites by remember { mutableStateOf(false) }
    var pendingInvitesCount by remember { mutableStateOf(0) }
    val groupRepository = remember { GroupRepository() }

    // Função para carregar dados
    suspend fun loadUserData() {
        if (currentUserId != null) {
            isLoading = true
            try {
                // Usar o método específico para grupos aceitos
                val acceptedGroups = groupRepository.getUserAcceptedGroups(currentUserId)

                // Verificar convites pendentes
                val pendingCount = groupRepository.getPendingInvitesCount(currentUserId)

                groups = acceptedGroups
                hasPendingInvites = pendingCount > 0
                pendingInvitesCount = pendingCount

                Log.d("ContentArea3", "Grupos aceitos: ${acceptedGroups.size}, Convites: $pendingCount")

            } catch (e: Exception) {
                Log.e("ContentArea3", "Erro ao carregar dados: ${e.message}")
            } finally {
                isLoading = false
            }
        } else {
            isLoading = false
        }
    }

    // Carregar dados quando o componente é criado ou quando volta de uma tela
    LaunchedEffect(currentUserId) {
        CoroutineScope(Dispatchers.IO).launch {
            loadUserData()
        }
    }

    // Recarregar dados quando volta para esta tela (para atualizar após aceitar/rejeitar convites)
    LaunchedEffect(navController.currentDestination?.route) {
        if (navController.currentDestination?.route == "groups" && currentUserId != null) {
            CoroutineScope(Dispatchers.IO).launch {
                loadUserData()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = padding)
    ) {
        Spacer(modifier = Modifier.height(verticalSpacing))

        // Botão New Group
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { navController.navigate("creategroup") },
                modifier = Modifier
                    .width(dynamicWidth(maxWidth = 300.dp))
                    .height(btnHeight)
                    .border(
                        width = 1.5.dp,
                        color = Color(0xFF395174),
                        shape = RoundedCornerShape(10.dp)
                    ),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF395174)
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Text(
                    text = stringResource(R.string.new_group),
                    fontSize = titleFontSize,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(verticalSpacing))
        }

        // Lista de grupos ou estado vazio
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
        } else if (groups.isNotEmpty() && currentUserId != null) {
            GroupsList(
                groups = groups,
                currentUserId = currentUserId,
                onGroupClick = { groupResponse ->
                    // Navegar para detalhes do grupo
                    navController.navigate("group_details/${groupResponse.group.id}")
                }
            )
        } else {
            // Estado vazio - mostrar animação
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.animation))

                LottieAnimation(
                    composition = composition,
                    modifier = Modifier.size(animationSize),
                    iterations = LottieConstants.IterateForever
                )

                Spacer(modifier = Modifier.height(verticalSpacing - 16.dp))

                Text(
                    text = stringResource(R.string.no_group),
                    fontSize = titleFontSize,
                    color = Color.Black,
                )

                Text(
                    text = stringResource(R.string.invitation),
                    fontSize = (titleFontSize.value - 2).sp,
                    color = Color.Black,
                    modifier = Modifier.padding(top = 4.dp),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun Indice3(navController: NavController) {
    // Obter currentUserId através do AuthViewModel
    val context = LocalContext.current
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(context)
    )
    val currentUser by authViewModel.currentUser.collectAsState()
    val currentUserId = currentUser?.id

    var hasPendingInvites by remember { mutableStateOf(false) }
    var pendingInvitesCount by remember { mutableStateOf(0) }
    val groupRepository = remember { GroupRepository() }

    // Função para carregar convites pendentes
    suspend fun loadPendingInvites() {
        if (currentUserId != null) {
            try {
                val count = groupRepository.getPendingInvitesCount(currentUserId)
                hasPendingInvites = count > 0
                pendingInvitesCount = count
                Log.d("Indice3", "Convites pendentes: $count")
            } catch (e: Exception) {
                Log.e("Indice3", "Erro ao carregar convites: ${e.message}")
            }
        }
    }

    // Verificar convites pendentes na inicialização
    LaunchedEffect(currentUserId) {
        CoroutineScope(Dispatchers.IO).launch {
            loadPendingInvites()
        }
    }

    // Recarregar convites quando volta para esta tela
    LaunchedEffect(navController.currentDestination?.route) {
        if (navController.currentDestination?.route == "groups") {
            CoroutineScope(Dispatchers.IO).launch {
                loadPendingInvites()
            }
        }
    }

    InvitesLink(
        hasPendingInvites = hasPendingInvites,
        inviteCount = pendingInvitesCount,
        onClick = {
            // Navegar para tela de convites
            navController.navigate("invites")
        }
    )
    ContentArea3(navController, currentUserId)
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun GroupsScreen(navController: NavController) {
    var selectedItem by remember { mutableIntStateOf(3) }

    Box(
        modifier = Modifier.fillMaxSize()
            .background(Color.White)
    ) {
        if(selectedItem == 0 || selectedItem == 1 || selectedItem == 3 || selectedItem == 4) {
            Circles()
        }

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            if(selectedItem == 0){
                SearchBar()
            }
            if(selectedItem == 1) {
                SBar(title = stringResource(R.string.study_session))
            }
            if(selectedItem == 3) {
                SBar(title = stringResource(R.string.Groups))
                Indice3(navController)
            } else {
                ScreenContent(selectedItem, navController)
            }
        }

        if (selectedItem == 0 || selectedItem == 1 || selectedItem == 2 || selectedItem == 3 || selectedItem == 4) {
            Nav(
                selectedItem = selectedItem,
                onItemSelected = { newIndex ->
                    selectedItem = newIndex
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
            )
        }
    }
}