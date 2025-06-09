package com.example.colearnhub.ui.screen.main

import android.util.Log
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.colearnhub.R
import com.example.colearnhub.modelLayer.Material
import com.example.colearnhub.ui.utils.Circles
import com.example.colearnhub.ui.utils.DateTimeUtils
import com.example.colearnhub.ui.utils.Nav
import com.example.colearnhub.ui.utils.SBar
import com.example.colearnhub.ui.utils.ScreenContent
import com.example.colearnhub.ui.utils.SearchBar
import com.example.colearnhub.ui.utils.animation
import com.example.colearnhub.ui.utils.btnHeight
import com.example.colearnhub.ui.utils.dynamicPadding
import com.example.colearnhub.ui.utils.dynamicWidth
import com.example.colearnhub.ui.utils.txtSize
import com.example.colearnhub.ui.utils.verticalSpacing
import com.example.colearnhub.viewModelLayer.AuthViewModelFactory
import com.example.colearnhub.viewModelLayer.MaterialViewModel
import com.example.colearnhub.viewmodel.AuthViewModel

@Composable
fun Indice(
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

    // Carregar materiais públicos na inicialização
    LaunchedEffect(Unit) {
        Log.d("IndiceScreen", "Carregando materiais públicos")
        materialViewModel.loadPublicMaterials()
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
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Spacer(modifier = Modifier.height(verticalSpacing))

        // Botão Share - posicionado no topo quando há dados
        val currentMaterials = if (selectedTab == 0) materials else userMaterials
        if (currentMaterials.isNotEmpty() && !isLoading) {
            ShareButton()
            Spacer(modifier = Modifier.height(16.dp))
        }

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
                materialViewModel = materialViewModel
            )
            1 -> ContentArea(
                materials = userMaterials,
                isLoading = isLoading,
                materialViewModel = materialViewModel
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

@Composable
fun ContentArea(
    materials: List<Material>,
    isLoading: Boolean,
    materialViewModel: MaterialViewModel
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
    ) {
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = Color(0xFF395174),
                    modifier = Modifier.padding(16.dp)
                )
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
                    onClick = { },
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
                materialViewModel = materialViewModel
            )
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun MaterialsList(
    materials: List<Material>,
    materialViewModel: MaterialViewModel
) {
    val highlights = materials.take(2)
    val others = materials.drop(2)

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Seção Highlights
        if (highlights.isNotEmpty()) {
            item {
                Text(
                    text = "Highlights",
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
                    isHighlight = true
                )
            }
        }

        // Seção Others
        if (others.isNotEmpty()) {
            item {
                Text(
                    text = "Others",
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
                    isHighlight = false
                )
            }
        }
    }
}

@Composable
fun MaterialCard(
    material: Material,
    materialViewModel: MaterialViewModel,
    isHighlight: Boolean
) {
    val authorName = materialViewModel.getUserName(material.author_id)
    val (languageName, languageFlag) = materialViewModel.getLanguageInfo(material.language)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Navegar para detalhes */ },
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
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

                    // Tags do material (agora usa a lista de tags do novo sistema)
                    Row(
                        modifier = Modifier.padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Mostrar as tags do material (limitado a 3 para não ocupar muito espaço)
                        material.tags?.take(3)?.forEach { tag ->
                            MaterialTypeTag(
                                text = tag.description,
                                backgroundColor = Color(0xFF4A90E2)
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
                    text = DateTimeUtils.formatTimeAgo(material.created_at),
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
                StatIcon(Icons.Default.Download, "203")
                Spacer(modifier = Modifier.width(12.dp))

                // Rating (valores fictícios por enquanto)
                StatIcon(Icons.Default.Star, "5.0", Color(0xFFFFA500))
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