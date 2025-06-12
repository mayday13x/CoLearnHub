package com.example.colearnhub.ui.screen.session

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.colearnhub.R
import com.example.colearnhub.ui.screen.main.Private
import com.example.colearnhub.ui.screen.main.ScrollableOutlinedTextField
import com.example.colearnhub.ui.screen.main.ShareBtn
import com.example.colearnhub.ui.utils.logoSize
import com.example.colearnhub.ui.utils.sbutton
import com.example.colearnhub.ui.utils.spacer2
import com.example.colearnhub.ui.utils.spacer3
import com.example.colearnhub.ui.utils.textFieldHeight
import com.example.colearnhub.ui.utils.txtSize
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NSSTopBar(onBack: () -> Unit) {
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
            text = stringResource(R.string.new_session),
            color = Color.White,
            fontSize = txtSize,
            fontWeight = FontWeight.Medium
        )

        Row(
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
fun NSSName(title: String, onTitleChange: (String) -> Unit) {
    val textFieldHeight = textFieldHeight()
    val paddingValue = logoSize() + 10.dp
    val titleFontSize = (txtSize().value + 1).sp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = paddingValue)
            .padding(top = paddingValue)
            .padding(bottom = 8.dp),
    ) {
        Text(
            text = stringResource(R.string.session_name),
            fontSize = titleFontSize,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF395174),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        ScrollableOutlinedTextField(
            text = title,
            onTextChange = onTitleChange,
            height = textFieldHeight
        )
    }
}

@Composable
fun NSSDescription(title: String, onTitleChange: (String) -> Unit) {
    val spacer = spacer2()
    val textFieldHeight = textFieldHeight() + 60.dp
    val paddingValue = logoSize() + 10.dp
    val paddingValue2 = logoSize() - 10.dp
    val titleFontSize = (txtSize().value + 1).sp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = paddingValue)
            .padding(top = paddingValue2)
            .padding(bottom = 8.dp),
        verticalArrangement = Arrangement.spacedBy(spacer)
    ) {
        Column {
            Text(
                text = stringResource(R.string.description),
                fontSize = titleFontSize,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF395174),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            ScrollableOutlinedTextField(
                text = title,
                onTextChange = onTitleChange,
                height = textFieldHeight
            )
        }
    }
}

@Composable
fun NSSLink(title: String, onTitleChange: (String) -> Unit) {
    val textFieldHeight = textFieldHeight()
    val paddingValue = logoSize() + 10.dp
    val titleFontSize = (txtSize().value + 1).sp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = paddingValue)
            .padding(top = paddingValue - 20.dp),
    ) {
        Text(
            text = stringResource(R.string.session_link),
            fontSize = titleFontSize,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF395174),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        ScrollableOutlinedTextField(
            text = title,
            onTextChange = onTitleChange,
            height = textFieldHeight
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NSSDate(selectedDate: String, onDateChange: (String) -> Unit) {
    val textFieldHeight = textFieldHeight()
    val paddingValue = logoSize() + 10.dp
    val titleFontSize = (txtSize().value + 1).sp

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = paddingValue)
            .padding(top = paddingValue - 20.dp),
    ) {
        Text(
            text = stringResource(R.string.date),
            fontSize = titleFontSize,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF395174),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = selectedDate,
            onValueChange = { },
            modifier = Modifier
                .fillMaxWidth()
                .height(paddingValue)
                .clickable { showDatePicker = true },
            readOnly = true,
            enabled = false,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Select Date",
                    tint = Color(0xFF395174)
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = Color.Black,
                disabledBorderColor = Color(0xFF395174),
                disabledTrailingIconColor = Color(0xFF395174)
            ),
            shape = RoundedCornerShape(8.dp)
        )
    }

    // Date Picker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            val formattedDate = formatter.format(Date(millis))
                            onDateChange(formattedDate)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK", color = Color(0xFF395174))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel", color = Color(0xFF395174))
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    selectedDayContainerColor = Color(0xFF395174),
                    todayContentColor = Color(0xFF395174),
                    todayDateBorderColor = Color(0xFF395174)
                )
            )
        }
    }
}

@Composable
fun NSSTimeAndDuration(
    startHour: String,
    startMinute: String,
    duration: String,
    onHourChange: (String) -> Unit,
    onMinuteChange: (String) -> Unit,
    onDurationChange: (String) -> Unit
) {
    val textFieldHeight = textFieldHeight()
    val paddingValue = logoSize() + 10.dp
    val titleFontSize = (txtSize().value + 1).sp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = paddingValue)
            .padding(top = paddingValue - 20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Start Time Section
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.start_time),
                    fontSize = titleFontSize,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF395174),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Hour Field
                    OutlinedTextField(
                        value = startHour,
                        onValueChange = { value ->
                            // Validar que seja um número entre 0-23
                            if (value.isEmpty() || (value.toIntOrNull() != null && value.toInt() in 0..23)) {
                                onHourChange(value)
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(paddingValue + 5.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF395174),
                            unfocusedBorderColor = Color(0xFF395174)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        textStyle = LocalTextStyle.current.copy(Color.Black),
                        placeholder = { Text("HH", textAlign = TextAlign.Center) }
                    )

                    // Separator
                    Text(
                        text = ":",
                        fontSize = titleFontSize,
                        color = Color(0xFF395174),
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )

                    // Minute Field
                    OutlinedTextField(
                        value = startMinute,
                        onValueChange = { value ->
                            // Validar que seja um número entre 0-59
                            if (value.isEmpty() || (value.toIntOrNull() != null && value.toInt() in 0..59)) {
                                onMinuteChange(value)
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(paddingValue + 5.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF395174),
                            unfocusedBorderColor = Color(0xFF395174)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        textStyle = LocalTextStyle.current.copy(Color.Black),
                        placeholder = { Text("MM", textAlign = TextAlign.Center) }
                    )
                }
            }

            Column(modifier = Modifier.weight(0.8f)) {
                Text(
                    text = stringResource(R.string.duration),
                    fontSize = titleFontSize,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF395174),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = duration,
                    onValueChange = { value ->
                        // Validar que seja um número positivo
                        if (value.isEmpty() || (value.toIntOrNull() != null && value.toInt() >= 0)) {
                            onDurationChange(value)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(paddingValue),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF395174),
                        unfocusedBorderColor = Color(0xFF395174)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    textStyle = LocalTextStyle.current.copy(Color.Black),
                    placeholder = { Text("90", textAlign = TextAlign.Center) },
                    suffix = { Text("min", color = Color(0xFF395174)) }
                )
            }
        }
    }
}

@Composable
fun Tags() {
    var showDialog by remember { mutableStateOf(false) }
    val paddingValue = logoSize() + 10.dp
    val paddingValue2 = logoSize() - 20.dp
    val titleFontSize = (txtSize().value + 1).sp

    Column {
        Text(
            text = stringResource(R.string.tag),
            fontSize = titleFontSize,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF395174),
            modifier = Modifier
                .padding(bottom = 8.dp)
                .padding(horizontal = paddingValue)
                .padding(top = paddingValue2)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = paddingValue),
            horizontalArrangement = Arrangement.Start
        ) {
            Button(
                onClick = { showDialog = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFFFFF)
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier
                    .border(1.dp, Color(0xFF395174), RoundedCornerShape(8.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add area",
                    tint = Color(0xFF395174),
                    modifier = Modifier.size(24.dp)
                )
            }

            /* Mostrar tags selecionadas
            Row(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                selectedTags.forEach { tag ->
                    Box(
                        modifier = Modifier
                            .background(
                                color = Color(0xFF395174),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = tag,
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }
                }
            }*/
        }
    }
}

@Composable
fun CreateBtn(
    enabled: Boolean,
    isCreating: Boolean,
    //onClick: () -> Unit
) {
    val paddingValue = logoSize() + 10.dp
    val paddingValue2 = logoSize() - 20.dp
    val shareButtonSize = sbutton() - 8.dp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = paddingValue)
            .padding(vertical = paddingValue2)
    ) {
        Button(
            onClick = {  },
            enabled = enabled, // && !isCreating,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = paddingValue2),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF395174),
                disabledContainerColor = Color(0xFFB0B0B0),
                contentColor = Color.White,
                disabledContentColor = Color.White.copy(alpha = 0.6f)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            if (isCreating) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Criando...",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(vertical = shareButtonSize)
                )
            } else {
                Text(
                    text = stringResource(R.string.create),
                    fontSize = 16.sp,
                    modifier = Modifier.padding(vertical = shareButtonSize)
                )
            }
        }
    }
}

@Composable
fun NSSIndice(onBack: () -> Unit){
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var link by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf("") }
    var startHour by remember { mutableStateOf("") }
    var startMinute by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var isPrivate by remember { mutableStateOf(false) }
    var isCreatingMaterial by remember { mutableStateOf(false) }

    val isCreateEnabled = name.isNotBlank() && link.isNotBlank() && selectedDate.isNotBlank() && startHour.isNotBlank() && startMinute.isNotBlank() && duration.isNotBlank()

    Column{
        NSSTopBar(onBack = onBack)
        NSSName(title = name) { name = it }
        NSSDescription(title = description) { description = it }
        NSSLink(title = link) { link = it }
        NSSDate(selectedDate = selectedDate) { selectedDate = it }
        NSSTimeAndDuration(
            startHour = startHour,
            startMinute = startMinute,
            duration = duration,
            onHourChange = { startHour = it },
            onMinuteChange = { startMinute = it },
            onDurationChange = { duration = it }
        )
        Tags()
        Private(
            isPrivate = isPrivate,
            onPrivateChange = { isPrivate = it }
        )
        CreateBtn(
            enabled = isCreateEnabled,
            isCreating = isCreatingMaterial
        )
    }
}

@Composable
fun NSSScreen(navController: NavHostController){
    val navigateBack = {
        navController.navigate("MainScreen?selectedItem=1") {
            popUpTo("group_details") { inclusive = true }
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
        Column (modifier = Modifier.verticalScroll(rememberScrollState())){
            NSSIndice(onBack = navigateBack)
        }
    }
}