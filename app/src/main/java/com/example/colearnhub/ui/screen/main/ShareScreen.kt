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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.style.TextAlign
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import android.util.Log
import androidx.compose.foundation.clickable
import com.example.colearnhub.modelLayer.SupabaseClient
import com.example.colearnhub.repositoryLayer.AuthRepository
import com.example.colearnhub.repositoryLayer.MaterialsRepository
import com.example.colearnhub.repositoryLayer.TagRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

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
    val spacer = spacer2()
    val textFieldHeight = textFieldHeight()
    val paddingValue = logoSize() + 10.dp
    val titleFontSize = (txtSize().value + 1).sp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = paddingValue)
            .padding(top = paddingValue)
            .padding(bottom = 8.dp),
        verticalArrangement = Arrangement.spacedBy(spacer)
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
fun TagSelectionDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onTagsSelected: (List<String>) -> Unit
) {
    var selectedTags by remember { mutableStateOf(listOf<String>()) }
    var tags by remember { mutableStateOf(listOf<String>()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // Carregar tags quando o diálogo é aberto
    LaunchedEffect(showDialog) {
        if (showDialog) {
            isLoading = true
            error = null
            try {
                val tagRepository = TagRepository()
                val result = tagRepository.getAllTags()
                tags = result.map { it.description }
            } catch (e: Exception) {
                error = "Erro ao carregar tags: ${e.message}"
                Log.e("TagSelectionDialog", "Error loading tags", e)
            } finally {
                isLoading = false
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = "Selecione as áreas",
                    color = Color(0xFF395174),
                    fontSize = 18.sp
                )
            },
            text = {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    if (isLoading) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = Color(0xFF395174),
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    } else if (error != null) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = error ?: "Erro desconhecido",
                                color = Color.Red,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    } else {
                        LazyColumn {
                            items(tags) { tag ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            selectedTags = if (selectedTags.contains(tag)) {
                                                selectedTags - tag
                                            } else {
                                                selectedTags + tag
                                            }
                                        }
                                        .padding(vertical = 8.dp, horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = selectedTags.contains(tag),
                                        onCheckedChange = { checked ->
                                            selectedTags = if (checked) {
                                                selectedTags + tag
                                            } else {
                                                selectedTags - tag
                                            }
                                        }
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = tag,
                                        fontSize = 16.sp,
                                        color = Color(0xFF395174)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onTagsSelected(selectedTags)
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF395174)
                    )
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Gray
                    )
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun Area(
    selectedTags: List<String>,
    onTagsSelected: (List<String>) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
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

            // Mostrar tags selecionadas
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
            }
        }
    }

    TagSelectionDialog(
        showDialog = showDialog,
        onDismiss = { showDialog = false },
        onTagsSelected = onTagsSelected
    )
}

@Composable
fun Upload(
    selectedFileUri: Uri?,
    onFileSelected: (Uri?) -> Unit,
    onFileUploaded: (String?) -> Unit // Nova callback para quando o upload terminar
) {
    var selectedFileName by remember { mutableStateOf<String?>(null) }
    var isUploading by remember { mutableStateOf(false) }
    var uploadError by remember { mutableStateOf<String?>(null) }
    var uploadedFileUrl by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val documentPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            onFileSelected(it)
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
                selectedFileName = "document_${System.currentTimeMillis()}.${getFileExtension(it.toString())}"
            }

            // Upload para o Supabase
            coroutineScope.launch {
                isUploading = true
                uploadError = null
                try {
                    val fileBytes = context.contentResolver.openInputStream(it)?.readBytes()
                    fileBytes?.let { bytes ->
                        val fileName = selectedFileName ?: "document_${System.currentTimeMillis()}.${getFileExtension(it.toString())}"
                        val path = fileName

                        try {
                            val bucket = SupabaseClient.client.storage["materials"]

                            bucket.upload(path, bytes) {
                                upsert = true
                            }

                            val publicUrl = bucket.publicUrl(path)
                            uploadedFileUrl = publicUrl
                            onFileUploaded(publicUrl) // Notificar que o upload terminou
                            Log.d("Upload", "File uploaded successfully. URL: $publicUrl")

                        } catch (e: Exception) {
                            Log.e("Upload", "Storage error", e)
                            uploadError = when {
                                e.message?.contains("row-level security policy") == true ->
                                    "Erro de permissão. Verifique se você está autenticado."
                                e.message?.contains("bucket") == true ->
                                    "Erro no bucket de armazenamento. Verifique as configurações."
                                else -> "Erro no upload: ${e.message}"
                            }
                            onFileUploaded(null)
                        }
                    } ?: run {
                        uploadError = "Erro ao ler o arquivo"
                        onFileUploaded(null)
                        Log.e("Upload", "Failed to read file bytes")
                    }
                } catch (e: Exception) {
                    uploadError = "Erro no upload: ${e.message}"
                    onFileUploaded(null)
                    Log.e("Upload", "Error uploading file", e)
                } finally {
                    isUploading = false
                }
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

                    if (isUploading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color(0xFF395174),
                            strokeWidth = 2.dp
                        )
                    } else {
                        IconButton(onClick = {
                            onFileSelected(null)
                            selectedFileName = null
                            uploadError = null
                            uploadedFileUrl = null
                            onFileUploaded(null)
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

            uploadError?.let { error ->
                Text(
                    text = error,
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 32.dp, vertical = 4.dp)
                )
            }
        }

        Button(
            onClick = { documentPickerLauncher.launch("*/*") },
            enabled = !isUploading,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF395174),
                disabledContainerColor = Color(0xFFB0B0B0)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            if (isUploading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Uploading...", color = Color.White)
            } else {
                Icon(Icons.Default.Upload, contentDescription = "Upload", tint = Color.White, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    if (selectedFileUri != null) stringResource(R.string.change_file) else stringResource(R.string.upload_file),
                    color = Color.White
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Language(
    selectedLanguage: String,
    onLanguageSelected: (String) -> Unit
) {
    var isDropdownExpanded by remember { mutableStateOf(false) }
    val languages = listOf(stringResource(R.string.ingles), stringResource(R.string.portugues))
    val titleFontSize = (txtSize().value + 1).sp
    val titleFontSize2 = (txtSize().value + 9).sp

    fun getFlagEmoji(language: String): String {
        return when (language) {
            "Inglês" -> "\uD83C\uDDFA\uD83C\uDDF8"
            "Português" -> "\uD83C\uDDF5\uD83C\uDDF9"
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
                            onLanguageSelected(language)
                            isDropdownExpanded = false
                        },
                    )
                }
            }
        }
    }
}

@Composable
fun Description(
    description: String,
    onDescriptionChange: (String) -> Unit
) {
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
                text = description,
                onTextChange = onDescriptionChange,
                height = textFieldHeight
            )
        }
    }
}

@Composable
fun Private(
    isPrivate: Boolean,
    onPrivateChange: (Boolean) -> Unit
){
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
            onCheckedChange = onPrivateChange,
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
fun ShareBtn(
    enabled: Boolean,
    isCreating: Boolean,
    onClick: () -> Unit
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
            onClick = onClick,
            enabled = enabled && !isCreating,
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
                    text = stringResource(R.string.Share),
                    fontSize = 16.sp,
                    modifier = Modifier.padding(vertical = shareButtonSize)
                )
            }
        }
    }
}
@Composable
fun Indice4(){
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var uploadedFileUrl by remember { mutableStateOf<String?>(null) }
    var selectedTags by remember { mutableStateOf(listOf<String>()) }
    var selectedLanguage by remember { mutableStateOf("Inglês") }
    var isPrivate by remember { mutableStateOf(false) }
    var isCreatingMaterial by remember { mutableStateOf(false) }
    var createError by remember { mutableStateOf<String?>(null) }
    var createSuccess by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    // Condição para habilitar o botão de compartilhar
    val isShareEnabled = title.isNotBlank() && uploadedFileUrl != null

    // Função para converter idioma para ID
    fun getLanguageId(language: String): Long {
        return when (language) {
            "Inglês" -> 1L
            "Português" -> 2L
            else -> 1L
        }
    }

    // Função para converter tags em IDs - CORRIGIDA
    suspend fun getTagIds(tagNames: List<String>): List<Long> {
        return if (tagNames.isEmpty()) {
            emptyList()
        } else {
            try {
                val tagRepository = TagRepository()
                val tagIds = tagRepository.getTagIdsByNames(tagNames)
                Log.d("ShareScreen", "Tags convertidas: $tagNames -> $tagIds")
                tagIds
            } catch (e: Exception) {
                Log.e("ShareScreen", "Erro ao converter tags: ${e.message}")
                emptyList()
            }
        }
    }

    // Função para criar o material - ATUALIZADA
    fun createMaterial() {
        coroutineScope.launch {
            isCreatingMaterial = true
            createError = null

            try {
                val materialsRepository = MaterialsRepository()
                val authRepository = AuthRepository()

                // Converter tags para IDs ANTES de criar o material
                val tagIds = getTagIds(selectedTags)
                val currentUser = authRepository.getCurrentUser()

                Log.d("ShareScreen", "=== Criando material ===")
                Log.d("ShareScreen", "Título: $title")
                Log.d("ShareScreen", "Tags selecionadas: $selectedTags")
                Log.d("ShareScreen", "Tag IDs: $tagIds")
                Log.d("ShareScreen", "Usuário: ${currentUser?.id}")

                val material = materialsRepository.createMaterial(
                    title = title,
                    description = description.ifBlank { null },
                    file_url = uploadedFileUrl,
                    visibility = !isPrivate, // visibility é o oposto de private
                    language = getLanguageId(selectedLanguage),
                    author_id = currentUser?.id,
                    tagIds = tagIds.ifEmpty { null }
                )

                if (material != null) {
                    createSuccess = true
                    Log.d("ShareScreen", "Material criado com sucesso: ${material.id}")
                    Log.d("ShareScreen", "Tags associadas: ${material.tags?.map { it.description }}")

                    // Resetar o formulário após sucesso
                    title = ""
                    description = ""
                    selectedFileUri = null
                    uploadedFileUrl = null
                    selectedTags = emptyList()
                    selectedLanguage = "Inglês"
                    isPrivate = false
                } else {
                    createError = "Erro ao criar material"
                }
            } catch (e: Exception) {
                createError = "Erro: ${e.message}"
                Log.e("ShareScreen", "Erro ao criar material", e)
            } finally {
                isCreatingMaterial = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 80.dp)
    ) {
        TopBar()

        Title(title) { title = it }

        Upload(
            selectedFileUri = selectedFileUri,
            onFileSelected = { selectedFileUri = it },
            onFileUploaded = { url -> uploadedFileUrl = url }
        )

        Language(
            selectedLanguage = selectedLanguage,
            onLanguageSelected = { selectedLanguage = it }
        )

        Description(
            description = description,
            onDescriptionChange = { description = it }
        )

        Area(
            selectedTags = selectedTags,
            onTagsSelected = { selectedTags = it }
        )

        Private(
            isPrivate = isPrivate,
            onPrivateChange = { isPrivate = it }
        )

        ShareBtn(
            enabled = isShareEnabled,
            isCreating = isCreatingMaterial,
            onClick = { createMaterial() }
        )

        // Mostrar tags selecionadas para debug
        if (selectedTags.isNotEmpty()) {
            Text(
                text = "Tags selecionadas: ${selectedTags.joinToString(", ")}",
                color = Color.Gray,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 32.dp, vertical = 4.dp)
            )
        }

        // Mostrar mensagens de erro ou sucesso
        createError?.let { error ->
            Text(
                text = error,
                color = Color.Red,
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 32.dp, vertical = 8.dp)
            )
        }

        if (createSuccess) {
            Text(
                text = "Material criado com sucesso!",
                color = Color.Green,
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 32.dp, vertical = 8.dp)
            )

            // Limpar mensagem de sucesso após alguns segundos
            LaunchedEffect(createSuccess) {
                kotlinx.coroutines.delay(3000)
                createSuccess = false
            }
        }
    }
}