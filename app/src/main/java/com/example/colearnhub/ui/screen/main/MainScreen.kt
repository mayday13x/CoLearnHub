package com.example.colearnhub.ui.screen.main

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
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
import com.example.colearnhub.ui.utils.Circles
import com.example.colearnhub.ui.utils.Nav
import com.example.colearnhub.ui.utils.ScreenContent
import com.example.colearnhub.ui.utils.ScreenSize
import com.example.colearnhub.ui.utils.SearchBar
import com.example.colearnhub.ui.utils.dynamicPadding
import com.example.colearnhub.ui.utils.dynamicWidth
import com.example.colearnhub.ui.utils.getScreenSize

@Composable
fun Indice(){
    var selectedTab by remember { mutableIntStateOf(0) }
    val label1 = stringResource(id = R.string.All)
    val label2 = stringResource(id = R.string.Created)
    val tabs = listOf(label1, label2)

    val screenSize = getScreenSize()
    dynamicPadding()
    val verticalSpacing = when (screenSize) {
        ScreenSize.SMALL -> 40.dp
        ScreenSize.MEDIUM -> 48.dp
        ScreenSize.LARGE -> 60.dp
    }
    val btnHeight = when (screenSize) {
        ScreenSize.SMALL -> 35.dp
        ScreenSize.MEDIUM -> 40.dp
        ScreenSize.LARGE -> 45.dp
    }
    val txtSize = when (screenSize) {
        ScreenSize.SMALL -> 16.sp
        ScreenSize.MEDIUM -> 18.sp
        ScreenSize.LARGE -> 20.sp
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Spacer(modifier = Modifier.height(verticalSpacing))
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
        ContentArea()
    }
}

@Composable
fun ContentArea() {
    val screenSize = getScreenSize()
    val padding = dynamicPadding()
    val animationSize = when (screenSize) {
        ScreenSize.SMALL -> 120.dp
        ScreenSize.MEDIUM -> 160.dp
        ScreenSize.LARGE -> 200.dp
    }
    val titleFontSize = when (screenSize) {
        ScreenSize.SMALL -> 16.sp
        ScreenSize.MEDIUM -> 18.sp
        ScreenSize.LARGE -> 20.sp
    }
    val verticalSpacing = when (screenSize) {
        ScreenSize.SMALL -> 40.dp
        ScreenSize.MEDIUM -> 48.dp
        ScreenSize.LARGE -> 60.dp
    }
    val btnHeight = when (screenSize) {
        ScreenSize.SMALL -> 50.dp
        ScreenSize.MEDIUM -> 58.dp
        ScreenSize.LARGE -> 70.dp
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = padding)
    ) {
        Spacer(modifier = Modifier.height(verticalSpacing))

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
                text = stringResource(R.string.not_found),
                fontSize = titleFontSize,
                color = Color.Black,
            )

            Text(
                text = stringResource(R.string.be_the_first),
                fontSize = (titleFontSize.value - 2).sp,
                color = Color.Black,
                modifier = Modifier.padding(top = 4.dp),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(verticalSpacing))

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
                    text = stringResource(R.string.Share),
                    fontSize = titleFontSize,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

data class BottomNavItem(
    val label: String,
    val icon: ImageVector? = null,
    val drawableRes: Int? = null
)

@Composable
fun MainScreen() {
    var selectedItem by remember { mutableIntStateOf(0) }

    Box(
        modifier = Modifier.fillMaxSize()
            .background(Color.White)
    ) {
        if(selectedItem == 0 || selectedItem == 1 || selectedItem == 4) {
            Circles()
        }

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            if(selectedItem == 0 || selectedItem == 1) {
                SearchBar()
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
fun PreviewMainScreen() {
    MainScreen()
}