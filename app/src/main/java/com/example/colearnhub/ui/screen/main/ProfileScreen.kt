package com.example.colearnhub.ui.screen.main

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.colearnhub.R
import com.example.colearnhub.ui.utils.Circles
import com.example.colearnhub.ui.utils.Nav
import com.example.colearnhub.ui.utils.SBar
import com.example.colearnhub.ui.utils.ScreenContent
import com.example.colearnhub.ui.utils.SearchBar
import com.example.colearnhub.ui.utils.dynamicWidth
import com.example.colearnhub.ui.utils.logoSize
import com.example.colearnhub.ui.utils.spacer
import com.example.colearnhub.ui.utils.titleFontSize
import com.example.colearnhub.ui.utils.txtSize
import com.example.colearnhub.ui.utils.verticalSpacing
import com.example.colearnhub.viewmodel.UserViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar2(onSettingsClick: () -> Unit, onFavouritesClick: () -> Unit) {
    val logoSize = logoSize()
    val titleFontSize = titleFontSize()
    val paddingValue = logoSize() - 10.dp
    val top = logoSize() + 50.dp

    TopAppBar(
        modifier = Modifier.height(top),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = paddingValue - 15.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(modifier = Modifier.height(paddingValue))
                    Image(
                        painter = painterResource(id = R.drawable.cubewhite),
                        contentDescription = "Logo",
                        modifier = Modifier.size(logoSize)
                    )
                    Text(
                        text = "COLEARNHUB",
                        fontSize = titleFontSize,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        style = androidx.compose.ui.text.TextStyle(
                            drawStyle = Stroke(width = 2f)
                        )
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                IconButton(
                    onClick = onFavouritesClick,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Favorites",
                        tint = Color.White
                    )
                }

                IconButton(onClick = onSettingsClick) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = Color.White
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        )
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Identity(
    modifier: Modifier = Modifier,
    userViewModel: UserViewModel = viewModel()
) {

    val titleFontSize = (txtSize().value + 2).sp
    val sizeValue = logoSize()
    val user by userViewModel.user.collectAsState()


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
    ) {
        if (user == null) {
            Box(
                modifier = Modifier
                    .size(sizeValue + 30.dp)
                    .clip(CircleShape)
                    .border(3.dp, Color.White, CircleShape)
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(40.dp),
                    color = Color.White
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .size(sizeValue + 30.dp)
                    .clip(CircleShape)
                    .border(3.dp, Color.White, CircleShape)
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    tint = Color.White,
                    modifier = Modifier.size(sizeValue)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = user!!.name,
                fontSize = titleFontSize,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = "@${user!!.username}",
                fontSize = (titleFontSize.value - 4).sp,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun StatsCardGroup(
    userViewModel: UserViewModel = viewModel()
) {
    val sizeValue = logoSize() - 20.dp
    val padding = logoSize() + 10.dp
    val titleFontSize = (txtSize().value + 4).sp
    val contributions by userViewModel.userContributions.collectAsState()
    val averageRating by userViewModel.averageRating.collectAsState()
    val user by userViewModel.user.collectAsState()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = (-(padding - 10.dp)))
            .padding(horizontal = padding),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(15.dp)
    ) {
        if (user == null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(40.dp),
                    color = Color(0xFF4A6FA5)
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.cube),
                            contentDescription = "Logo",
                            modifier = Modifier.size(sizeValue)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = contributions.toString(),
                            fontSize = titleFontSize,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.contributions_label),
                        fontSize = (titleFontSize.value - 8).sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Medium
                    )
                }

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(padding + 20.dp)
                        .padding(vertical = 4.dp)
                        .background(Color.LightGray)
                )

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFA000),
                            modifier = Modifier.size(sizeValue)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = String.format("%.1f", averageRating),
                            fontSize = titleFontSize,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.evaluations_label),
                        fontSize = (titleFontSize.value - 8).sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileDetailsSection(
    userViewModel: UserViewModel = viewModel()
) {
    val padding = logoSize() + 10.dp
    val spacer = spacer()
    val titleFontSize = txtSize()
    val user by userViewModel.user.collectAsState()
    val countryName by userViewModel.countryName.collectAsState()
    val formattedCreatedAt by userViewModel.formattedCreatedAt.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(padding)
            .offset(y = (-50).dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (user == null || countryName == null || formattedCreatedAt == "Not defined") {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(40.dp),
                    color = Color(0xFF4A6FA5)
                )
            }
        } else {
            ProfileDetailRow(
                label1 = stringResource(R.string.member_since_label),
                value1 = formattedCreatedAt,
                label2 = stringResource(R.string.school_label),
                value2 = user?.school ?: "Not defined"
            )

            Spacer(modifier = Modifier.height(spacer))

            ProfileDetailRow(
                label1 = stringResource(R.string.email),
                value1 = user?.email ?: "Not defined",
                label2 = stringResource(R.string.course_label),
                value2 = user?.course ?: "Not defined"
            )

            Spacer(modifier = Modifier.height(spacer))

            ProfileDetailRow(
                label1 = stringResource(R.string.country),
                value1 = countryName ?: "Not defined",
                label2 = stringResource(R.string.birthdate),
                value2 = user?.birth_date ?: "Not defined"
            )

           // Spacer(modifier = Modifier.height(spacer))

        /*    Row {
                Column(modifier = Modifier.weight(1f)
                    .offset(x = -(20).dp)) {
                    Text(
                        text = stringResource(R.string.birthdate),
                        fontSize = titleFontSize,
                        color = Color(0xFF4A6FA5),
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = user?.birth_date ?: "Not defined",
                        fontSize = (titleFontSize.value - 2).sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Normal
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
            }*/
        }
    }
}

@Composable
fun ProfileDetailRow(
    label1: String,
    value1: String,
    label2: String,
    value2: String
) {
    val titleFontSize = txtSize()

    Row {
        Column(modifier = Modifier.weight(1f)
            .offset(x = -(20).dp)) {
            Text(
                text = label1,
                fontSize = titleFontSize,
                color = Color(0xFF4A6FA5),
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value1,
                fontSize = (titleFontSize.value - 2).sp,
                color = Color.Black,
                fontWeight = FontWeight.Normal
            )
        }

        Column(modifier = Modifier.weight(1f)
            .offset(x = 20.dp)) {
            Text(
                text = label2,
                fontSize = titleFontSize,
                color = Color(0xFF4A6FA5),
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value2,
                fontSize = (titleFontSize.value - 2).sp,
                color = if (value2 == "Not defined") Color.Gray else Color.Black,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

@Composable
fun EditProfileBtn(onSettingsClick: () -> Unit){
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
                .offset(y = (-30).dp)
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
                text = stringResource(R.string.edit_profile_btn),
                fontSize = titleFontSize,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Indice5(navController: NavController? = null,
            userViewModel: UserViewModel = viewModel()){

    LaunchedEffect(Unit) {
        userViewModel.loadCurrentUser()
    }

    TopBar2(
        onSettingsClick = {
            navController?.navigate("settings")
        },
        onFavouritesClick = {
            navController?.navigate("favourites") {
                launchSingleTop = true
                restoreState = true
            }
        }
    )
    Identity(modifier = Modifier.offset(y = (-60).dp)
        .offset(x = 10.dp),
        userViewModel = userViewModel)
    StatsCardGroup(userViewModel = userViewModel)
    ProfileDetailsSection(userViewModel = userViewModel)
    EditProfileBtn(onSettingsClick = {
        navController?.navigate("editprofile")
    })
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProfileScreen(navController: NavController){
    var selectedItem by remember { mutableIntStateOf(4) }
    val userViewModel: UserViewModel = viewModel()

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
            }
            ScreenContent(selectedItem, navController)
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