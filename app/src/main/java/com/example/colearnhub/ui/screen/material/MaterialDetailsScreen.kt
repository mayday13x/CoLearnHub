package com.example.colearnhub.ui.screen.main

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.colearnhub.modelLayer.Comments
import com.example.colearnhub.modelLayer.Material
import com.example.colearnhub.repositoryLayer.CommentsRepository
import com.example.colearnhub.repositoryLayer.MaterialsRepository
import com.example.colearnhub.repositoryLayer.UserRepository
import com.example.colearnhub.viewModelLayer.AuthViewModelFactory
import com.example.colearnhub.viewmodel.AuthViewModel
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.util.*


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaterialDetailsScreen(
    navController: NavController,
    materialId: String
) {
    // Obter contexto para passar ao factory
    val context = LocalContext.current.applicationContext

    // Criar AuthViewModel via factory passando o context
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(context)
    )

    val currentUser by authViewModel.currentUser.collectAsState()
    val currentUserId = currentUser?.id

    val materialsRepository = remember { MaterialsRepository() }
    val commentsRepository = remember { CommentsRepository() }
    val userRepository = remember { UserRepository(context) }
    val scope = rememberCoroutineScope()

    var material by remember { mutableStateOf<Material?>(null) }
    var comments by remember { mutableStateOf<List<Comments>>(emptyList()) }
    var commentText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var authorName by remember { mutableStateOf<String?>(null) }
    var replyingTo by remember { mutableStateOf<Comments?>(null) }
    var userNames by remember { mutableStateOf<Map<String, String>>(emptyMap()) }

    // Handle back navigation
    BackHandler {
        if (replyingTo != null) {
            replyingTo = null
        } else {
            navController.popBackStack()
        }
    }

    // Load material, comments and author data
    LaunchedEffect(materialId) {
        try {
            isLoading = true
            material = materialsRepository.getMaterialByIdWithTags(materialId)

            // Fetch author name
            material?.author_id?.let { authorId ->
                userRepository.getUserById(authorId)?.let { user ->
                    authorName = user.name
                }
            }

            // Fetch comments
            comments = commentsRepository.getCommentsForMaterial(materialId)

            // Fetch user names for all comments
            val userIds = comments.flatMap { comment ->
                listOf(comment.user_id) + comment.responses.map { it.user_id }
            }.filterNotNull().distinct()

            val names = mutableMapOf<String, String>()
            userIds.forEach { userId ->
                userRepository.getUserById(userId)?.let { user ->
                    names[userId] = user.name
                }
            }
            userNames = names

            error = null
        } catch (e: Exception) {
            error = e.message
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        TopAppBar(
            title = {
                Text(
                    text = material?.title ?: "Material Details",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = {
                    scope.launch {
                        replyingTo = null
                        navController.popBackStack("MainScreen", false)
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFF395174)
            )
        )

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (error != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Error: $error",
                    color = Color.Red
                )
            }
        } else {
            material?.let { material ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Material Info
                    item {
                        MaterialInfoSection(
                            material = material,
                            authorName = authorName
                        )
                    }

                    // Action Buttons
                    item {
                        val activityContext = LocalContext.current
                        ActionButtonsSection(
                            material = material,
                            context = activityContext
                        )
                    }

                    // Tags
                    item {
                        TagsSection(tags = material.tags?.map { it.description } ?: emptyList())
                    }

                    // Comments Header
                    item {
                        CommentsHeader(
                            commentCount = comments.size,
                            replyingTo = replyingTo,
                            onCancelReply = { replyingTo = null },
                            userName = replyingTo?.user_id?.let { userNames[it] }
                        )
                    }

                    // Comment Input - só mostra se o usuário estiver logado
                    if (currentUserId != null) {
                        item {
                            CommentInputSection(
                                commentText = commentText,
                                onCommentChange = { commentText = it },
                                onSendComment = {
                                    scope.launch {
                                        try {
                                            commentsRepository.createComment(
                                                userId = currentUserId,
                                                materialId = materialId,
                                                content = commentText,
                                                responseTo = replyingTo?.id
                                            )
                                            // Refresh comments
                                            comments = commentsRepository.getCommentsForMaterial(materialId)

                                            // Ensure current user's name is in the map after commenting
                                            currentUserId?.let { userId ->
                                                if (!userNames.containsKey(userId)) {
                                                    userRepository.getUserById(userId)?.let { user ->
                                                        userNames = userNames + (userId to user.name)
                                                    }
                                                }
                                            }

                                            commentText = ""
                                            replyingTo = null
                                        } catch (e: Exception) {
                                            error = e.message
                                        }
                                    }
                                },
                                replyingTo = replyingTo
                            )
                        }
                    } else {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFFF5F5F5)
                                )
                            ) {
                                Text(
                                    text = "Faça login para comentar",
                                    modifier = Modifier.padding(16.dp),
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    // Comments List
                    items(comments) { comment ->
                        CommentItem(
                            comment = comment,
                            userName = userNames[comment.user_id],
                            onReply = {
                                // Só permite resposta se estiver logado
                                if (currentUserId != null) {
                                    replyingTo = it
                                }
                            },
                            onDelete = { commentId ->
                                scope.launch {
                                    try {
                                        commentsRepository.deleteComment(commentId)
                                        // Refresh comments
                                        comments = commentsRepository.getCommentsForMaterial(materialId)
                                    } catch (e: Exception) {
                                        error = e.message
                                    }
                                }
                            },
                            canInteract = currentUserId != null
                        )
                    }
                }
            }
        }
    }
}

// Helper function to get MIME type from URL
private fun getMimeType(url: String): String? {
    val extension = MimeTypeMap.getFileExtensionFromUrl(url)
    return if (extension != null) {
        MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.lowercase())
    } else {
        // Fallback based on file extension from URL
        val fileExtension = url.substringAfterLast('.', "").lowercase()
        when (fileExtension) {
            "pdf" -> "application/pdf"
            "doc" -> "application/msword"
            "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            "ppt" -> "application/vnd.ms-powerpoint"
            "pptx" -> "application/vnd.openxmlformats-officedocument.presentationml.presentation"
            "xls" -> "application/vnd.ms-excel"
            "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "gif" -> "image/gif"
            "txt" -> "text/plain"
            "rtf" -> "application/rtf"
            else -> null
        }
    }
}

// Helper function to download file
private fun downloadFile(context: Context, url: String, fileName: String) {
    try {
        val request = DownloadManager.Request(Uri.parse(url)).apply {
            setTitle("Downloading $fileName")
            setDescription("Download em progresso...")
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
            setAllowedOverMetered(true)
            setAllowedOverRoaming(true)
        }

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)

        Toast.makeText(context, "Download a iniciar...", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Erro ao fazer download: ${e.message}", Toast.LENGTH_LONG).show()
    }
}

// Helper function to preview file - sempre abre no browser
private fun previewFile(context: Context, url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        context.startActivity(intent)
        Toast.makeText(context, "A abrir preview no browser...", Toast.LENGTH_SHORT).show()

    } catch (e: Exception) {
        Toast.makeText(context, "Erro ao abrir no navegador: ${e.message}", Toast.LENGTH_LONG).show()
    }
}

// Helper function to get filename from URL
private fun getFileNameFromUrl(url: String, title: String): String {
    return try {
        val uri = Uri.parse(url)
        val path = uri.path
        if (path != null && path.contains("/")) {
            val fileName = path.substringAfterLast("/")
            if (fileName.contains(".")) {
                fileName
            } else {
                // If no extension, try to add one based on detected MIME type
                val mimeType = getMimeType(url)
                val extension = when (mimeType) {
                    "application/pdf" -> ".pdf"
                    "application/msword" -> ".doc"
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document" -> ".docx"
                    else -> ""
                }
                "${title.take(50)}$extension"
            }
        } else {
            "${title.take(50)}.pdf" // Default to PDF if can't determine
        }
    } catch (e: Exception) {
        "${title.take(50)}.pdf"
    }
}

@Composable
fun CommentsHeader(
    commentCount: Int,
    replyingTo: Comments?,
    onCancelReply: () -> Unit,
    userName: String? = null
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Comments | 📝 $commentCount",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Text(
                text = "Sort by ⚡",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }

        // Show replying to message if applicable
        replyingTo?.let {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Replying to: ${userName ?: "Unknown User"}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                TextButton(
                    onClick = onCancelReply,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = "Cancel",
                        color = Color.Red,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CommentItem(
    comment: Comments,
    userName: String?,
    onReply: (Comments) -> Unit,
    onDelete: (Int) -> Unit,
    canInteract: Boolean = true
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(Color(0xFF395174), CircleShape)
            )

            Column(modifier = Modifier.weight(1f)) {
                // Author and time
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = userName ?: "Unknown User",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = formatTimeAgo(comment.created_at),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                // Comment content
                if (!comment.content.isNullOrEmpty()) {
                    Text(
                        text = comment.content,
                        fontSize = 14.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(top = 4.dp),
                        lineHeight = 18.sp
                    )
                }

                // Actions - só mostra se o usuário pode interagir
                if (canInteract) {
                    Row(
                        modifier = Modifier.padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        TextButton(
                            onClick = { onReply(comment) }
                        ) {
                            Text(
                                text = "Reply",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }

                        TextButton(
                            onClick = { comment.id?.let { onDelete(it) } }
                        ) {
                            Text(
                                text = "Delete",
                                fontSize = 12.sp,
                                color = Color.Red
                            )
                        }
                    }
                }

                // Responses
                if (comment.responses.isNotEmpty()) {
                    Column(
                        modifier = Modifier
                            .padding(start = 16.dp, top = 8.dp)
                    ) {
                        comment.responses.forEach { response ->
                            CommentItem(
                                comment = response,
                                userName = userName,
                                onReply = onReply,
                                onDelete = onDelete,
                                canInteract = canInteract
                            )
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun formatTimeAgo(createdAt: String?): String {
    if (createdAt == null) return "Unknown time"

    try {
        val formatter = DateTimeFormatter.ISO_DATE_TIME
        val commentTime = LocalDateTime.parse(createdAt, formatter)
        val now = LocalDateTime.now(ZoneId.of("Europe/Lisbon"))
        val duration = Duration.between(commentTime, now)

        return when {
            duration.toMinutes() < 5 -> "Agora mesmo"
            duration.toHours() < 1 -> "à ${duration.toMinutes()} min"
            duration.toDays() < 1 -> "à ${duration.toHours()}h"
            duration.toDays() == 1L -> "Ontem"
            duration.toDays() < 30 -> "à ${duration.toDays()}d"
            duration.toDays() < 365 -> "à ${duration.toDays() / 30} meses"
            else -> "à ${duration.toDays() / 365} anos"
        }
    } catch (e: Exception) {
        return createdAt // Return the original string if parsing fails
    }
}

data class MaterialDetail(
    val title: String,
    val author: String,
    val language: String,
    val rating: Float,
    val description: String,
    val tags: List<String>
)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MaterialInfoSection(
    material: Material,
    authorName: String?
) {
    val dateFormatter = DateTimeFormatterBuilder()
        .appendPattern("dd/MM/yyyy HH:mm")
        .toFormatter(Locale.getDefault())

    // Language mapping
    val languageInfo = when (material.language) {
        1L -> Pair("🇵🇹", "Portuguese")
        2L -> Pair("🇬🇧", "English")
        else -> Pair("🌐", "Unknown")
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Title
        Text(
            text = "Title",
            fontSize = 12.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = material.title,
            fontSize = 16.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 2.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Author and Language Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Author",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(Color(0xFF395174), CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = authorName ?: "Unknown",
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                }
            }

            Column {
                Text(
                    text = "Content language",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Text(
                        text = languageInfo.first,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = languageInfo.second,
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Description
        if (!material.description.isNullOrEmpty()) {
            Text(
                text = "Description",
                fontSize = 12.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = material.description,
                fontSize = 14.sp,
                color = Color.Black,
                modifier = Modifier.padding(top = 2.dp),
                lineHeight = 20.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Created At
        if (!material.created_at.isNullOrEmpty()) {
            Text(
                text = "Created",
                fontSize = 12.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = try {
                    LocalDateTime.parse(material.created_at, DateTimeFormatter.ISO_DATE_TIME)
                        .format(dateFormatter)
                } catch (e: Exception) {
                    material.created_at
                },
                fontSize = 14.sp,
                color = Color.Black,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Composable
fun ActionButtonsSection(
    material: Material,
    context: Context
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Add to Favourites Button
        OutlinedButton(
            onClick = { },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color(0xFF395174)
            )
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Add to Favourites",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        // Download and Preview Buttons in a Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Download Button
            if (!material.file_url.isNullOrEmpty()) {
                Button(
                    onClick = {
                        val fileName = getFileNameFromUrl(material.file_url, material.title)
                        downloadFile(context, material.file_url, fileName)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF395174)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Download",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }

                // Preview Button
                Button(
                    onClick = {
                        previewFile(context, material.file_url)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF395174)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Preview",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun TagsSection(tags: List<String>) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Tags",
            fontSize = 14.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (tags.isNotEmpty()) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.wrapContentHeight()
            ) {
                tags.forEach { tag ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF4A90E2),
                        modifier = Modifier.padding(vertical = 2.dp)
                    ) {
                        Text(
                            text = tag,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        } else {
            Text(
                text = "No tags available",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun CommentInputSection(
    commentText: String,
    onCommentChange: (String) -> Unit,
    onSendComment: () -> Unit,
    replyingTo: Comments? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(Color(0xFF395174), CircleShape)
        )

        // Comment input
        Column(modifier = Modifier.weight(1f)) {
            OutlinedTextField(
                value = commentText,
                onValueChange = onCommentChange,
                placeholder = {
                    Text(
                        text = if (replyingTo != null) "Write a reply..." else "Leave a comment",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF395174),
                    unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
                ),
                minLines = 2
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
            ) {
                if (replyingTo != null) {
                    TextButton(onClick = { onCommentChange("") }) {
                        Text(
                            text = "Cancel",
                            color = Color.Gray
                        )
                    }
                }

                Button(
                    onClick = onSendComment,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF395174)
                    ),
                    shape = RoundedCornerShape(6.dp),
                    enabled = commentText.isNotBlank()
                ) {
                    Text(
                        text = if (replyingTo != null) "Reply" else "Comment",
                        color = Color.White
                    )
                }
            }
        }
    }
}