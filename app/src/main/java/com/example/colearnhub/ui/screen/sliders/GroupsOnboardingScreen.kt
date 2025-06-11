package com.example.colearnhub.ui.screen.sliders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.colearnhub.R

@Composable
fun GroupsOnboardingScreen(
    navController: NavController,
    onDone: () -> Unit = { navController.navigate("login") }
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFB0B0B0))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF4A6FA5))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Progress indicators
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(4) { index ->
                        Box(
                            modifier = Modifier
                                .width(if (index == 3) 24.dp else 8.dp)
                                .height(4.dp)
                                .background(
                                    color = if (index == 3) Color.White else Color.White.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(2.dp)
                                )
                        )
                        if (index < 3) {
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Title
                Text(
                    text = stringResource(R.string.onboarding_groups_title),
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Icon (groups/people illustration)
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(Color.Transparent),
                    contentAlignment = Alignment.Center
                ) {
                    // Simple groups representation using text
                    Text(
                        text = "👥",
                        fontSize = 64.sp,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Description
                Text(
                    text = stringResource(R.string.onboarding_groups_description),
                    color = Color.White,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )

                Spacer(modifier = Modifier.height(48.dp))

                // Done button (aligned to end)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onDone,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White.copy(alpha = 0.2f)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.button_done),
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }

        // Copyright text at bottom
        Text(
            text = stringResource(R.string.copyright_text),
            color = Color.Gray,
            fontSize = 12.sp,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}