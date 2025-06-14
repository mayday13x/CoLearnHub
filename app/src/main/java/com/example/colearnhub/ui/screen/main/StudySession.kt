package com.example.colearnhub.ui.screen.main

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.colearnhub.R
import com.example.colearnhub.modelLayer.StudySession
import com.example.colearnhub.ui.utils.Circles
import com.example.colearnhub.ui.utils.Nav
import com.example.colearnhub.ui.utils.SBar
import com.example.colearnhub.ui.utils.ScreenContent
import com.example.colearnhub.ui.utils.SearchBar
import com.example.colearnhub.ui.utils.animation
import com.example.colearnhub.ui.utils.btnHeight
import com.example.colearnhub.ui.utils.dynamicPadding
import com.example.colearnhub.ui.utils.dynamicWidth
import com.example.colearnhub.ui.utils.txtSize
import com.example.colearnhub.ui.utils.verticalSpacing
import com.example.colearnhub.viewModelLayer.StudySessionViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.RssFeed
import android.util.Log
import java.time.OffsetTime
import com.example.colearnhub.modelLayer.TagData
import androidx.compose.ui.platform.LocalContext
import com.example.colearnhub.viewModelLayer.AuthViewModelFactory
import com.example.colearnhub.viewmodel.AuthViewModel
import androidx.compose.foundation.clickable

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Indice2(navController: NavController? = null, viewModel: StudySessionViewModel = viewModel()){
    var selectedTab by remember { mutableIntStateOf(0) }
    val label1 = stringResource(R.string.All)
    val label2 = stringResource(R.string.Joined)
    val label3 = stringResource(R.string.Created2)
    val tabs = listOf(label1, label2, label3)

    val futureStudySessions by viewModel.futureStudySessions.collectAsState()
    val joinedStudySessions by viewModel.joinedStudySessions.collectAsState()
    val createdStudySessions by viewModel.createdStudySessions.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // Combine joined and created sessions for the Joined tab
    val combinedJoinedSessions = remember(joinedStudySessions, createdStudySessions) {
        (joinedStudySessions + createdStudySessions).distinctBy { it.id }
    }

    // Log current state for debugging
    LaunchedEffect(futureStudySessions, isLoading, error) {
        Log.d("StudySessionUI", "futureStudySessions size: ${futureStudySessions.size}, isLoading: $isLoading, error: $error")
    }

    // Load sessions when component mounts
    LaunchedEffect(Unit) {
        viewModel.loadFutureStudySessions()
        viewModel.loadJoinedStudySessions()
        viewModel.loadCreatedStudySessions()
    }

    dynamicPadding()
    val verticalSpacing = verticalSpacing()
    val btnHeight = btnHeight()
    val txtSize = txtSize()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Spacer(modifier = Modifier.height(verticalSpacing))
        NewSessionBtn(onSettingsClick = {
            navController?.navigate("new_session")
        })
        Spacer(modifier = Modifier.height(verticalSpacing - 10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tabs.forEachIndexed { index, title ->
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Button(
                        onClick = { selectedTab = index },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(btnHeight),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedTab == index) Color(0xC9E9F2FF) else Color.Transparent,
                            contentColor = Color(0xFF395174)
                        ),
                        elevation = ButtonDefaults.buttonElevation(0.dp)
                    ) {
                        Text(
                            text = title,
                            fontSize = txtSize,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF395174)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .background(
                                if (selectedTab == index) Color(0xFF395174) else Color.Transparent
                            )
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(verticalSpacing - 30.dp))

        error?.let { errorMessage ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    fontSize = txtSize,
                    textAlign = TextAlign.Center
                )
            }
        }

        when (selectedTab) {
            0 -> ContentArea2(
                sessions = futureStudySessions,
                isLoading = isLoading,
                emptyMessage = stringResource(R.string.not_found2),
                navController = navController
            )
            1 -> ContentArea2(
                sessions = combinedJoinedSessions,
                isLoading = isLoading,
                emptyMessage = "You haven't joined or created any study sessions yet",
                navController = navController
            )
            2 -> ContentArea2(
                sessions = createdStudySessions,
                isLoading = isLoading,
                emptyMessage = "You haven't created any study sessions yet",
                navController = navController
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ContentArea2(
    sessions: List<StudySession> = emptyList(),
    isLoading: Boolean = false,
    emptyMessage: String = "No sessions found",
    navController: NavController?
) {
    val padding = dynamicPadding()
    val animationSize = animation()
    val titleFontSize = txtSize()
    val verticalSpacing = verticalSpacing()

    // Filter out past sessions
    val filteredSessions = sessions.filter { !isSessionPast(it) }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = Color(0xFF395174),
                modifier = Modifier.size(40.dp)
            )
        }
    } else if (filteredSessions.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = padding)
        ) {
            Spacer(modifier = Modifier.height(verticalSpacing - 10.dp))

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
                    text = emptyMessage,
                    fontSize = titleFontSize,
                    color = Color.Black,
                )

                Text(
                    text = stringResource(R.string.Knowledge2),
                    fontSize = (titleFontSize.value - 2).sp,
                    color = Color.Black,
                    modifier = Modifier.padding(top = 4.dp),
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.weight(1f))
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = padding)
                .padding(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredSessions) { session ->
                StudySessionCard(session = session, navController = navController)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StudySessionCard(session: StudySession, navController: NavController?) {
    val isLive = isSessionLive(session)
    val context = LocalContext.current
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(context)
    )
    val currentUser by authViewModel.currentUser.collectAsState()
    val isOwner = session.creatorId == currentUser?.id

    // Debug logs
    LaunchedEffect(session, currentUser) {
        Log.d("StudySessionCard", "Session creatorId: ${session.creatorId}")
        Log.d("StudySessionCard", "Current user id: ${currentUser?.id}")
        Log.d("StudySessionCard", "Is owner: $isOwner")
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable {
                navController?.navigate("study_session_details/${session.id}")
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = session.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    if (session.description.isNotEmpty()) {
                        Text(
                            text = session.description,
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (isOwner) {
                        Text(
                            text = "Owner 👑",
                            fontSize = 12.sp,
                            color = Color(0xFF395174),
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    if (isLive) {
                        Icon(
                            imageVector = Icons.Filled.RssFeed,
                            contentDescription = "LIVE",
                            modifier = Modifier.size(16.dp),
                            tint = Color.Red
                        )
                        Text(
                            text = "LIVE",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Red
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Display Tag if available
            session.embeddedTag?.let { tag ->
                StudySessionTag(tag = tag)
                Spacer(modifier = Modifier.height(8.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.DateRange,
                        contentDescription = "Date",
                        modifier = Modifier.size(16.dp),
                        tint = Color.Gray
                    )
                    Text(
                        text = formatDate(session.date),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Schedule,
                        contentDescription = "Time",
                        modifier = Modifier.size(16.dp),
                        tint = Color.Gray
                    )
                    Text(
                        text = formatTime(session.startTime),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.HourglassEmpty,
                        contentDescription = "Duration",
                        modifier = Modifier.size(16.dp),
                        tint = Color.Gray
                    )
                    Text(
                        text = "${session.duration} min",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun isSessionLive(session: StudySession): Boolean {
    val currentDate = LocalDate.now()
    val currentTime = LocalTime.now()

    // Parse session date
    val sessionDate = LocalDate.parse(session.date)

    // Define a formatter for the expected startTime format (e.g., "HH:mm:ss+00")
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ssX")

    // Parse session start time using the defined formatter, then convert to LocalTime
    val sessionStartTime = OffsetTime.parse(session.startTime, timeFormatter).toLocalTime()

    // Calculate session end time
    val sessionEndTime = sessionStartTime.plusMinutes(session.duration)

    // Check if session is today and currently running
    return sessionDate == currentDate &&
            currentTime.isAfter(sessionStartTime) &&
            currentTime.isBefore(sessionEndTime)
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatDate(dateString: String): String {
    return try {
        val date = LocalDate.parse(dateString)
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        date.format(formatter)
    } catch (e: Exception) {
        dateString
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatTime(timeString: String): String {
    return try {
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ssX")
        val time = OffsetTime.parse(timeString, timeFormatter)
        time.format(DateTimeFormatter.ofPattern("HH:mm"))
    } catch (e: Exception) {
        timeString
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun isSessionPast(session: StudySession): Boolean {
    val currentDate = LocalDate.now()
    val currentTime = LocalTime.now()
    val sessionDate = LocalDate.parse(session.date)
    
    // If session date is before today, it's past
    if (sessionDate.isBefore(currentDate)) {
        return true
    }
    
    // If session is today, check if the time has passed
    if (sessionDate == currentDate) {
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ssX")
        val sessionStartTime = OffsetTime.parse(session.startTime, timeFormatter).toLocalTime()
        val sessionEndTime = sessionStartTime.plusMinutes(session.duration)
        
        return currentTime.isAfter(sessionEndTime)
    }
    
    return false
}

@Composable
fun NewSessionBtn(onSettingsClick: () -> Unit){
    val btnHeight = verticalSpacing() + 10.dp
    val titleFontSize = txtSize()

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onSettingsClick,
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
                text = stringResource(R.string.New_Session),
                fontSize = titleFontSize,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StudySessionScreen(navController: NavController) {
    var selectedItem by remember { mutableIntStateOf(1) }

    Box(
        modifier = Modifier.fillMaxSize()
            .background(Color.White)
    ) {
        if(selectedItem == 0 || selectedItem == 1 || selectedItem == 3 || selectedItem == 4) {
            Circles()
        }

        // Main content column that takes space above the nav bar
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp) // Adjust this padding based on actual Nav bar height
        ) {
            if(selectedItem == 0){
                SearchBar()
            }
            if(selectedItem == 1) {
                SBar(title = stringResource(R.string.study_session))
            }
            if(selectedItem == 3) {
                SBar(title = stringResource(R.string.Groups))
            }
            ScreenContent(selectedItem, navController)
        }

        // Nav bar, aligned to the bottom of the outer Box
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

@Composable
fun StudySessionTag(
    tag: TagData
) {
    // Map of tag descriptions to specific colors
    val tagColorMap = mapOf(
        "Math" to Color(0xFF4A90E2),      // Azul
        "Physics" to Color(0xFF50C878),    // Verde
        "Chemistry" to Color(0xFFFFA500),  // Laranja
        "Biology" to Color(0xFFE91E63),    // Rosa
        "Computer Science" to Color(0xFF9C27B0), // Roxo
        "Languages" to Color(0xFF00BCD4),  // Ciano
        "History" to Color(0xFFFF6B6B),    // Vermelho
        "Geography" to Color(0xFF795548)   // Marrom
    )

    // Get color from map or use a default color if tag not found
    val tagColor = tagColorMap[tag.description] ?: Color(0xFF4A90E2)

    Box(
        modifier = Modifier
            .background(
                color = tagColor,
                shape = RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = tag.description,
            fontSize = 10.sp,
            color = Color.White,
            fontWeight = FontWeight.Medium
        )
    }
}