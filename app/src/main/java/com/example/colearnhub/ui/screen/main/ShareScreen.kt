package com.example.colearnhub.ui.screen.main

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.colearnhub.R
import com.example.colearnhub.ui.utils.Circles
import com.example.colearnhub.ui.utils.Nav
import com.example.colearnhub.ui.utils.SBar
import com.example.colearnhub.ui.utils.ScreenContent
import com.example.colearnhub.ui.utils.ScreenSize
import com.example.colearnhub.ui.utils.SearchBar
import com.example.colearnhub.ui.utils.getFileExtension
import com.example.colearnhub.ui.utils.getFileIcon
import com.example.colearnhub.ui.utils.getScreenSize
import com.example.colearnhub.ui.utils.logoSize
import com.example.colearnhub.ui.utils.sbutton
import com.example.colearnhub.ui.utils.spacer2
import com.example.colearnhub.ui.utils.textFieldHeight
import com.example.colearnhub.ui.utils.txtSize

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
            text = stringResource(R.string.Share),
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
    val borderColor = Color(0xFF395174)
    val backgroundColor = Color.White
    val titleFontSize = txtSize()

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
            .padding(8.dp)
    ) {
        BasicTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            textStyle = TextStyle(fontSize = titleFontSize, color = Color.Black),
            cursorBrush = SolidColor(Color.Black)
        )
    }
}

@Composable
fun Title(title: String, onTitleChange: (String) -> Unit) {
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
            text = stringResource(R.string.Title_label),
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
fun Upload(selectedFileUri: Uri?, onFileSelected: (Uri?) -> Unit) {
    var selectedFileName by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    val documentPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            onFileSelected(it)  // Atualiza o estado externo
            val cursor = context.contentResolver.query(it, null, null, null, null)
            cursor?.use { c ->
                if (c.moveToFirst()) {
                    val nameIndex = c.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                    if (nameIndex >= 0) {
                        selectedFileName = c.getString(nameIndex)
                    }
                }
            }
            if (selectedFileName == null) {
                selectedFileName = "document.${getFileExtension(it.toString())}"
            }
        }
    }

    val titleFontSize = (txtSize().value + 1).sp

    Column(modifier = Modifier.padding(16.dp)) {
        if (selectedFileUri != null && selectedFileName != null) {
            Text(
                text = stringResource(R.string.file_uploaded),
                fontSize = titleFontSize,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF395174),
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            OutlinedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .padding(horizontal = 32.dp),
                colors = CardDefaults.outlinedCardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder().copy(width = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = getFileIcon(selectedFileName),
                        contentDescription = "Document",
                        tint = Color(0xFF395174),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = selectedFileName ?: stringResource(R.string.unknown_file),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF333333),
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = {
                        onFileSelected(null)
                        selectedFileName = null
                    }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove file",
                            tint = Color(0xFF999999),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        Button(
            onClick = { documentPickerLauncher.launch("*/*") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF395174)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(Icons.Default.Upload, contentDescription = "Upload", tint = Color.White, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(if (selectedFileUri != null) stringResource(R.string.change_file) else stringResource(R.string.upload_file)
                    ,color = Color.White,)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Language() {
    var selectedLanguage by remember { mutableStateOf("Inglês") }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    val languages = listOf(stringResource(R.string.ingles), stringResource(R.string.portugues))
    val titleFontSize = (txtSize().value + 1).sp
    val titleFontSize2 = (txtSize().value + 9).sp

    fun getFlagEmoji(language: String): String {
        return when (language) {
            "Inglês" -> "\uD83C\uDDFA\uD83C\uDDF8"  // 🇺🇸
            "Português" -> "\uD83C\uDDF5\uD83C\uDDF9"  // 🇵🇹
            else -> ""
        }
    }

    Column(modifier = Modifier.padding(horizontal = 50.dp)) {
        Text(
            text = stringResource(R.string.language),
            fontSize = titleFontSize,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF395174),
            modifier = Modifier.padding(bottom = 8.dp)
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
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
                            selectedLanguage = language
                            isDropdownExpanded = false
                        },
                    )
                }
            }
        }
    }
}

@Composable
fun Description() {
    var title by remember { mutableStateOf("") }

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
                onTextChange = { title = it },
                height = textFieldHeight
            )
        }
    }
}

@Composable
fun Area(){
    val paddingValue = logoSize() + 10.dp
    val paddingValue2 = logoSize() - 20.dp
    val titleFontSize = (txtSize().value + 1).sp
    Column {
        Text(
            text = stringResource(R.string.area),
            fontSize = titleFontSize,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF395174),
            modifier = Modifier.padding(bottom = 8.dp)
                .padding(horizontal = paddingValue)
                .padding(top = paddingValue2)
        )

        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = paddingValue),
            horizontalArrangement = Arrangement.Start
        ) {
            Button(
                onClick = {
                    // TODO: Implement tag selection functionality
                },
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
        }
    }
}

@Composable
fun Private(){
    var isPrivate by remember { mutableStateOf(false) }
    val titleFontSize = (txtSize().value + 1).sp
    val paddingValue = logoSize() + 10.dp
    val paddingValue2 = logoSize() - 20.dp
    val top = sbutton()
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = paddingValue)
            .padding(top = paddingValue2),
    ) {
        Text(
            text = stringResource(R.string.priv),
            fontSize = titleFontSize,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF395174),
            modifier = Modifier.padding(top = top)
        )

        Switch(
            checked = isPrivate,
            onCheckedChange = { isPrivate = it },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF5A6B7D),
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFCCCCCC)
            ),
            modifier = Modifier.padding(start = top - 4.dp)
        )
    }
}

@Composable
fun ShareBtn(enabled: Boolean) {
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
            onClick = { /* ação de partilhar */ },
            enabled = enabled, // usar o parâmetro passado
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = paddingValue2),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF395174),
                disabledContainerColor = Color(0xFFB0B0B0), // cor quando desativado (ex: cinzento claro)
                contentColor = Color.White,
                disabledContentColor = Color.White.copy(alpha = 0.6f)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = stringResource(R.string.Share),
                fontSize = 16.sp,
                modifier = Modifier.padding(vertical = shareButtonSize)
            )
        }
    }
}

@Composable
fun Indice4(){
    var title by remember { mutableStateOf("") }
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    val isShareEnabled = title.isNotBlank() && selectedFileUri != null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 80.dp)
    ) {
        TopBar()
        Title(title) { title = it }               // <-- Passa o estado e callback
        Upload(selectedFileUri) { selectedFileUri = it }  // <-- idem
        Language()
        Description()
        Area()
        Private()
        ShareBtn(isShareEnabled)                  // <-- Passa o estado booleano
    }
}

@Composable
fun ShareScreen(navController: NavController) {
    var selectedItem by remember { mutableIntStateOf(2) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        if (selectedItem == 0 || selectedItem == 1 || selectedItem == 3 || selectedItem == 4) {
            Circles()
        }

        Column(
            modifier = Modifier
                .padding(bottom = 80.dp)
        ) {
            if(selectedItem == 0){
                SearchBar()
            }
            if(selectedItem == 1) {
                SBar(title = stringResource(R.string.study_session))
            }
            if(selectedItem == 3) {
                SBar(title = stringResource(R.string.Groups))
            }
            ScreenContent(selectedItem, navController)
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            if (selectedItem == 0 || selectedItem == 1 || selectedItem == 2 || selectedItem == 3 || selectedItem == 4) {
                Nav(
                    selectedItem = selectedItem,
                    onItemSelected = { newIndex ->
                        selectedItem = newIndex
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                )
            }
        }
    }
}
