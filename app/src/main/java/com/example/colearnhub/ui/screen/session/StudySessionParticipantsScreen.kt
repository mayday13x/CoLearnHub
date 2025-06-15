package com.example.colearnhub.ui.screen.session

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.colearnhub.R
import com.example.colearnhub.ui.utils.logoSize
import com.example.colearnhub.ui.utils.sbutton
import com.example.colearnhub.ui.utils.spacer3
import com.example.colearnhub.ui.utils.txtSize
import com.example.colearnhub.viewModelLayer.StudySessionViewModel
import com.example.colearnhub.repositoryLayer.UserRepository

data class SessionParticipantData(
    val id: String,
    val username: String,
    val email: String? = null,
    val isOwner: Boolean = false
)

@Composable
fun SessionParticipantsTopBar(onBack: () -> Unit) {
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
            text = stringResource(R.string.participants),
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
fun SessionSearchBar(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = searchText,
        onValueChange = onSearchTextChange,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.Black),
        placeholder = {
            Text(
                text = stringResource(R.string.search_participants),
                color = Color.Gray
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = Color.Gray
            )
        },
        shape = RoundedCornerShape(8.dp),
        singleLine = true,
    )
}

@Composable
fun SessionParticipantItem(
    participant: SessionParticipantData,
    modifier: Modifier = Modifier
) {
    val sizeValue = sbutton() + 8.dp

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile picture placeholder
            Box(
                modifier = Modifier
                    .size(sizeValue)
                    .clip(CircleShape)
                    .background(Color.Gray.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    tint = Color.Gray,
                    modifier = Modifier.size((sizeValue.value * 0.6f).dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // User information
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = participant.username,
                    fontSize = txtSize(),
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                participant.email?.let { email ->
                    Text(
                        text = email,
                        fontSize = (txtSize().value - 2).sp,
                        color = Color.Gray
                    )
                }
            }

            // Owner badge
            if (participant.isOwner) {
                Surface(
                    color = Color(0xFFE53E3E),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.owner_badge),
                        color = Color.White,
                        fontSize = (txtSize().value - 4).sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SessionParticipantsList(
    participants: List<SessionParticipantData>,
    searchText: String,
    modifier: Modifier = Modifier
) {
    val filteredParticipants = participants.filter { participant ->
        participant.username.contains(searchText, ignoreCase = true) ||
                participant.email?.contains(searchText, ignoreCase = true) == true
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(filteredParticipants) { participant ->
            SessionParticipantItem(participant = participant)
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun StudySessionParticipantsScreen(
    navController: NavHostController,
    sessionId: String,
    viewModel: StudySessionViewModel
) {
    val selectedStudySession by viewModel.selectedStudySession.collectAsState()
    val userRepository = remember { UserRepository() }
    var participants by remember { mutableStateOf<List<SessionParticipantData>>(emptyList()) }
    var searchText by remember { mutableStateOf("") }
    var isLoadingParticipants by remember { mutableStateOf(false) }

    // Load session details when screen is opened
    LaunchedEffect(sessionId) {
        viewModel.loadStudySessionDetails(sessionId)
    }

    // Load participants when session details are available
    LaunchedEffect(selectedStudySession) {
        selectedStudySession?.let { session ->
            isLoadingParticipants = true
            val participantsList = mutableListOf<SessionParticipantData>()

            try {
                // Add session owner
                val owner = userRepository.getUserById(session.creatorId)
                owner?.let {
                    participantsList.add(
                        SessionParticipantData(
                            id = it.id,
                            username = it.username,
                            email = it.email,
                            isOwner = true
                        )
                    )
                }

                // Add session participants
                session.sessionParticipants?.forEach { participant ->
                    val user = userRepository.getUserById(participant.userId)
                    user?.let {
                        participantsList.add(
                            SessionParticipantData(
                                id = it.id,
                                username = it.username,
                                email = it.email,
                                isOwner = false
                            )
                        )
                    }
                }

                // Sort: owner first, then alphabetically
                participants = participantsList.sortedWith(
                    compareByDescending<SessionParticipantData> { it.isOwner }
                        .thenBy { it.username }
                )
            } catch (e: Exception) {
                // Handle error
            } finally {
                isLoadingParticipants = false
            }
        }
    }

    fun navigateBack() {
        navController.popBackStack()
    }

    BackHandler(onBack = ::navigateBack)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        SessionParticipantsTopBar(onBack = ::navigateBack)

        SessionSearchBar(
            searchText = searchText,
            onSearchTextChange = { searchText = it }
        )

        when {
            isLoadingParticipants -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF395174))
                }
            }
            else -> {
                SessionParticipantsList(
                    participants = participants,
                    searchText = searchText
                )
            }
        }
    }
} 