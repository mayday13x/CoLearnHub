package com.example.colearnhub.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun verticalSpacing(): Dp {
    val screenSize = getScreenSize()
    val verticalSpacing = when (screenSize) {
        ScreenSize.SMALL -> 40.dp
        ScreenSize.MEDIUM -> 48.dp
        ScreenSize.LARGE -> 60.dp
    }
    return verticalSpacing
}

@Composable
fun btnHeight(): Dp {
    val screenSize = getScreenSize()
    val btnHeight = when (screenSize) {
        ScreenSize.SMALL -> 35.dp
        ScreenSize.MEDIUM -> 40.dp
        ScreenSize.LARGE -> 45.dp
    }
    return btnHeight
}

@Composable
fun animation(): Dp {
    val screenSize = getScreenSize()
    val animationSize = when (screenSize) {
        ScreenSize.SMALL -> 120.dp
        ScreenSize.MEDIUM -> 160.dp
        ScreenSize.LARGE -> 200.dp
    }
    return animationSize

}

@Composable
fun logoSize(): Dp {
    val screenSize = getScreenSize()
    val logoSize = when (screenSize) {
        ScreenSize.SMALL -> 40.dp
        ScreenSize.MEDIUM -> 50.dp
        ScreenSize.LARGE -> 60.dp
    }
    return logoSize
}

@Composable
fun spacer(): Dp {
    val screenSize = getScreenSize()
    val spacer = when (screenSize) {
        ScreenSize.SMALL -> 16.dp
        ScreenSize.MEDIUM -> 24.dp
        ScreenSize.LARGE -> 30.dp
    }
    return spacer
}

@Composable
fun spacer2(): Dp {
    val screenSize = getScreenSize()
    val spacer = when (screenSize) {
        ScreenSize.SMALL -> 16.dp
        ScreenSize.MEDIUM -> 20.dp
        ScreenSize.LARGE -> 24.dp
    }
    return spacer
}

@Composable
fun textFieldHeight(): Dp {
    val screenSize = getScreenSize()
    val textFieldHeight = when (screenSize) {
        ScreenSize.SMALL -> 40.dp
        ScreenSize.MEDIUM -> 60.dp
        ScreenSize.LARGE -> 80.dp
    }
    return textFieldHeight
}

@Composable
fun sbutton(): Dp {
    val screenSize = getScreenSize()
    val spacer = when (screenSize) {
        ScreenSize.SMALL -> 14.dp
        ScreenSize.MEDIUM -> 16.dp
        ScreenSize.LARGE -> 18.dp
    }
    return spacer
}