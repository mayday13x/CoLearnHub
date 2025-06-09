package com.example.colearnhub.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@Composable
fun txtSize(): TextUnit {
    val screenSize = getScreenSize()
    val txtSize = when (screenSize) {
        ScreenSize.SMALL -> 16.sp
        ScreenSize.MEDIUM -> 18.sp
        ScreenSize.LARGE -> 20.sp
    }
    return txtSize
}

@Composable
fun titleFontSize(): TextUnit {
    val screenSize = getScreenSize()
    val txtSize = when (screenSize) {
        ScreenSize.SMALL -> 12.sp
        ScreenSize.MEDIUM -> 15.sp
        ScreenSize.LARGE -> 18.sp
    }
    return txtSize
}