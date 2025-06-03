package com.example.colearnhub.ui.screen.main

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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.colearnhub.R
import com.example.colearnhub.ui.utils.Circles
import com.example.colearnhub.ui.utils.Nav
import com.example.colearnhub.ui.utils.ScreenContent
import com.example.colearnhub.ui.utils.ScreenSize
import com.example.colearnhub.ui.utils.dynamicWidth
import com.example.colearnhub.ui.utils.getScreenSize

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar2() {
    val screenSize = getScreenSize()
    val logoSize = when (screenSize) {
        ScreenSize.SMALL -> 40.dp
        ScreenSize.MEDIUM -> 50.dp
        ScreenSize.LARGE -> 60.dp
    }
    val titleFontSize = when (screenSize) {
        ScreenSize.SMALL -> 12.sp
        ScreenSize.MEDIUM -> 15.sp
        ScreenSize.LARGE -> 18.sp
    }
    val paddingValue = when (screenSize) {
        ScreenSize.SMALL -> 30.dp
        ScreenSize.MEDIUM -> 40.dp
        ScreenSize.LARGE -> 50.dp
    }
    val top = when (screenSize) {
        ScreenSize.SMALL -> 90.dp
        ScreenSize.MEDIUM -> 100.dp
        ScreenSize.LARGE -> 110.dp
    }

    TopAppBar(
        modifier = Modifier.height(top),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = paddingValue - 15.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
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

                IconButton(onClick = {}) {
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

@Composable
fun Identity(modifier: Modifier = Modifier) {
    val screenSize = getScreenSize()
    val titleFontSize = when (screenSize) {
        ScreenSize.SMALL -> 18.sp
        ScreenSize.MEDIUM -> 20.sp
        ScreenSize.LARGE -> 22.sp
    }
    val sizeValue = when (screenSize) {
        ScreenSize.SMALL -> 40.dp
        ScreenSize.MEDIUM -> 50.dp
        ScreenSize.LARGE -> 60.dp
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
    ) {
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
            text = "Miguel Silva",
            fontSize = titleFontSize,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Text(
            text = "@michelangelo",
            fontSize = (titleFontSize.value - 4).sp,
            color = Color.White.copy(alpha = 0.8f)
        )
    }
}

@Composable
fun StatsCardGroup() {
    val screenSize = getScreenSize()
    val sizeValue = when (screenSize) {
        ScreenSize.SMALL -> 20.dp
        ScreenSize.MEDIUM -> 30.dp
        ScreenSize.LARGE -> 40.dp
    }
    val padding = when (screenSize) {
        ScreenSize.SMALL -> 50.dp
        ScreenSize.MEDIUM -> 60.dp
        ScreenSize.LARGE -> 70.dp
    }
    val titleFontSize = when (screenSize) {
        ScreenSize.SMALL -> 20.sp
        ScreenSize.MEDIUM -> 22.sp
        ScreenSize.LARGE -> 24.sp
    }
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
                        text = "5",
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
                        text = "4.0",
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

@Composable
fun ProfileDetailsSection() {
    val screenSize = getScreenSize()
    val padding = when (screenSize) {
        ScreenSize.SMALL -> 50.dp
        ScreenSize.MEDIUM -> 60.dp
        ScreenSize.LARGE -> 70.dp
    }
    val spacer = when (screenSize) {
        ScreenSize.SMALL -> 16.dp
        ScreenSize.MEDIUM -> 24.dp
        ScreenSize.LARGE -> 30.dp
    }
    val titleFontSize = when (screenSize) {
        ScreenSize.SMALL -> 16.sp
        ScreenSize.MEDIUM -> 18.sp
        ScreenSize.LARGE -> 20.sp
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(padding)
            .offset(y = (-40).dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProfileDetailRow(
            label1 = stringResource(R.string.member_since_label),
            value1 = "25/04/2025",
            label2 = stringResource(R.string.school_label),
            value2 = "Not defined"
        )

        Spacer(modifier = Modifier.height(spacer))

        ProfileDetailRow(
            label1 = stringResource(R.string.email),
            value1 = "mjosea@ipvc.pt",
            label2 = stringResource(R.string.course_label),
            value2 = "Not defined"
        )

        Spacer(modifier = Modifier.height(spacer))

        ProfileDetailRow(
            label1 = stringResource(R.string.country),
            value1 = "Portugal",
            label2 = stringResource(R.string.curricular_year_label),
            value2 = "Not defined"
        )

        Spacer(modifier = Modifier.height(spacer))

        Row {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.birthdate),
                    fontSize = titleFontSize,
                    color = Color(0xFF4A6FA5),
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "22/06/1996",
                    fontSize = (titleFontSize.value - 2).sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Normal
                )
            }
            Spacer(modifier = Modifier.weight(1f))
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
    val screenSize = getScreenSize()
    val titleFontSize = when (screenSize) {
        ScreenSize.SMALL -> 16.sp
        ScreenSize.MEDIUM -> 18.sp
        ScreenSize.LARGE -> 20.sp
    }

    Row {
        Column(modifier = Modifier.weight(1f)) {
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

        Column(modifier = Modifier.weight(1f)) {
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
fun EditProfileBtn(){
    val screenSize = getScreenSize()
    val btnHeight = when (screenSize) {
        ScreenSize.SMALL -> 50.dp
        ScreenSize.MEDIUM -> 58.dp
        ScreenSize.LARGE -> 70.dp
    }
    val titleFontSize = when (screenSize) {
        ScreenSize.SMALL -> 16.sp
        ScreenSize.MEDIUM -> 18.sp
        ScreenSize.LARGE -> 20.sp
    }
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = { },
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
                text = stringResource(R.string.edit_profile_btn),
                fontSize = titleFontSize,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun Indice5(){
    TopBar2()
    Identity(modifier = Modifier.offset(y = (-60).dp)
        .offset(x = 10.dp))
    StatsCardGroup()
    ProfileDetailsSection()
    EditProfileBtn()
}

@Composable
fun ProfileScreen(){
    var selectedItem by remember { mutableIntStateOf(4) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if(selectedItem == 0 || selectedItem == 1 || selectedItem == 4) {
            Circles()
        }

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            if(selectedItem == 0 || selectedItem == 1 || selectedItem == 4) {
                Indice5()
            }
            ScreenContent(selectedItem)
        }

        if(selectedItem == 0 || selectedItem == 1 || selectedItem == 4) {
            Nav(
                selectedItem = selectedItem,
                onItemSelected = { newIndex ->
                    selectedItem = newIndex
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewProfileScreen() {
    ProfileScreen()
}