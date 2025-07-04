package com.example.colearnhub.ui.screen.main

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.colearnhub.R
import com.example.colearnhub.modelLayer.Material
import com.example.colearnhub.ui.components.TagFilterSection
import com.example.colearnhub.ui.utils.Circles
import com.example.colearnhub.ui.utils.DateTimeUtils
import com.example.colearnhub.ui.utils.Nav
import com.example.colearnhub.ui.utils.SBar
import com.example.colearnhub.ui.utils.ScreenContent
import com.example.colearnhub.ui.utils.ScreenSize
import com.example.colearnhub.ui.utils.animation
import com.example.colearnhub.ui.utils.btnHeight
import com.example.colearnhub.ui.utils.dynamicPadding
import com.example.colearnhub.ui.utils.dynamicWidth
import com.example.colearnhub.ui.utils.getScreenSize
import com.example.colearnhub.ui.utils.txtSize
import com.example.colearnhub.ui.utils.verticalSpacing
import com.example.colearnhub.viewModelLayer.AuthViewModelFactory
import com.example.colearnhub.viewModelLayer.MaterialViewModel
import com.example.colearnhub.viewmodel.AuthViewModel
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.rememberDatePickerState
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.LocalDateTime
import java.time.ZoneOffset
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerState
import androidx.compose.ui.window.Dialog

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Indice(
    navController: NavController,
    materialViewModel: MaterialViewModel = viewModel()
) {
    // Obter contexto para passar ao factory
    val context = LocalContext.current.applicationContext

    // Criar AuthViewModel via factory passando o context
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(context)
    )

    val currentUser by authViewModel.currentUser.collectAsState()
    val currentUserId = currentUser?.id

    // Atualizar o ID do usuário atual no ViewModel
    LaunchedEffect(currentUserId) {
        materialViewModel.setCurrentUserId(currentUserId)
    }

    var selectedTab by remember { mutableIntStateOf(0) }
    val label1 = stringResource(id = R.string.All)
    val label2 = stringResource(id = R.string.Created)
    val tabs = listOf(label1, label2)
    val verticalSpacing = verticalSpacing()
    val btnHeight = btnHeight()
    val txtSize = txtSize()

    // Estados do ViewModel
    val materials by materialViewModel.materials.collectAsState()
    val userMaterials by materialViewModel.userMaterials.collectAsState()
    val isLoading by materialViewModel.isLoading.collectAsState()

    // Forçar loading state inicial
   // var isInitialLoad by remember { mutableStateOf(true) }

    // Carregar materiais públicos na inicialização
    LaunchedEffect(Unit) {
        Log.d("IndiceScreen", "Carregando materiais públicos")
        materialViewModel.loadPublicMaterials()
        // Observar o estado de loading do ViewModel
       /* materialViewModel.isLoading.collect { isLoading ->
            if (!isLoading) {
        isInitialLoad = false
            }
        }*/
    }

    LaunchedEffect(selectedTab, currentUserId) {
        Log.d("IndiceScreen", "selectedTab: $selectedTab, currentUserId: $currentUserId")

        if (selectedTab == 1) {
            if (currentUserId != null && currentUserId.isNotEmpty()) {
                Log.d("IndiceScreen", "Chamando loadMaterialsByAuthor com userId: $currentUserId")
                materialViewModel.loadMaterialsByAuthor(currentUserId)
            } else {
                Log.e("IndiceScreen", "currentUserId é null ou vazio!")
            }
        } else if (selectedTab == 0) {
            // Recarregar materiais públicos sempre que a aba All for selecionada
            materialViewModel.loadPublicMaterials()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        SearchBar(materialViewModel = materialViewModel)

        Spacer(modifier = Modifier.height(65.dp))

        // Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tabs.forEachIndexed { index, title ->
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Button(
                        onClick = {
                            Log.d("IndiceScreen", "Tab clicada: $index")
                            selectedTab = index
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(btnHeight),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedTab == index) Color(0xC9E9F2FF) else Color.Transparent,
                            contentColor = Color(0xFF395174)
                        ),
                        elevation = ButtonDefaults.buttonElevation(0.dp)
                    ) {
                        Text(
                            text = title,
                            fontSize = txtSize,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF395174)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .background(
                                if (selectedTab == index) Color(0xFF395174) else Color.Transparent
                            )
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(verticalSpacing - 30.dp))

        Log.d("IndiceScreen", "Tab: $selectedTab, Materials count: ${if (selectedTab == 0) materials.size else userMaterials.size}")

        when (selectedTab) {
            0 -> ContentArea(
                materials = materials,
                isLoading = isLoading,
                materialViewModel = materialViewModel,
                navController = navController,
                isAllTab = true
            )
            1 -> ContentArea(
                materials = userMaterials,
                isLoading = isLoading,
                materialViewModel = materialViewModel,
                navController = navController,
                isAllTab = false
            )
        }
    }
}

@Composable
fun ShareButton() {
    val btnHeight = btnHeight()
    val txtSize = txtSize()

    Button(
        onClick = { },
        modifier = Modifier
            .fillMaxWidth()
            .height(btnHeight)
            .border(
                width = 1.5.dp,
                color = Color(0xFF395174),
                shape = RoundedCornerShape(10.dp)
            ),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color(0xFF395174)
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
    ) {
        Text(
            text = stringResource(R.string.Share),
            fontSize = txtSize,
            fontWeight = FontWeight.Bold
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ContentArea(
    materials: List<Material>,
    isLoading: Boolean,
    materialViewModel: MaterialViewModel,
    navController: NavController,
    isAllTab: Boolean = true
) {
    val padding = dynamicPadding()
    val animationSize = animation()
    val titleFontSize = txtSize()
    val verticalSpacing = verticalSpacing()
    val btnHeight = verticalSpacing() + 10.dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = padding)
            .padding(bottom = 100.dp)
    ) {
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        color = Color(0xFF395174),
                        strokeWidth = 4.dp
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = stringResource(R.string.loading_materials),
                        fontSize = titleFontSize,
                        color = Color(0xFF395174),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        } else if (materials.isEmpty()) {
            // Tela vazia - igual ao original
            Spacer(modifier = Modifier.height(verticalSpacing))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.animation))

                LottieAnimation(
                    composition = composition,
                    modifier = Modifier.size(animationSize),
                    iterations = LottieConstants.IterateForever
                )

                Spacer(modifier = Modifier.height(verticalSpacing - 16.dp))

                Text(
                    text = stringResource(R.string.not_found),
                    fontSize = titleFontSize,
                    color = Color.Black,
                )

                Text(
                    text = stringResource(R.string.be_the_first),
                    fontSize = (titleFontSize.value - 2).sp,
                    color = Color.Black,
                    modifier = Modifier.padding(top = 4.dp),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(verticalSpacing))

                Button(
                    onClick = { navController.navigate("MainScreen?selectedItem=2") {
                        popUpTo("share") { inclusive = true }
                    } },
                    modifier = Modifier
                        .width(dynamicWidth(maxWidth = 300.dp))
                        .height(btnHeight)
                        .border(
                            width = 1.5.dp,
                            color = Color(0xFF395174),
                            shape = RoundedCornerShape(10.dp)
                        ),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF395174)
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Text(
                        text = stringResource(R.string.Share),
                        fontSize = titleFontSize,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        } else {
            // Lista de materiais
            MaterialsList(
                materials = materials,
                materialViewModel = materialViewModel,
                navController = navController,
                isAllTab = isAllTab
            )
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MaterialsList(
    materials: List<Material>,
    materialViewModel: MaterialViewModel,
    navController: NavController,
    isAllTab: Boolean = true
) {
    // Ordenar materiais por data de criação (mais recentes primeiro)
    val sortedMaterials = materials.sortedByDescending { it.created_at }

    if (isAllTab) {
        // Lógica para aba All - dois mais recentes em Highlights
        val highlights = sortedMaterials.take(2)
        val others = sortedMaterials.drop(2)

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Seção Highlights
            if (highlights.isNotEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.highlights_title),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF395174),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                items(highlights) { material ->
                    MaterialCard(
                        material = material,
                        materialViewModel = materialViewModel,
                        isHighlight = true,
                        navController = navController
                    )
                }
            }

            // Seção Others
            if (others.isNotEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.others_title),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF395174),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                items(others) { material ->
                    MaterialCard(
                        material = material,
                        materialViewModel = materialViewModel,
                        isHighlight = false,
                        navController = navController
                    )
                }
            }
        }
    } else {
        // Lista simples ordenada por data para a aba Shared
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(sortedMaterials) { material ->
                MaterialCard(
                    material = material,
                    materialViewModel = materialViewModel,
                    isHighlight = false,
                    navController = navController
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MaterialCard(
    material: Material,
    materialViewModel: MaterialViewModel,
    isHighlight: Boolean,
    navController: NavController
) {
    val authorName = materialViewModel.getUserName(material.author_id)
    val (languageName, languageFlag) = materialViewModel.getLanguageInfo(material.language)
    val context = LocalContext.current

    // Lista de cores para as tags
    val tagColors = listOf(
        Color(0xFF4A90E2), // Azul
        Color(0xFF50C878), // Verde
        Color(0xFFFFA500), // Laranja
        Color(0xFFE91E63), // Rosa
        Color(0xFF9C27B0), // Roxo
        Color(0xFF00BCD4), // Ciano
        Color(0xFFFF6B6B), // Vermelho
        Color(0xFF795548)  // Marrom
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                // Navegar para a tela de detalhes do material
                navController.navigate("material_details/${material.id}")
            },
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        shape = RoundedCornerShape(8.dp)
    ){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)

        ) {
            // Linha superior: Título e informações adicionais
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = material.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Tags do material com cores diferentes
                    Row(
                        modifier = Modifier.padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Mostrar as tags do material (limitado a 3 para não ocupar muito espaço)
                        material.tags?.take(3)?.forEachIndexed { index, tag ->
                            MaterialTypeTag(
                                text = tag.description,
                                backgroundColor = tagColors[index % tagColors.size]
                            )
                        }

                        // Tag do formato do arquivo (se tiver file_url)
                        material.file_url?.let { fileUrl ->
                            val extension = fileUrl.substringAfterLast(".", "")
                            if (extension.isNotEmpty()) {
                                MaterialTypeTag(
                                    text = extension.uppercase(),
                                    backgroundColor = Color(0xFF6B7280)
                                )
                            }
                        }
                    }

                    // Mostrar descrição se disponível
                    material.description?.let { description ->
                        if (description.isNotEmpty()) {
                            Text(
                                text = description,
                                fontSize = 12.sp,
                                color = Color.Gray,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }

                // Tempo
                Text(
                    text = DateTimeUtils.formatTimeAgo(material.created_at, context),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Linha inferior: Estatísticas, idioma e autor
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Downloads (valores fictícios por enquanto)
                // Removed downloads icon
                // Spacer(modifier = Modifier.width(12.dp))

                // Rating
                if (material.average_rating != null) {
                    StatIcon(Icons.Default.Star, String.format("%.1f", material.average_rating), Color(0xFFFFA500))
                } else {
                    Text(
                        text = stringResource(R.string.no_rating_available),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))

                // Idioma com bandeira (se disponível)
                if (languageName.isNotEmpty()) {
                    LanguageTag(
                        language = languageName,
                        flagCode = languageFlag
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                }

                // Indicador de visibilidade
                if (!material.visibility!!) {
                    MaterialTypeTag(
                        text = "PRIVADO",
                        backgroundColor = Color(0xFFFF6B6B)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Autor
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Author",
                        tint = Color.Gray,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = authorName.ifEmpty { "Utilizador" },
                        fontSize = 12.sp,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun MaterialTypeTag(
    text: String,
    backgroundColor: Color
) {
    Box(
        modifier = Modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            color = Color.White,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun LanguageTag(
    language: String,
    flagCode: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = flagCode,
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = language,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun StatIcon(
    icon: ImageVector,
    value: String,
    tint: Color = Color.Gray
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = value,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

data class BottomNavItem(
    val label: String,
    val icon: ImageVector? = null,
    val drawableRes: Int? = null
)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(navController: NavController, initialSelectedItem: Int = 0) {
    var selectedItem by remember { mutableIntStateOf(initialSelectedItem) }

    Box(
        modifier = Modifier.fillMaxSize()
            .background(Color.White)
    ) {
        if(selectedItem == 0 || selectedItem == 1 || selectedItem == 3 || selectedItem == 4) {
            Circles()
        }

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            if(selectedItem == 1) {
                SBar(title = stringResource(R.string.study_session))
            }
            if(selectedItem == 3) {
                SBar(title = stringResource(R.string.Groups))
            }
            ScreenContent(selectedItem, navController)
        }

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

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    materialViewModel: MaterialViewModel = viewModel()
) {
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
        ScreenSize.SMALL -> 18.dp
        ScreenSize.MEDIUM -> 26.dp
        ScreenSize.LARGE -> 38.dp
    }

    // Obter o valor atual da pesquisa do ViewModel
    val currentSearchQuery by materialViewModel.currentSearchQuery.collectAsState()
    var searchQuery by remember { mutableStateOf(currentSearchQuery) }

    // Atualizar o searchQuery local quando o currentSearchQuery mudar
    LaunchedEffect(currentSearchQuery) {
        searchQuery = currentSearchQuery
    }

    var showFilterModal by remember { mutableStateOf(false) }
    var showTagFilter by remember { mutableStateOf(false) }
    var selectedFilterTime by remember { mutableStateOf<String?>(null) }

    // Estados do ViewModel
    val allTags by materialViewModel.allTags.collectAsState()
    val selectedTags by materialViewModel.selectedFilterTags.collectAsState()
    val startDateFilter by materialViewModel.startDateFilter.collectAsState()
    val endDateFilter by materialViewModel.endDateFilter.collectAsState()

    Column(
        modifier = Modifier
            .padding(5.dp),
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
                    style = TextStyle(
                        drawStyle = Stroke(width = 2f)
                    )
                )
            }
        }
        Spacer(Modifier.height(verticalSpacing))
        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                Log.d("SearchBar", "Search query changed: $it")
                materialViewModel.searchMaterials(it)
            },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            trailingIcon = {
                IconButton(onClick = {
                    showFilterModal = !showFilterModal
                    Log.d("SearchBar", "Filter button clicked. showFilterModal: $showFilterModal")
                }) {
                    Icon(Icons.Default.FilterList, contentDescription = "Filter")
                }
            },
            placeholder = { Text(stringResource(R.string.Search), color = Color.Gray) },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(10.dp)),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                cursorColor = Color.Black
            ),
            singleLine = true
        )

        // Filter Modal - FIXED VERSION
        if (showFilterModal) {
            Dialog(
                onDismissRequest = { showFilterModal = false }
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .fillMaxHeight(0.7f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Header - Fixed at top
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp)
                                .align(Alignment.TopCenter), // This works in Box scope
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.filters),
                                fontSize = (txtSize().value + 4).sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF395174)
                            )
                            IconButton(
                                onClick = { showFilterModal = false }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = Color.Gray
                                )
                            }
                        }

                        // Scrollable content
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 80.dp) // Space for header
                                .padding(horizontal = 24.dp)
                                .padding(bottom = 80.dp) // Space for buttons
                                .verticalScroll(rememberScrollState()),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Date Filter Section
                            var isDateFilterExpanded by remember { mutableStateOf(true) }
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                            ) {
                                // Date Filter Header
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { isDateFilterExpanded = !isDateFilterExpanded }
                                        .padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.CalendarMonth,
                                            contentDescription = "Date Filter",
                                            tint = Color(0xFF395174),
                                            modifier = Modifier.size(24.dp).padding(end = 8.dp)
                                        )
                                        Text(
                                            text = stringResource(R.string.date),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = txtSize(),
                                            color = Color(0xFF395174)
                                        )
                                    }
                                    Icon(
                                        imageVector = if (isDateFilterExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                        contentDescription = if (isDateFilterExpanded) "Collapse" else "Expand",
                                        tint = Color(0xFF395174)
                                    )
                                }

                                // Date Filter Content
                                if (isDateFilterExpanded) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 8.dp)
                                    ) {
                                        val datePickerState = rememberDatePickerState(selectableDates = object : SelectableDates {
                                            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                                                return true
                                            }
                                        })

                                        var showStartDatePicker by remember { mutableStateOf(false) }
                                        var showEndDatePicker by remember { mutableStateOf(false) }

                                        Column(modifier = Modifier.fillMaxWidth()) {
                                            // Start Date Picker
                                            OutlinedTextField(
                                                value = startDateFilter?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: "",
                                                onValueChange = { /* Não permite edição direta */ },
                                                readOnly = true,
                                                trailingIcon = {
                                                    IconButton(onClick = { showStartDatePicker = true }) {
                                                        Icon(Icons.Default.DateRange, contentDescription = "Select Start Date")
                                                    }
                                                },
                                                placeholder = { Text(stringResource(R.string.start_date)) },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable { showStartDatePicker = true },
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    focusedBorderColor = Color.LightGray,
                                                    unfocusedBorderColor = Color.LightGray,
                                                    focusedContainerColor = Color.White,
                                                    unfocusedContainerColor = Color.White,
                                                    focusedTextColor = Color.Black,
                                                    unfocusedTextColor = Color.Black,
                                                    cursorColor = Color.Black
                                                ),
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))

                                            // End Date Picker
                                            OutlinedTextField(
                                                value = endDateFilter?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: "",
                                                onValueChange = { /* Não permite edição direta */ },
                                                readOnly = true,
                                                trailingIcon = {
                                                    IconButton(onClick = { showEndDatePicker = true }) {
                                                        Icon(Icons.Default.DateRange, contentDescription = "Select End Date")
                                                    }
                                                },
                                                placeholder = { Text(stringResource(R.string.end_date)) },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable { showEndDatePicker = true },
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    focusedBorderColor = Color.LightGray,
                                                    unfocusedTextColor = Color.Black,
                                                    unfocusedBorderColor = Color.LightGray,
                                                    focusedContainerColor = Color.White,
                                                    unfocusedContainerColor = Color.White,
                                                    focusedTextColor = Color.Black,
                                                    cursorColor = Color.Black
                                                ),
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                        }

                                        if (showStartDatePicker) {
                                            DatePickerDialog(
                                                onDismissRequest = { showStartDatePicker = false },
                                                confirmButton = {
                                                    Button(onClick = {
                                                        datePickerState.selectedDateMillis?.let { millis ->
                                                            val localDateTime = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDateTime()
                                                            materialViewModel.setStartDateFilter(localDateTime)
                                                        }
                                                        showStartDatePicker = false
                                                    }) { Text(stringResource(R.string.select2)) }
                                                },
                                                dismissButton = {
                                                    Button(onClick = { showStartDatePicker = false }) { Text(
                                                        stringResource(R.string.cancel)
                                                    ) }
                                                }
                                            ) {
                                                DatePicker(state = datePickerState)
                                            }
                                        }

                                        if (showEndDatePicker) {
                                            DatePickerDialog(
                                                onDismissRequest = { showEndDatePicker = false },
                                                confirmButton = {
                                                    Button(onClick = {
                                                        datePickerState.selectedDateMillis?.let { millis ->
                                                            val localDateTime = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDateTime()
                                                            materialViewModel.setEndDateFilter(localDateTime)
                                                        }
                                                        showEndDatePicker = false
                                                    }) { Text(stringResource(R.string.select2)) }
                                                },
                                                dismissButton = {
                                                    Button(onClick = { showEndDatePicker = false }) { Text(
                                                        stringResource(R.string.cancel)
                                                    ) }
                                                }
                                            ) {
                                                DatePicker(state = datePickerState)
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Tag Filter Section
                            var isTagFilterExpanded by remember { mutableStateOf(true) }
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                            ) {
                                // Tag Filter Header
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { isTagFilterExpanded = !isTagFilterExpanded }
                                        .padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Bookmark,
                                            contentDescription = "Area Filter",
                                            tint = Color(0xFF395174),
                                            modifier = Modifier.size(24.dp).padding(end = 8.dp)
                                        )
                                        Text(
                                            text = stringResource(R.string.area),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = txtSize(),
                                            color = Color(0xFF395174)
                                        )
                                    }
                                    Icon(
                                        imageVector = if (isTagFilterExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                        contentDescription = if (isTagFilterExpanded) "Collapse" else "Expand",
                                        tint = Color(0xFF395174)
                                    )
                                }

                                // Tag Filter Content
                                if (isTagFilterExpanded) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 8.dp)
                                    ) {
                                        TagFilterSection(
                                            availableTags = allTags,
                                            selectedTags = selectedTags,
                                            onTagToggle = { tag ->
                                                materialViewModel.toggleTagFilter(tag)
                                            },
                                            onClearAll = {
                                                materialViewModel.clearTagFilter()
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // Action Buttons - Fixed at bottom
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp)
                                .align(Alignment.BottomCenter), // This works in Box scope
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Reset Button
                            Button(
                                onClick = {
                                    materialViewModel.resetAllFilters()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).padding(end = 8.dp)
                            ) {
                                Text(stringResource(R.string.clean), color = Color.Black)
                            }

                            // Apply Button
                            Button(
                                onClick = {
                                    materialViewModel.applyAllFilters()
                                    showFilterModal = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF395174)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).padding(start = 8.dp)
                            ) {
                                Text(stringResource(R.string.apply), color = Color.White)
                            }
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                stringResource(R.string.Knowledge),
                color = Color.White
                //fontWeight = FontWeight.Bold
            )
        }
    }
}