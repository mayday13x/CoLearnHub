package com.example.colearnhub.ui.screen.group

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.colearnhub.repositoryLayer.User
import com.example.colearnhub.viewModelLayer.AuthViewModelFactory
import com.example.colearnhub.viewModelLayer.GroupViewModel
import com.example.colearnhub.viewmodel.AuthViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGroupScreen(
    onNavigateBack: () -> Unit,
    onGroupCreated: () -> Unit,
    viewModel: GroupViewModel = viewModel()
) {
    val uiState by viewModel.createGroupUiState.collectAsState()
    val context = LocalContext.current

    // Observar quando o grupo é criado
    LaunchedEffect(uiState.isCreated) {
        if (uiState.isCreated) {
            onGroupCreated()
            viewModel.resetCreateGroupState()
        }
    }

    // Mostrar mensagens de erro
    uiState.errorMessage?.let { error ->
        LaunchedEffect(error) {
            // Aqui você pode mostrar um Snackbar ou Toast
            viewModel.clearErrorMessage()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Top Bar
        TopAppBar(
            title = {
                Text(
                    text = "New Group",
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp
                )
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Voltar"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFF395174),
                titleContentColor = Color.White,
                navigationIconContentColor = Color.White
            )
        )

        // Conteúdo
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Campo Nome
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Name",
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = Color(0xFF395174)
                )

                OutlinedTextField(
                    value = uiState.groupName,
                    onValueChange = viewModel::updateGroupName,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF395174),
                        unfocusedBorderColor = Color(0xFFE0E0E0)
                    ),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true
                )
            }

            // Campo Descrição
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Description",
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = Color(0xFF395174)
                )

                OutlinedTextField(
                    value = uiState.groupDescription,
                    onValueChange = viewModel::updateGroupDescription,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF395174),
                        unfocusedBorderColor = Color(0xFFE0E0E0)
                    ),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    maxLines = 4
                )
            }

            // Seção Convidar Pessoas
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Invite people",
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = Color(0xFF395174)
                )

                // Campo de pesquisa
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = viewModel::updateSearchQuery,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "Search users...",
                            color = Color(0xFF9E9E9E)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Pesquisar",
                            tint = Color(0xFF9E9E9E)
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF395174),
                        unfocusedBorderColor = Color(0xFFE0E0E0)
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Search
                    ),
                    singleLine = true
                )

                // Resultados da pesquisa
                if (uiState.isSearching) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF395174),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                } else if (uiState.searchResults.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 200.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.searchResults) { user ->
                            UserSearchItem(
                                user = user,
                                onInvite = { viewModel.addUserToInvite(user) }
                            )
                        }
                    }
                }

                // Usuários convidados
                if (uiState.invitedUsers.isNotEmpty()) {
                    Text(
                        text = "Invited (${uiState.invitedUsers.size})",
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        color = Color(0xFF666666)
                    )

                    LazyColumn(
                        modifier = Modifier.heightIn(max = 150.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.invitedUsers) { user ->
                            InvitedUserItem(
                                user = user,
                                onRemove = { viewModel.removeUserFromInvite(user) }
                            )
                        }
                    }
                }

                // Estado vazio quando não há pesquisa
                if (uiState.searchQuery.isEmpty() && uiState.invitedUsers.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PersonAdd,
                                contentDescription = "Convidar pessoas",
                                tint = Color(0xFF395174),
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                text = "Invite new people to this group.",
                                fontSize = 14.sp,
                                color = Color(0xFF9E9E9E),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            val authViewModel: AuthViewModel = viewModel(
                factory = AuthViewModelFactory(context)
            )

            val currentUser by authViewModel.currentUser.collectAsState()
            val currentUserId = currentUser?.id
            // Botão Criar
            Button(
                onClick = {
                    currentUser?.id?.let { viewModel.createGroup(it) }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF395174)
                ),
                shape = RoundedCornerShape(8.dp),
                enabled = !uiState.isLoading && uiState.groupName.trim().isNotEmpty()
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(
                        text = "Create",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun UserSearchItem(
    user: User,
    onInvite: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onInvite() },
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF395174)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = user.username?.take(1)?.uppercase() ?: "U",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.username ?: "Usuário",
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = Color(0xFF2C3E50)
                )
                user.email?.let { email ->
                    Text(
                        text = email,
                        fontSize = 12.sp,
                        color = Color(0xFF666666)
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Convidar",
                tint = Color(0xFF395174)
            )
        }
    }
}

@Composable
fun InvitedUserItem(
    user: com.example.colearnhub.repositoryLayer.User,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF0F8FF)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF395174)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = user.username?.take(1)?.uppercase() ?: "U",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.username ?: "Usuário",
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = Color(0xFF2C3E50)
                )
                Text(
                    text = "Invited",
                    fontSize = 12.sp,
                    color = Color(0xFF395174)
                )
            }

            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remover",
                    tint = Color(0xFF666666),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}