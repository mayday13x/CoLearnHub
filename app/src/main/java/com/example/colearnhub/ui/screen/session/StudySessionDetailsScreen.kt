package com.example.colearnhub.ui.screen.session

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.colearnhub.modelLayer.StudySession
import com.example.colearnhub.modelLayer.TagData
import com.example.colearnhub.ui.screen.main.formatDate
import com.example.colearnhub.ui.screen.main.formatTime
import com.example.colearnhub.ui.utils.dynamicPadding
import com.example.colearnhub.ui.utils.txtSize
import com.example.colearnhub.ui.utils.verticalSpacing
import com.example.colearnhub.viewModelLayer.StudySessionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudySessionDetailsTopBar(navController: NavController) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(R.string.study_session_details),
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color(0xFF395174)
        )
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StudySessionDetailsScreen(
    navController: NavController,
    viewModel: StudySessionViewModel = viewModel()
) {
    val selectedStudySession by viewModel.selectedStudySession.collectAsState()
    val isUserParticipating by viewModel.isUserParticipating.collectAsState()
    val isLoading by viewModel.isLoadingDetails.collectAsState()
    val error by viewModel.error.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val sessionId = navController.currentBackStackEntry?.arguments?.getString("sessionId")
        Log.d("StudySessionDetailsScreen", "Retrieved Session ID from Nav Arguments: $sessionId")
        if (sessionId == null) {
            Log.e("StudySessionDetailsScreen", "Session ID is null from navigation arguments.")
            // Optionally, handle this case by navigating back or showing a specific error
        }
        sessionId?.let { viewModel.loadStudySessionDetails(it) }
    }

    LaunchedEffect(selectedStudySession, isLoading, error) {
        Log.d("StudySessionDetailsScreen", "ViewModel State - selectedStudySession: ${selectedStudySession?.name}, isLoading: $isLoading, error: $error")
        selectedStudySession?.let { session ->
            Log.d("StudySessionDetails", "Selected session: ${session.name}, Creator ID: ${session.creatorId}, Participants: ${session.sessionParticipants?.size}")
            // You might want to refresh participation status here if necessary
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        StudySessionDetailsTopBar(navController = navController)

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else if (error != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = stringResource(R.string.error_loading_details, error ?: "Unknown Error"), color = Color.Red)
            }
        } else if (selectedStudySession != null) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = dynamicPadding())
                    .padding(bottom = 80.dp), // Padding for the bottom navigation bar
                verticalArrangement = Arrangement.spacedBy(verticalSpacing())
            ) {
                item {
                    Spacer(modifier = Modifier.height(verticalSpacing()))

                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(R.string.session_name),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = selectedStudySession!!.name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.height(verticalSpacing() / 2))

                        // Description
                        if (selectedStudySession!!.description.isNotEmpty()) {
                            Text(
                                text = stringResource(R.string.description),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = selectedStudySession!!.description,
                                fontSize = 16.sp,
                                color = Color.Black,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(verticalSpacing()))

                        // Date, Time, Duration
                        Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min), horizontalArrangement = Arrangement.SpaceBetween) {
                            // Date
                            OutlinedButton(
                                onClick = { /* No action on click */ },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).height(65.dp).padding(end = 4.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = Color.LightGray.copy(alpha = 0.2f),
                                    contentColor = Color.Black
                                )
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                    Icon(Icons.Filled.DateRange, contentDescription = "Date", modifier = Modifier.size(24.dp), tint = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = formatDate(selectedStudySession!!.date),
                                        fontSize = 12.sp,
                                        textAlign = TextAlign.Center,
                                        lineHeight = 16.sp,
                                        softWrap = true,
                                        maxLines = 2
                                    )
                                }
                            }

                            // Start Time
                            OutlinedButton(
                                onClick = { /* No action on click */ },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).height(65.dp).padding(horizontal = 4.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = Color.LightGray.copy(alpha = 0.2f),
                                    contentColor = Color.Black
                                )
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.Schedule, contentDescription = "Time", modifier = Modifier.size(24.dp), tint = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = formatTime(selectedStudySession!!.startTime), fontSize = 14.sp)
                                }
                            }

                            // Duration
                            OutlinedButton(
                                onClick = { /* No action on click */ },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).height(65.dp).padding(start = 4.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = Color.LightGray.copy(alpha = 0.2f),
                                    contentColor = Color.Black
                                )
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.HourglassEmpty, contentDescription = "Duration", modifier = Modifier.size(24.dp), tint = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "${selectedStudySession!!.duration} min", fontSize = 14.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(verticalSpacing()))

                        // Tags
                        selectedStudySession!!.embeddedTag?.let { tag ->
                            Text(
                                text = stringResource(R.string.tags),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            StudySessionDetailTag(tag = tag)
                        }

                        Spacer(modifier = Modifier.height(verticalSpacing()))

                        // Going / Participants
                        Text(
                            text = stringResource(R.string.going),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { /* Navigate to participants list */ }
                                .background(Color.LightGray.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.People, contentDescription = "Participants", tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = stringResource(R.string.participants_count, selectedStudySession!!.numParticipants))
                            }
                            Icon(Icons.Filled.Share, contentDescription = "Share") // Placeholder for navigation arrow
                        }

                        Spacer(modifier = Modifier.height(verticalSpacing()))

                        // Session Link (conditional visibility)
                        if (isUserParticipating) {
                            selectedStudySession!!.sessionLink?.let { link ->
                                Text(
                                    text = stringResource(R.string.session_s_link),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = link,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .clickable { val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link)); context.startActivity(intent) }
                                        .padding(top = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(verticalSpacing() * 2))

                        // Join/Leave Button
                        Button(
                            onClick = {
                                if (isUserParticipating) {
                                    viewModel.leaveSession(selectedStudySession!!.id.toString())
                                } else {
                                    viewModel.joinSession(selectedStudySession!!.id.toString())
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = if (isUserParticipating) stringResource(R.string.leave) else stringResource(R.string.join),
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        } else {
            // Should not happen if loading and error are handled
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = stringResource(R.string.session_not_found), color = Color.Black)
            }
        }
    }
}

@Composable
fun StudySessionDetailTag(
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
        "History" to Color(0xFFFFFF00),    // Amarelo
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
