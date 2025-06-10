@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.colearnhub.ui.screen.others

import androidx.activity.compose.BackHandler
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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
fun IdentityWithEdit(modifier: Modifier = Modifier) {
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
            text = "Miguel Silva",
            fontSize = titleFontSize,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Text(
            text = "@michelangelo",
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
    val paddingValue2 = logoSize() + 50.dp
    val titleFontSize = (txtSize().value + 1).sp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = paddingValue)
            .offset(y = paddingValue2),
        verticalArrangement = Arrangement.spacedBy(spacer)
    ) {
        Text(
            text = stringResource(R.string.name),
            fontSize = titleFontSize,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF395174),
        )

        ScrollableOutlinedTextField(
            text = title,
            onTextChange = onTitleChange,
            height = textFieldHeight
        )
    }
}

@Composable
fun EditUsername(title: String, onTitleChange: (String) -> Unit) {
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
            text = stringResource(R.string.username),
            fontSize = titleFontSize,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF395174),
        )

        ScrollableOutlinedTextField(
            text = title,
            onTextChange = onTitleChange,
            height = textFieldHeight
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

        ScrollableOutlinedTextField(
            text = title,
            onTextChange = onTitleChange,
            height = textFieldHeight
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditLanguage(
    selectedLanguage: String,
    onLanguageChange: (String) -> Unit
) {
    var isDropdownExpanded by remember { mutableStateOf(false) }
    val languages = listOf(stringResource(R.string.ingles), stringResource(R.string.portugues))
    val titleFontSize = (txtSize().value + 1).sp
    val titleFontSize2 = (txtSize().value + 9).sp
    val paddingValue = sbutton() - 6.dp

    fun getFlagEmoji(language: String): String {
        return when (language) {
            "Inglês" -> "\uD83C\uDDFA\uD83C\uDDF8"  // 🇺🇸
            "Português" -> "\uD83C\uDDF5\uD83C\uDDF9"  // 🇵🇹
            else -> ""
        }
    }

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
                value = selectedLanguage,
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
                leadingIcon = {
                    Text(
                        text = getFlagEmoji(selectedLanguage),
                        fontSize = titleFontSize2
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
                languages.forEach { language ->
                    DropdownMenuItem(
                        text = {
                            Row {
                                Text(
                                    text = getFlagEmoji(language),
                                    fontSize = titleFontSize2,
                                    modifier = Modifier.padding(end = 8.dp),
                                )
                                Text(text = language,
                                    color = Color(0xFF000000),)
                            }
                        },
                        onClick = {
                            onLanguageChange(language)
                            isDropdownExpanded = false
                        },
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
){
    var dayExpanded by remember { mutableStateOf(false) }
    var monthExpanded by remember { mutableStateOf(false) }
    var yearExpanded by remember { mutableStateOf(false) }

    val days = (1..31).map { it.toString() }
    val months = listOf(
        stringResource(R.string.january), stringResource(R.string.february), stringResource(R.string.march),
        stringResource(R.string.april), stringResource(R.string.may), stringResource(R.string.june),
        stringResource(R.string.july), stringResource(R.string.august), stringResource(R.string.september),
        stringResource(R.string.october), stringResource(R.string.november), stringResource(R.string.december)
    )
    val years = (1950..2010).map { it.toString() }

    val titleFontSize = (txtSize().value + 1).sp
    Spacer(modifier = Modifier.height(24.dp))

    // Birth Date
    Text(
        text = stringResource(R.string.birthdate),
        fontSize = titleFontSize,
        fontWeight = FontWeight.Medium,
        color = Color(0xFF395174),
        modifier = Modifier
            .padding(bottom = 8.dp)
            .padding(horizontal = 12.dp)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Day dropdown
        ExposedDropdownMenuBox(
            expanded = dayExpanded,
            onExpandedChange = { dayExpanded = !dayExpanded },
            modifier = Modifier.weight(0.65f)
        ) {
            OutlinedTextField(
                value = selectedDay,
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null
                    )
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF395174),
                    unfocusedBorderColor = Color(0xFF395174),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                textStyle = TextStyle(
                    fontSize = 14.sp,
                    color = Color.Black
                )
            )

            ExposedDropdownMenu(
                expanded = dayExpanded,
                onDismissRequest = { dayExpanded = false },
                modifier = Modifier.background(Color.White)
            ) {
                days.forEach { day ->
                    DropdownMenuItem(
                        text = { Text(day, color = Color(0xFF000000)) },
                        onClick = {
                            onDayChange(day)
                            dayExpanded = false
                        }
                    )
                }
            }
        }

        // Month dropdown
        ExposedDropdownMenuBox(
            expanded = monthExpanded,
            onExpandedChange = { monthExpanded = !monthExpanded },
            modifier = Modifier.weight(1.1f)
        ) {
            OutlinedTextField(
                value = selectedMonth,
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null
                    )
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF395174),
                    unfocusedBorderColor = Color(0xFF395174),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                textStyle = TextStyle(
                    fontSize = 14.sp,
                    color = Color.Black
                )
            )

            ExposedDropdownMenu(
                expanded = monthExpanded,
                onDismissRequest = { monthExpanded = false },
                modifier = Modifier.background(Color.White)
            ) {
                months.forEach { month ->
                    DropdownMenuItem(
                        text = { Text(month, color = Color(0xFF000000)) },
                        onClick = {
                            onMonthChange(month)
                            monthExpanded = false
                        }
                    )
                }
            }
        }

        // Year dropdown
        ExposedDropdownMenuBox(
            expanded = yearExpanded,
            onExpandedChange = { yearExpanded = !yearExpanded },
            modifier = Modifier.weight(0.75f)
        ) {
            OutlinedTextField(
                value = selectedYear,
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null
                    )
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF395174),
                    unfocusedBorderColor = Color(0xFF395174),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                textStyle = TextStyle(
                    fontSize = 14.sp,
                    color = Color.Black
                )
            )

            ExposedDropdownMenu(
                expanded = yearExpanded,
                onDismissRequest = { yearExpanded = false },
                modifier = Modifier.background(Color.White)
            ) {
                years.reversed().forEach { year ->
                    DropdownMenuItem(
                        text = { Text(year, color = Color(0xFF000000)) },
                        onClick = {
                            onYearChange(year)
                            yearExpanded = false
                        }
                    )
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
            .padding(top = paddingValue + 8.dp),
        verticalArrangement = Arrangement.spacedBy(spacer)
    ) {
        Text(
            text = stringResource(R.string.school_label),
            fontSize = titleFontSize,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF395174),
        )

        ScrollableOutlinedTextField(
            text = title,
            onTextChange = onTitleChange,
            height = textFieldHeight
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

        ScrollableOutlinedTextField(
            text = title,
            onTextChange = onTitleChange,
            height = textFieldHeight
        )
    }
}

@Composable
fun EditCYear(title: String, onTitleChange: (String) -> Unit) {
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
            text = stringResource(R.string.curricular_year_label),
            fontSize = titleFontSize,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF395174),
        )

        ScrollableOutlinedTextField(
            text = title,
            onTextChange = onTitleChange,
            height = textFieldHeight
        )
    }
}

@Composable
fun SaveBtn(
    enabled: Boolean
){
    val paddingValue = sbutton()
    val paddingValue2 = logoSize() - 10.dp
    val shareButtonSize = sbutton() - 8.dp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = paddingValue)
            .padding(vertical = paddingValue2)
    ) {
        Button(
            onClick = {},
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF395174),
                disabledContainerColor = Color(0xFFB0B0B0), // cor quando desativado (ex: cinzento claro)
                contentColor = Color.White,
                disabledContentColor = Color.White.copy(alpha = 0.6f)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = stringResource(R.string.save),
                fontSize = 16.sp,
                modifier = Modifier.padding(vertical = shareButtonSize)
            )
        }
    }
}

@Composable
fun CleanBtn(
    enabled: Boolean,
    onClean: () -> Unit
){
    val paddingValue = sbutton()
    val shareButtonSize = sbutton() - 8.dp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = paddingValue)
    ) {
        Button(
            onClick = onClean,
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF0000),
                disabledContainerColor = Color(0xFFB0B0B0), // cor quando desactivado (ex: cinzento claro)
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

@Composable
fun IndiceEditProfile(onBack: () -> Unit){
    // Valores originais (estado inicial)
    val originalName = "Miguel Silva"
    val originalUsername = "@michelangelo"
    val originalEmail = "mjosea@ipvc.pt"
    val originalSchool = "IPVC"
    val originalCourse = "Engenharia Informática"
    val originalYear = "2025"
    val originalLanguage = "Inglês"
    val originalDay = "22"
    val originalMonth = "June"
    val originalBirthYear = "1996"

    // Estados atuais
    var name by remember { mutableStateOf(originalName) }
    var username by remember { mutableStateOf(originalUsername) }
    var email by remember { mutableStateOf(originalEmail) }
    var school by remember { mutableStateOf(originalSchool) }
    var course by remember { mutableStateOf(originalCourse) }
    var year by remember { mutableStateOf(originalYear) }
    var selectedLanguage by remember { mutableStateOf(originalLanguage) }
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
                    year != originalYear ||
                    selectedLanguage != originalLanguage ||
                    selectedDay != originalDay ||
                    selectedMonth != originalMonth ||
                    selectedBirthYear != originalBirthYear
        }
    }

    // Função para limpar os campos (voltar aos valores originais)
    val resetFields = {
        name = originalName
        username = originalUsername
        email = originalEmail
        school = originalSchool
        course = originalCourse
        year = originalYear
        selectedLanguage = originalLanguage
        selectedDay = originalDay
        selectedMonth = originalMonth
        selectedBirthYear = originalBirthYear
    }

    // Estado para controlar o diálogo de alterações não salvas
    var showUnsavedDialog by remember { mutableStateOf(false) }

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
        // CORREÇÃO: Passar hasChanges em vez de false
        TopEditProfileBar(onBack = onBack, hasUnsavedChanges = hasChanges, onShowDialog = { showUnsavedDialog = true })
        IdentityWithEdit(modifier = Modifier.offset(y = 30.dp))
        EditName(name) { name = it }
        EditUsername(username) { username = it }
        EditEmail(email) { email = it }
        EditLanguage(
            selectedLanguage = selectedLanguage,
            onLanguageChange = { selectedLanguage = it }
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
        EditCYear(year) { year = it }
        SaveBtn(
            enabled = hasChanges
        )
        CleanBtn(
            enabled = hasChanges,
            onClean = resetFields
        )
    }
}

@Composable
fun EditProfileScreen(navController: NavHostController){
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
            IndiceEditProfile(onBack = {
                navController.navigate("MainScreen?selectedItem=4") {
                    popUpTo("settings") { inclusive = true }
                }
            })
        }
    }
}