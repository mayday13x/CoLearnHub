package com.example.colearnhub.ui.screen.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
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

@Composable
fun ScrollableOutlinedTextField(
    text: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = 200.dp,
    cornerRadius: Dp = 12.dp
) {
    val scrollState = rememberScrollState()

    val shape = RoundedCornerShape(cornerRadius)
    val borderColor = Color(0xFF5A6B7D)
    val backgroundColor = Color.White

    Box(
        modifier = modifier
            .height(height)
            .fillMaxWidth()
            .clip(shape)
            .background(backgroundColor)
            .drawBehind {
                val strokeWidth = 2.dp.toPx()
                drawRoundRect(
                    color = borderColor,
                    size = size,
                    cornerRadius = CornerRadius(cornerRadius.toPx()),
                    style = Stroke(strokeWidth)
                )
            }
            .padding(8.dp) // padding entre a borda e o conteúdo
    ) {
        BasicTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
            cursorBrush = SolidColor(Color.Black),
            decorationBox = { innerTextField ->
                if (text.isEmpty()) {
                    Text("Enter title here...", color = Color.Gray)
                }
                innerTextField()
            }
        )
    }
}

@Composable
fun Title() {
    var title by remember { mutableStateOf("") }

    val screenSize = getScreenSize()

    val spacer = when (screenSize) {
        ScreenSize.SMALL -> 16.dp
        ScreenSize.MEDIUM -> 20.dp
        ScreenSize.LARGE -> 24.dp
    }

    val textFieldHeight = when (screenSize) {
        ScreenSize.SMALL -> 60.dp
        ScreenSize.MEDIUM -> 160.dp
        ScreenSize.LARGE -> 200.dp
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(50.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(spacer)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Title",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF5A6B7D),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            ScrollableOutlinedTextField(
                text = title,
                onTextChange = { title = it },
                height = textFieldHeight
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewShareScreen() {
    Column {
        TopBar()
        Title()
    }
}
