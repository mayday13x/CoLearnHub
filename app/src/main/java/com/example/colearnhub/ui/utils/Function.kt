package com.example.colearnhub.ui.utils

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
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TextSnippet
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.colearnhub.R
import com.example.colearnhub.ui.screen.main.BottomNavItem
import com.example.colearnhub.ui.screen.main.Indice
import com.example.colearnhub.ui.screen.main.Indice2
import com.example.colearnhub.ui.screen.main.Indice3
import com.example.colearnhub.ui.screen.main.Indice4
import com.example.colearnhub.ui.screen.main.Indice5

@Composable
fun Circles() {
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
fun SearchBar() {
    val screenSize = getScreenSize()
    val padding = dynamicPadding()
    val logoSize = when (screenSize) {
        ScreenSize.SMALL -> 40.dp
        ScreenSize.MEDIUM -> 50.dp
        ScreenSize.LARGE -> 60.dp
    }
    val titleFontSize = when (screenSize) {
        ScreenSize.SMALL -> 10.sp
        ScreenSize.MEDIUM -> 14.sp
        ScreenSize.LARGE -> 18.sp
    }
    val verticalSpacing = when (screenSize) {
        ScreenSize.SMALL -> 27.dp
        ScreenSize.MEDIUM -> 35.dp
        ScreenSize.LARGE -> 47.dp
    }

    Column(
        modifier = Modifier
            .padding(padding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.cubewhite),
            contentDescription = "Logo",
            modifier = Modifier.size(logoSize)
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
                    fontSize = titleFontSize,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    style = androidx.compose.ui.text.TextStyle(
                        drawStyle = Stroke(width = 2f)
                    )
                )
            }
        }
        Spacer(Modifier.height(verticalSpacing))
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
                stringResource(R.string.Search),
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
                stringResource(R.string.Knowledge),
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun SBar(title: String) {
    val screenSize = getScreenSize()
    val padding = dynamicPadding()

    val titleFontSize = when (screenSize) {
        ScreenSize.SMALL -> 16.sp
        ScreenSize.MEDIUM -> 20.sp
        ScreenSize.LARGE -> 24.sp
    }

    val verticalSpacing = when (screenSize) {
        ScreenSize.SMALL -> 27.dp
        ScreenSize.MEDIUM -> 35.dp
        ScreenSize.LARGE -> 47.dp
    }

    Column(
        modifier = Modifier
            .padding(padding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Box {
                Text(
                    text = title, // <- Título dinâmico
                    fontSize = titleFontSize,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
            }
        }

        Spacer(Modifier.height(verticalSpacing + 33.dp))

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
                stringResource(R.string.Search),
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
                stringResource(R.string.Knowledge),
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun NavBarIcon(
    item: BottomNavItem,
    isSelected: Boolean,
    iconSize: Dp,
    isCenterButton: Boolean
) {
    if (isCenterButton) {
        // Ícone central (ex: Share)
        Box(
            modifier = Modifier
                .size(iconSize)
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(10.dp)
                )
                .border(
                    width = 1.5.dp,
                    color = if (isSelected) Color(0xFF395174) else Color.Gray,
                    shape = RoundedCornerShape(10.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = item.icon ?: Icons.Default.Add,
                contentDescription = item.label,
                tint = if (isSelected) Color(0xFF395174) else Color.Gray,
                modifier = Modifier.size(iconSize)
            )
        }
    } else {
        if (item.drawableRes != null) {
            Image(
                painter = painterResource(id = item.drawableRes),
                contentDescription = item.label,
                colorFilter = ColorFilter.tint(
                    if (isSelected) Color(0xFF395174) else Color.Gray
                ),
                modifier = Modifier.size(iconSize)
            )
        } else if (item.icon != null) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.label,
                tint = if (isSelected) Color(0xFF395174) else Color.Gray,
                modifier = Modifier.size(iconSize)
            )
        }
    }
}

@Composable
fun Nav(
    selectedItem: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val screenSize = getScreenSize()
    val bottomBarHeight = when (screenSize) {
        ScreenSize.SMALL -> 90.dp
        ScreenSize.MEDIUM -> 102.dp
        ScreenSize.LARGE -> 110.dp
    }
    val iconSize = when (screenSize) {
        ScreenSize.SMALL -> 24.dp
        ScreenSize.MEDIUM -> 32.dp
        ScreenSize.LARGE -> 40.dp
    }
    val textSize = when (screenSize) {
        ScreenSize.SMALL -> 12.sp
        ScreenSize.MEDIUM -> 18.sp
        ScreenSize.LARGE -> 24.sp
    }

    val label1 = stringResource(R.string.Home)
    val label2 = stringResource(R.string.Sessions)
    val label3 = stringResource(R.string.Share)
    val label4 = stringResource(R.string.Groups)
    val label5 = stringResource(R.string.Profile)

    val bottomNavItems = listOf(
        BottomNavItem(label1, drawableRes = R.drawable.cube),
        BottomNavItem(label2, icon = Icons.Default.Schedule),
        BottomNavItem(label3, icon = Icons.Default.Add),
        BottomNavItem(label4, icon = Icons.Default.Group),
        BottomNavItem(label5, icon = Icons.Default.Person)
    )

    NavigationBar(
        containerColor = Color.White,
        modifier = modifier
            .height(bottomBarHeight)
            .drawBehind {
                val strokeWidth = 1.5.dp.toPx()
                drawLine(
                    color = Color(0xFFA4A4A4),
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = strokeWidth
                )
            }
    ) {
        bottomNavItems.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedItem == index,
                onClick = { onItemSelected(index) },
                icon = {
                    NavBarIcon(
                        item = item,
                        isSelected = selectedItem == index,
                        iconSize = iconSize,
                        isCenterButton = index == 2,
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = textSize
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF395174),
                    unselectedIconColor = Color.Gray,
                    selectedTextColor = Color(0xFF395174),
                    unselectedTextColor = Color.Gray,
                    indicatorColor = Color(0x46395174)
                )
            )
        }
    }
}

@Composable
fun ScreenContent(selectedItem: Int, navController: NavController) {
    when (selectedItem) {
        0 -> Indice()
        1 -> Indice2()
        2 -> Indice4()
        3 -> Indice3()
        4 -> Indice5(navController)
    }
}

fun getFileExtension(fileName: String): String {
    return fileName.substringAfterLast('.', "pdf")
}

fun getFileIcon(fileName: String?) = when {
    fileName?.endsWith(".pdf", ignoreCase = true) == true -> Icons.Default.PictureAsPdf
    fileName?.endsWith(".doc", ignoreCase = true) == true ||
            fileName?.endsWith(".docx", ignoreCase = true) == true -> Icons.Default.Description
    fileName?.endsWith(".txt", ignoreCase = true) == true -> Icons.Default.TextSnippet
    fileName?.endsWith(".jpg", ignoreCase = true) == true ||
            fileName?.endsWith(".jpeg", ignoreCase = true) == true ||
            fileName?.endsWith(".png", ignoreCase = true) == true -> Icons.Default.Image
    else -> Icons.Default.Description
}
