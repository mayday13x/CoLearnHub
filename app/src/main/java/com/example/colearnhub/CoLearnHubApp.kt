package com.example.colearnhub.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.colearnhub.ui.navigation.NavGraph
import com.example.colearnhub.ui.theme.CoLearnHubTheme

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CoLearnHubApp() {
    CoLearnHubTheme {
        Surface(modifier = Modifier) {
            NavGraph()
        }
    }
}
