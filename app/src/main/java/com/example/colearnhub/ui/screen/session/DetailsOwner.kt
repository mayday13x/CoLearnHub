package com.example.colearnhub.ui.screen.session

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.colearnhub.R
import com.example.colearnhub.ui.utils.logoSize
import com.example.colearnhub.ui.utils.spacer3
import com.example.colearnhub.ui.utils.txtSize

@Composable
fun DetailsOwnerTopBar(onBack: () -> Unit){
    val txtSize = (txtSize().value + 4).sp
    val barSize = spacer3()
    val logoSize = logoSize() - 13.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(barSize)
            .background(Color(0xFF395174)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.session_details),
            color = Color.White,
            fontSize = txtSize,
            fontWeight = FontWeight.Medium
        )

        Row (
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(logoSize)
                )
            }
        }
    }
}

@Composable
fun DOSessionName(){
    val paddingValue = logoSize() + 10.dp
    val titleFontSize = (txtSize().value + 1).sp
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = paddingValue)
            .padding(top = paddingValue)
            .padding(bottom = 8.dp),
    ){
        Text(
            text = stringResource(R.string.session_name),
            fontSize = titleFontSize,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF395174),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Technical English - Pratical Lesson",
            fontSize = (titleFontSize.value - 2).sp,
            color = Color.Black,
            fontWeight = FontWeight.Normal
        )
    }
}

@Composable
fun DODescription(){
    val paddingValue = logoSize() + 10.dp
    val titleFontSize = (txtSize().value + 1).sp
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = paddingValue)
            .padding(top = paddingValue)
            .padding(bottom = 8.dp),
    ){
        Text(
            text = stringResource(R.string.description),
            fontSize = titleFontSize,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF395174),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Technical English oral presentation training",
            fontSize = (titleFontSize.value - 2).sp,
            color = Color.Black,
            fontWeight = FontWeight.Normal
        )
    }
}

@Composable
fun DOLink(link: String) {
    val paddingValue = logoSize() + 10.dp
    val titleFontSize = (txtSize().value + 1).sp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = paddingValue)
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = stringResource(R.string.session_link),
            fontSize = titleFontSize,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF395174),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = link,
            onValueChange = { },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Launch,
                    contentDescription = "Open Link",
                    tint = Color(0xFF395174)
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF395174),
                unfocusedBorderColor = Color(0xFF395174)
            ),
            shape = RoundedCornerShape(12.dp),
            textStyle = TextStyle(fontSize = (titleFontSize.value - 2).sp)
        )
    }
}

@Composable
fun DODate(day: String, month: String, year: String){
    val paddingValue = logoSize() + 10.dp
    val titleFontSize = (txtSize().value + 1).sp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = paddingValue)
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = stringResource(R.string.date),
            fontSize = titleFontSize,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF395174),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "$day/$month/$year",
            fontSize = (titleFontSize.value - 2).sp,
            color = Color.Black,
            fontWeight = FontWeight.Normal
        )
    }
}

@Composable
fun DOTimeAndDuration(hour: String, minute: String, duration: String) {
    val paddingValue = logoSize() + 10.dp
    val titleFontSize = (txtSize().value + 1).sp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = paddingValue)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Start Time
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.start_time),
                fontSize = titleFontSize,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF395174),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "$hour:$minute",
                fontSize = (titleFontSize.value - 2).sp,
                color = Color.Black,
                fontWeight = FontWeight.Normal
            )
        }

        // Duration
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.duration),
                fontSize = titleFontSize,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF395174),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = duration,
                fontSize = (titleFontSize.value - 2).sp,
                color = Color.Black,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

@Composable
fun DOTag() {
    val paddingValue = logoSize() + 10.dp
    val titleFontSize = (txtSize().value + 1).sp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = paddingValue)
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = stringResource(R.string.tag),
            fontSize = titleFontSize,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF395174),
            modifier = Modifier.padding(bottom = 8.dp)
        )
    }
}

@Composable
fun DOParticipants(){
    val paddingValue = logoSize() + 10.dp
    val titleFontSize = txtSize()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = paddingValue)
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Participants (7)",
                fontSize = titleFontSize,
                color = Color.Black
            )
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "View participants",
                tint = Color.Gray
            )
        }
    }
}

@Composable
fun DOActionButtons(onRemove: () -> Unit, onEdit: () -> Unit) {
    val paddingValue = logoSize() + 10.dp
    val titleFontSize = txtSize()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = paddingValue)
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Remove Button
        Button(
            onClick = onRemove,
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFE53935)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Remove",
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.remove),
                color = Color.White,
                fontSize = titleFontSize
            )
        }

        // Edit Button
        Button(
            onClick = onEdit,
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF395174)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit",
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.edit),
                color = Color.White,
                fontSize = titleFontSize
            )
        }
    }
}

@Composable
fun DetailsOwnerIndice(onBack: () -> Unit){
    val link by remember { mutableStateOf("https://zoom.us/j/1234567890") }
    val day by remember { mutableStateOf("15") }
    val month by remember { mutableStateOf("08") }
    val year by remember { mutableStateOf("2023") }
    val hour by remember { mutableStateOf("10") }
    val minute by remember { mutableStateOf("00") }
    val duration by remember { mutableStateOf("1h") }

    Column{
        DetailsOwnerTopBar(onBack = onBack)
        DOSessionName()
        DODescription()
        DOLink(link = link)
        DODate(day = day, month = month, year = year)
        DOTimeAndDuration(hour = hour, minute = minute, duration = duration)
        DOTag()
        DOParticipants()
        DOActionButtons(
            onRemove = { /* Handle remove */ },
            onEdit = { /* Handle edit */ }
        )
    }
}

@Composable
fun DetailsOwnerScreen(navController: NavHostController){
    val navigateBack = {
        navController.navigate("MainScreen?selectedItem=1") {
            popUpTo("session_details_owner") { inclusive = true }
            launchSingleTop = true
        }
    }

    BackHandler {
        navigateBack()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column {
            DetailsOwnerIndice(onBack = navigateBack)
        }
    }
}