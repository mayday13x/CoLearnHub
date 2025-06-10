package com.example.colearnhub.ui.screen.main

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

@Composable
fun InvitesLink(onClick: () -> Unit) {
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
            textDecoration = TextDecoration.Underline, // opcional
            modifier = Modifier
                .clickable(onClick = onClick)
        )
    }
}

@Composable
fun ContentArea3() {
    val padding = dynamicPadding()
    val animationSize = animation()
    val titleFontSize = txtSize()
    val verticalSpacing = verticalSpacing()
    val btnHeight = verticalSpacing() + 10.dp

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
                    text = stringResource(R.string.new_group),
                    fontSize = titleFontSize,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(verticalSpacing))

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

            Spacer(modifier = Modifier.height(verticalSpacing))
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun Indice3(){
    InvitesLink(onClick = { /* Acción al hacer clic en "Invites" */ })
    ContentArea3()
}

@Composable
fun GroupsScreen(navController: NavController){
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