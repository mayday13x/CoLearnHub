package com.example.colearnhub.ui.screen.main

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.colearnhub.R

// Composable function to create the UI
@Composable
fun Group46() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawIntoCanvas {
            // Draw the first circle
            translate(left = -size.width / 4, top = -(size.height / 2) * 1.04f) {
                drawCircle(
                    color = Color(0xF54A6FA5),
                    radius = size.width / 1.65f
                )
            }
            // Draw the second circle
            translate(left = size.width / 4, top = -(size.height / 2)) {
                drawCircle(
                    color = Color(0xFF4A6FA5),
                    radius = size.width / 1.65f
                )
            }
        }
    }
}

@Composable
fun Group34() {
    Column(
        modifier = Modifier
            .padding(26.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.cubewhite),
            contentDescription = "Logo",
            modifier = Modifier.size(40.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Box {
                Text(
                    text = "COLEARNHUB",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    style = androidx.compose.ui.text.TextStyle(
                        drawStyle = Stroke(width = 2f)
                    )
                )
            }
        }
        Spacer(Modifier.height(27.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(10))
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Search, contentDescription = "Search")
            Spacer(Modifier.width(8.dp))
            Text(
                "Search",
                color = Color.Gray,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium
            )
            Icon(Icons.Default.FilterList, contentDescription = "Filter")
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                "Share your knowledge",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun Indice(){
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("All", "Created")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Spacer(modifier = Modifier.height(40.dp))
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
                            .height(35.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedTab == index) Color(0xC9E9F2FF) else Color.Transparent,
                            contentColor = Color(0xFF395174)
                        ),
                        elevation = ButtonDefaults.buttonElevation(0.dp)
                    ) {
                        Text(
                            text = title,
                            fontSize = 16.sp,
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
        Spacer(modifier = Modifier.height(10.dp))
        ContentArea()
    }
}

@Composable
fun ContentArea(){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {

        Spacer(modifier = Modifier.height(40.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp),
                contentAlignment = Alignment.Center
            ) {
                val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.animation))

                LottieAnimation(
                    composition = composition,
                    modifier = Modifier.size(120.dp),
                    iterations = LottieConstants.IterateForever
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "No material was found ...",
                fontSize = 16.sp,
                color = Color.Black,
            )

            Text(
                text = "Be the first to share your knowledge !",
                fontSize = 14.sp,
                color = Color.Black,
                modifier = Modifier.padding(top = 4.dp),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(horizontal = 20.dp)
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
                    text = "Share",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun ProfileScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Profile Screen",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF395174)
        )
    }
}

@Composable
fun Nav(onNavigateToProfile: () -> Unit = {}) { // Adicionar este parâmetro
    var selectedItem by remember { mutableIntStateOf(0) }

    val bottomNavItems = listOf(
        BottomNavItem("Home", drawableRes = R.drawable.cube),
        BottomNavItem("Sessions", icon = Icons.Default.Schedule),
        BottomNavItem("Share", icon = Icons.Default.Add),
        BottomNavItem("Groups", icon = Icons.Default.Group),
        BottomNavItem("Profile", icon = Icons.Default.Person)
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        NavigationBar(
            containerColor = Color.White,
            modifier = Modifier.height(80.dp)
                .drawBehind {
                    val strokeWidth = 1.5.dp.toPx()
                    drawLine(
                        color = Color(0xFF395174),
                        start = Offset(0f, 0f),
                        end = Offset(size.width, 0f),
                        strokeWidth = strokeWidth
                    )
                }
        ) {
            bottomNavItems.forEachIndexed { index, item ->
                NavigationBarItem(
                    selected = selectedItem == index,
                    onClick = {
                        selectedItem = index
                        if (index == 4) { // Profile é o índice 4
                            onNavigateToProfile()
                        }
                    },
                    icon = {
                        if (index == 2) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(
                                        color = Color(0xFFFFFFFF),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .border(
                                        width = 1.5.dp,
                                        color = if (selectedItem == index) Color(0xFF395174) else Color.Gray,
                                        shape = RoundedCornerShape(10.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = item.icon!!,
                                    contentDescription = item.label,
                                    tint = if (selectedItem == index) Color(0xFF395174) else Color.Gray,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        } else {
                            if (item.drawableRes != null) {
                                Image(
                                    painter = painterResource(id = item.drawableRes),
                                    contentDescription = item.label,
                                    colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(
                                        if (selectedItem == index) Color(0xFF395174) else Color.Gray
                                    ),
                                    modifier = Modifier.size(24.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = item.icon!!,
                                    contentDescription = item.label,
                                    tint = if (selectedItem == index) Color(0xFF395174) else Color.Gray
                                )
                            }
                        }
                    },
                    label = {
                        Text(
                            text = item.label,
                            fontSize = 12.sp,
                            color = if (selectedItem == index) Color(0xFF395174) else Color.Gray
                        )
                    }
                )
            }
        }
    }
}

data class BottomNavItem(
    val label: String,
    val icon: ImageVector? = null,
    val drawableRes: Int? = null
)

@Composable
fun MainScreen(onNavigateToProfile: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Group46()

        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Group34()
            Indice()
        }

        Nav(onNavigateToProfile = onNavigateToProfile)
    }
}


@Preview(showBackground = true)
@Composable
fun Preview() {
    MainScreen()
}

