@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.colearnhub.ui.screen.others

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.colearnhub.R
import com.example.colearnhub.ui.screen.main.ScrollableOutlinedTextField
import com.example.colearnhub.ui.utils.Circles
import com.example.colearnhub.ui.utils.logoSize
import com.example.colearnhub.ui.utils.sbutton
import com.example.colearnhub.ui.utils.spacer2
import com.example.colearnhub.ui.utils.spacer3
import com.example.colearnhub.ui.utils.textFieldHeight
import com.example.colearnhub.ui.utils.txtSize
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import com.example.colearnhub.modelLayer.User
import com.example.colearnhub.viewmodel.UserViewModel
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext

@Composable
fun UnsavedChangesDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    onDiscard: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = stringResource(R.string.unsaved_changes_title),
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF395174)
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.unsaved_changes_message),
                    color = Color.Black
                )
            },
            confirmButton = {
                TextButton(
                    onClick = onConfirm,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color(0xFF395174)
                    )
                ) {
                    Text(stringResource(R.string.save))
                }
            },
            dismissButton = {
                Row {
                    TextButton(
                        onClick = onDiscard,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color(0xFFFF0000)
                        )
                    ) {
                        Text(stringResource(R.string.discard))
                    }

                    TextButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color.Gray
                        )
                    ) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun TopEditProfileBar(
    onBack: () -> Unit,
    hasUnsavedChanges: Boolean,
    onShowDialog: () -> Unit // Nova função para mostrar o diálogo
) {
    val txtSize = (txtSize().value + 4).sp
    val barSize = spacer3()
    val logoSize = logoSize() - 13.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(barSize)
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.edit_profile_btn),
            color = Color.Black,
            fontSize = txtSize,
            fontWeight = FontWeight.Medium
        )

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    if (hasUnsavedChanges) {
                        onShowDialog()
                    } else {
                        onBack()
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black,
                    modifier = Modifier.size(logoSize)
                )
            }
        }
    }
}

@Composable
fun IdentityWithEdit(
    modifier: Modifier = Modifier,
    name: String,
    username: String
) {
    val titleFontSize = (txtSize().value + 2).sp
    val sizeValue = logoSize()
    val profileSize = sizeValue + 30.dp

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(profileSize),
            contentAlignment = Alignment.Center
        ) {
            // Perfil
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(CircleShape)
                    .border(3.dp, Color.White, CircleShape)
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    tint = Color.White,
                    modifier = Modifier.size(sizeValue)
                )
            }

            Box(
                modifier = Modifier
                    .size(30.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = 6.dp, y = (-40).dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .clickable { /* Handle edit photo */ },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = name,
            fontSize = titleFontSize,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Text(
            text = "@$username",
            fontSize = (titleFontSize.value - 4).sp,
            color = Color.White.copy(alpha = 0.8f)
        )
    }
}

@Composable
fun EditName(title: String, onTitleChange: (String) -> Unit) {
    val spacer = spacer2() - 8.dp
    val textFieldHeight = textFieldHeight()
    val paddingValue = sbutton()
    val paddingValue2 = logoSize() + 48.dp
    val titleFontSize = (txtSize().value + 1).sp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = paddingValue)
            .padding(top = paddingValue + paddingValue2),
        verticalArrangement = Arrangement.spacedBy(spacer)
    ) {
        Text(
            text = stringResource(R.string.name),
            fontSize = titleFontSize,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF395174),
        )

        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            textStyle = TextStyle(
                fontSize = titleFontSize,
                color = Color(0xFF000000)
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF395174),
                unfocusedBorderColor = Color(0xFF395174)
            )
        )
    }
}

@Composable
fun EditUsername(title: String, onTitleChange: (String) -> Unit) {
    val spacer = spacer2() - 8.dp
    val textFieldHeight = textFieldHeight()
    val paddingValue = sbutton()
    val paddingValue2 = 8.dp
    val titleFontSize = (txtSize().value + 1).sp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = paddingValue)
            .padding(top = paddingValue + paddingValue2),
        verticalArrangement = Arrangement.spacedBy(spacer)
    ) {
        Text(
            text = stringResource(R.string.username),
            fontSize = titleFontSize,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF395174),
        )

        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            readOnly = true,
            enabled = false,
            textStyle = TextStyle(
                fontSize = titleFontSize,
                color = Color.Gray
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF395174),
                unfocusedBorderColor = Color(0xFF395174),
                disabledBorderColor = Color(0xFF395174),
                disabledTextColor = Color.Gray,
                disabledContainerColor = Color(0xFFF5F5F5)
            )
        )
    }
}

@Composable
fun EditEmail(title: String, onTitleChange: (String) -> Unit) {
    val spacer = spacer2() - 8.dp
    val textFieldHeight = textFieldHeight()
    val paddingValue = sbutton()
    val titleFontSize = (txtSize().value + 1).sp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = paddingValue)
            .padding(top = paddingValue + 2.dp),
        verticalArrangement = Arrangement.spacedBy(spacer)
    ) {
        Text(
            text = stringResource(R.string.email),
            fontSize = titleFontSize,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF395174),
        )

        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            readOnly = true,
            enabled = false,
            textStyle = TextStyle(
                fontSize = titleFontSize,
                color = Color.Gray
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF395174),
                unfocusedBorderColor = Color(0xFF395174),
                disabledBorderColor = Color(0xFF395174),
                disabledTextColor = Color.Gray,
                disabledContainerColor = Color(0xFFF5F5F5)
            )
        )
    }
}

@Composable
fun EditCountry(
    selectedCountry: Int,
    onCountryChange: (Int) -> Unit
) {
    var isDropdownExpanded by remember { mutableStateOf(false) }
    val countries = listOf(
        "🇵🇹 ${stringResource(R.string.portugal)}" to 1,
        "🇺🇸 ${stringResource(R.string.unitedstates)}" to 2
    )
    val titleFontSize = (txtSize().value + 1).sp
    val paddingValue = sbutton() - 6.dp

    Column(modifier = Modifier
        .padding(horizontal = 12.dp)
        .padding(top = paddingValue)) {
        Text(
            text = stringResource(R.string.country),
            fontSize = titleFontSize,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF395174),
            modifier = Modifier
                .padding(bottom = 8.dp)
                .padding(top = 8.dp)
        )

        ExposedDropdownMenuBox(
            expanded = isDropdownExpanded,
            onExpandedChange = { isDropdownExpanded = !isDropdownExpanded }
        ) {
            OutlinedTextField(
                value = countries.find { it.second == selectedCountry }?.first ?: countries[0].first,
                onValueChange = {},
                readOnly = true,
                textStyle = TextStyle(
                    fontSize = titleFontSize,
                    color = Color(0xFF000000)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = isDropdownExpanded
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF395174),
                    unfocusedBorderColor = Color(0xFF395174)
                )
            )

            ExposedDropdownMenu(
                expanded = isDropdownExpanded,
                onDismissRequest = { isDropdownExpanded = false },
                modifier = Modifier.background(Color.White)
            ) {
                countries.forEach { (countryName, countryId) ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = countryName,
                                fontSize = titleFontSize,
                                color = Color(0xFF000000)
                            )
                        },
                        onClick = {
                            onCountryChange(countryId)
                            isDropdownExpanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun EditDate(
    selectedDay: String,
    selectedMonth: String,
    selectedYear: String,
    onDayChange: (String) -> Unit,
    onMonthChange: (String) -> Unit,
    onYearChange: (String) -> Unit
) {
    var expandedDay by remember { mutableStateOf(false) }
    var expandedMonth by remember { mutableStateOf(false) }
    var expandedYear by remember { mutableStateOf(false) }
    val days = (1..31).map { it.toString() }
    val months = listOf(
        stringResource(R.string.january),
        stringResource(R.string.february),
        stringResource(R.string.march),
        stringResource(R.string.april),
        stringResource(R.string.may),
        stringResource(R.string.june),
        stringResource(R.string.july),
        stringResource(R.string.august),
        stringResource(R.string.september),
        stringResource(R.string.october),
        stringResource(R.string.november),
        stringResource(R.string.december)
    )
    val years = (1950..2010).map { it.toString() }.reversed()
    val titleFontSize = (txtSize().value + 1).sp
    val paddingValue = sbutton() - 6.dp

    Column(modifier = Modifier
        .padding(horizontal = 12.dp)
        .padding(top = paddingValue)) {
        Text(
            text = stringResource(R.string.birthdate),
            fontSize = titleFontSize,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF395174),
            modifier = Modifier
                .padding(bottom = 8.dp)
                .padding(top = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Dia
            ExposedDropdownMenuBox(
                expanded = expandedDay,
                onExpandedChange = { expandedDay = !expandedDay },
                modifier = Modifier.weight(0.8f)
            ) {
                OutlinedTextField(
                    value = selectedDay,
                    onValueChange = {},
                    readOnly = true,
                    textStyle = TextStyle(
                        fontSize = titleFontSize,
                        color = Color(0xFF000000)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = expandedDay
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF395174),
                        unfocusedBorderColor = Color(0xFF395174)
                    )
                )

                ExposedDropdownMenu(
                    expanded = expandedDay,
                    onDismissRequest = { expandedDay = false },
                    modifier = Modifier.background(Color.White)
                ) {
                    days.forEach { day ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = day,
                                    fontSize = titleFontSize,
                                    color = Color(0xFF000000)
                                )
                            },
                            onClick = {
                                onDayChange(day)
                                expandedDay = false
                            }
                        )
                    }
                }
            }

            // Mês
            ExposedDropdownMenuBox(
                expanded = expandedMonth,
                onExpandedChange = { expandedMonth = !expandedMonth },
                modifier = Modifier.weight(1.2f)
            ) {
                OutlinedTextField(
                    value = selectedMonth,
                    onValueChange = {},
                    readOnly = true,
                    textStyle = TextStyle(
                        fontSize = titleFontSize,
                        color = Color(0xFF000000)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = expandedMonth
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF395174),
                        unfocusedBorderColor = Color(0xFF395174)
                    )
                )

                ExposedDropdownMenu(
                    expanded = expandedMonth,
                    onDismissRequest = { expandedMonth = false },
                    modifier = Modifier.background(Color.White)
                ) {
                    months.forEach { month ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = month,
                                    fontSize = titleFontSize,
                                    color = Color(0xFF000000)
                                )
                            },
                            onClick = {
                                onMonthChange(month)
                                expandedMonth = false
                            }
                        )
                    }
                }
            }

            // Ano
            ExposedDropdownMenuBox(
                expanded = expandedYear,
                onExpandedChange = { expandedYear = !expandedYear },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = selectedYear,
                    onValueChange = {},
                    readOnly = true,
                    textStyle = TextStyle(
                        fontSize = titleFontSize,
                        color = Color(0xFF000000)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = expandedYear
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF395174),
                        unfocusedBorderColor = Color(0xFF395174)
                    )
                )

                ExposedDropdownMenu(
                    expanded = expandedYear,
                    onDismissRequest = { expandedYear = false },
                    modifier = Modifier.background(Color.White)
                ) {
                    years.forEach { year ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = year,
                                    fontSize = titleFontSize,
                                    color = Color(0xFF000000)
                                )
                            },
                            onClick = {
                                onYearChange(year)
                                expandedYear = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EditSchool(title: String, onTitleChange: (String) -> Unit) {
    val spacer = spacer2() - 8.dp
    val textFieldHeight = textFieldHeight()
    val paddingValue = sbutton()
    val titleFontSize = (txtSize().value + 1).sp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = paddingValue)
            .padding(top = paddingValue + 2.dp),
        verticalArrangement = Arrangement.spacedBy(spacer)
    ) {
        Text(
            text = stringResource(R.string.school_label),
            fontSize = titleFontSize,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF395174),
        )

        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            textStyle = TextStyle(
                fontSize = titleFontSize,
                color = Color(0xFF000000)
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF395174),
                unfocusedBorderColor = Color(0xFF395174)
            )
        )
    }
}

@Composable
fun EditCourse(title: String, onTitleChange: (String) -> Unit) {
    val spacer = spacer2() - 8.dp
    val textFieldHeight = textFieldHeight()
    val paddingValue = sbutton()
    val titleFontSize = (txtSize().value + 1).sp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = paddingValue)
            .padding(top = paddingValue + 2.dp),
        verticalArrangement = Arrangement.spacedBy(spacer)
    ) {
        Text(
            text = stringResource(R.string.course_label),
            fontSize = titleFontSize,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF395174),
        )

        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            textStyle = TextStyle(
                fontSize = titleFontSize,
                color = Color(0xFF000000)
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF395174),
                unfocusedBorderColor = Color(0xFF395174)
            )
        )
    }
}

@Composable
fun SaveBtn(
    enabled: Boolean,
    onClick: () -> Unit
) {
    val paddingValue = sbutton()
    val titleFontSize = (txtSize().value + 1).sp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = paddingValue)
            .padding(top = paddingValue + 2.dp)
    ) {
        Button(
            onClick = onClick,
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF395174),
                disabledContainerColor = Color(0xFF395174).copy(alpha = 0.5f)
            )
        ) {
            Text(
                text = stringResource(R.string.save),
                fontSize = titleFontSize,
                color = Color.White
            )
        }
    }
}

@Composable
fun CleanBtn(
    enabled: Boolean,
    onClean: () -> Unit
) {
    val paddingValue = sbutton()
    val shareButtonSize = sbutton() - 8.dp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = paddingValue)
            .padding(top = 16.dp)
    ) {
        Button(
            onClick = onClean,
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF0000),
                disabledContainerColor = Color(0xFFB0B0B0),
                contentColor = Color.White,
                disabledContentColor = Color.White.copy(alpha = 0.6f)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = stringResource(R.string.clean),
                fontSize = 16.sp,
                modifier = Modifier.padding(vertical = shareButtonSize)
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditProfileScreen(
    navController: NavHostController,
    userViewModel: UserViewModel = viewModel()
) {
    val user by userViewModel.user.collectAsState()
    var showSuccessDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        userViewModel.loadCurrentUserEditProfile()
    }

    val navigateBack = {
        navController.navigate("MainScreen?selectedItem=4") {
            popUpTo("MainScreen") { inclusive = true }
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
    ){
        Column (
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 80.dp)
        ){
            Column {
                Circles()
            }
            if (user == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(40.dp),
                        color = Color(0xFF395174)
                    )
                }
            } else {
                IndiceEditProfile(
                    onBack = navigateBack,
                    user = user!!,
                    onSave = { updatedUser ->
                        userViewModel.updateUser(updatedUser)
                        showSuccessDialog = true
                    }
                )
            }
        }
    }

}

@Composable
fun IndiceEditProfile(
    onBack: () -> Unit,
    user: User,
    onSave: (User) -> Unit
) {
    // Função auxiliar para converter número do mês para nome
    fun getMonthName(monthNumber: String): String {
        return when(monthNumber) {
            "01" -> "Janeiro"
            "02" -> "Fevereiro"
            "03" -> "Março"
            "04" -> "Abril"
            "05" -> "Maio"
            "06" -> "Junho"
            "07" -> "Julho"
            "08" -> "Agosto"
            "09" -> "Setembro"
            "10" -> "Outubro"
            "11" -> "Novembro"
            "12" -> "Dezembro"
            else -> ""
        }
    }

    // Valores originais (estado inicial)
    var originalName by remember { mutableStateOf(user.name) }
    var originalUsername by remember { mutableStateOf(user.username) }
    var originalEmail by remember { mutableStateOf(user.email ?: "") }
    var originalSchool by remember { mutableStateOf(user.school ?: "") }
    var originalCourse by remember { mutableStateOf(user.course ?: "") }
    var originalCountry by remember { mutableStateOf(user.country) }
    var originalDay by remember { mutableStateOf(user.birth_date.split("-").getOrNull(2) ?: "") }
    var originalMonth by remember { mutableStateOf(getMonthName(user.birth_date.split("-").getOrNull(1) ?: "")) }
    var originalBirthYear by remember { mutableStateOf(user.birth_date.split("-").getOrNull(0) ?: "") }

    // Estados atuais
    var name by remember { mutableStateOf(originalName) }
    var username by remember { mutableStateOf(originalUsername) }
    var email by remember { mutableStateOf(originalEmail) }
    var school by remember { mutableStateOf(originalSchool) }
    var course by remember { mutableStateOf(originalCourse) }
    var country by remember { mutableStateOf(originalCountry) }
    var selectedDay by remember { mutableStateOf(originalDay) }
    var selectedMonth by remember { mutableStateOf(originalMonth) }
    var selectedBirthYear by remember { mutableStateOf(originalBirthYear) }

    // Verificar se houve alterações usando derivedStateOf para otimização
    val hasChanges by remember {
        derivedStateOf {
            name != originalName ||
                    username != originalUsername ||
                    email != originalEmail ||
                    school != originalSchool ||
                    course != originalCourse ||
                    country != originalCountry ||
                    selectedDay != originalDay ||
                    selectedMonth != originalMonth ||
                    selectedBirthYear != originalBirthYear
        }
    }

    // Função para limpar os campos (voltar aos valores originais)
    fun resetFields() {
        name = originalName
        username = originalUsername
        email = originalEmail
        school = originalSchool
        course = originalCourse
        country = originalCountry
        selectedDay = originalDay
        selectedMonth = originalMonth
        selectedBirthYear = originalBirthYear
    }

    var showUnsavedDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Interceptar o botão de voltar do sistema
    BackHandler(enabled = hasChanges) {
        showUnsavedDialog = true
    }

    // Diálogo para alterações não guardadas
    UnsavedChangesDialog(
        showDialog = showUnsavedDialog,
        onDismiss = { showUnsavedDialog = false },
        onConfirm = {
            // Aqui você implementaria a lógica para salvar
            showUnsavedDialog = false
            onBack()
        },
        onDiscard = {
            showUnsavedDialog = false
            onBack()
        }
    )

    Column{
        TopEditProfileBar(onBack = onBack, hasUnsavedChanges = hasChanges, onShowDialog = { showUnsavedDialog = true })
        IdentityWithEdit(
            modifier = Modifier.offset(y = 30.dp),
            name = name,
            username = username
        )
        EditName(name) { name = it }
        EditUsername(username) { username = it }
        EditEmail(email) { email = it }
        EditCountry(
            selectedCountry = country,
            onCountryChange = { country = it }
        )
        EditDate(
            selectedDay = selectedDay,
            selectedMonth = selectedMonth,
            selectedYear = selectedBirthYear,
            onDayChange = { selectedDay = it },
            onMonthChange = { selectedMonth = it },
            onYearChange = { selectedBirthYear = it }
        )
        EditSchool(school) { school = it }
        EditCourse(course) { course = it }
        SaveBtn(
            enabled = hasChanges,
            onClick = {
                // Criar a data de nascimento no formato correto
                val birthDate = "$selectedBirthYear-${getMonthNumber(selectedMonth)}-$selectedDay"
                
                // Criar o objeto User atualizado
                val updatedUser = user.copy(
                    name = name,
                    school = school,
                    course = course,
                    country = country,
                    birth_date = birthDate
                )
                
                // Chamar a função de salvamento
                onSave(updatedUser)
                
                // Atualizar os valores originais com os novos valores
                originalName = name
                originalUsername = username
                originalEmail = email
                originalSchool = school
                originalCourse = course
                originalCountry = country
                originalDay = selectedDay
                originalMonth = selectedMonth
                originalBirthYear = selectedBirthYear
                
                // Mostrar Toast de sucesso
                Toast.makeText(
                    context,
                    context.getString(R.string.profile_updated_successfully),
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
        CleanBtn(
            enabled = hasChanges,
            onClean = { resetFields() }
        )
    }
}

private fun getMonthNumber(month: String): String {
    return when(month) {
        "Janeiro" -> "01"
        "Fevereiro" -> "02"
        "Março" -> "03"
        "Abril" -> "04"
        "Maio" -> "05"
        "Junho" -> "06"
        "Julho" -> "07"
        "Agosto" -> "08"
        "Setembro" -> "09"
        "Outubro" -> "10"
        "Novembro" -> "11"
        "Dezembro" -> "12"
        else -> "01"
    }
}