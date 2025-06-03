package com.example.colearnhub.ui.screen.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.colearnhub.ui.utils.ScreenSize
import com.example.colearnhub.ui.utils.getScreenSize

@Composable
fun TopBar() {
    val screenSize = getScreenSize()
    val txtSize = when (screenSize) {
        ScreenSize.SMALL -> 20.sp
        ScreenSize.MEDIUM -> 22.sp
        ScreenSize.LARGE -> 24.sp
    }
    val paddingValue = when (screenSize) {
        ScreenSize.SMALL -> 16.dp
        ScreenSize.MEDIUM -> 20.dp
        ScreenSize.LARGE -> 24.dp
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFFFF))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF395174))
                .padding(paddingValue),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Share",
                color = Color.White,
                fontSize = txtSize,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun Title(){
    var title by remember { mutableStateOf("") }

    val screenSize = getScreenSize()
    val spacer = when (screenSize) {
        ScreenSize.SMALL -> 16.dp
        ScreenSize.MEDIUM -> 20.dp
        ScreenSize.LARGE -> 24.dp
    }

    Spacer(modifier = Modifier.height(spacer))
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column {
            Text(
                text = "Title",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF5A6B7D),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF5A6B7D),
                    unfocusedBorderColor = Color(0xFFCCCCCC)
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTopBar() {
    TopBar()
    Title()
}