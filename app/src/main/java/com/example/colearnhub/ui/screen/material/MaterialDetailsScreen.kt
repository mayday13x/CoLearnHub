package com.example.colearnhub.ui.screen.main

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.colearnhub.R
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
import com.example.colearnhub.repositoryLayer.RatingRepository
import com.example.colearnhub.modelLayer.Rating
import com.example.colearnhub.repositoryLayer.FavouritesRepository
import com.example.colearnhub.ui.utils.logoSize
import com.example.colearnhub.ui.utils.txtSize


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

    val ratingRepository = remember { RatingRepository() }
    var currentUserRating by remember { mutableStateOf<Rating?>(null) }
    var averageRating by remember { mutableStateOf(0.0) }
    var totalRatings by remember { mutableStateOf(0) }
    var showRatingDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmationDialog by remember { mutableStateOf<Comments?>(null) }

    val currentUser by authViewModel.currentUser.collectAsState()
    val currentUserId = currentUser?.id

    val materialsRepository = remember { MaterialsRepository() }
    val commentsRepository = remember { CommentsRepository() }
    val userRepository = remember { UserRepository() }
    val scope = rememberCoroutineScope()

    var material by remember { mutableStateOf<Material?>(null) }
    var comments by remember { mutableStateOf<List<Comments>>(emptyList()) }
    var commentText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var authorName by remember { mutableStateOf<String?>(null) }
    var replyingTo by remember { mutableStateOf<Comments?>(null) }
    var userNames by remember { mutableStateOf<Map<String, String>>(emptyMap()) }

    val favouritesRepository = remember { FavouritesRepository() }
    var isFavourite by remember { mutableStateOf(false) }

    // Helper function to count all comments recursively
    fun getTotalCommentCount(commentsList: List<Comments>): Int {
        var count = 0
        for (comment in commentsList) {
            count++
            count += getTotalCommentCount(comment.responses)
        }
        return count
    }

    val totalCommentsCount = remember(comments) {
        getTotalCommentCount(comments)
    }

    // Handle back navigation
    BackHandler {
        if (replyingTo != null) {
            replyingTo = null
        } else {
            // Navigate back to MainScreen with default selected item (0)
            navController.navigate("MainScreen?selectedItem=0") {
                popUpTo("MainScreen") { inclusive = true }
            }
        }
    }

    // Load material, comments and author data
    LaunchedEffect(materialId, currentUserId) {
        try {
            isLoading = true
            material = materialsRepository.getMaterialByIdWithTags(materialId)

            // Check if material is in favorites
            currentUserId?.let { userId ->
                isFavourite = favouritesRepository.isFavourite(userId, materialId.toLong())
            }

            // Fetch author name
            material?.author_id?.let { authorId ->
                userRepository.getUserById(authorId)?.let { user ->
                    authorName = user.name
                }
            }

            // Fetch comments
            comments = commentsRepository.getCommentsForMaterial(materialId)

            // Fetch user names for all comments and replies
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

            // Fetch rating data if user is logged in
            currentUserId?.let { userId ->
                try {
                    // Get current user's rating
                    currentUserRating = ratingRepository.getRating(userId, materialId.toLong())

                    // Get average rating and total ratings
                    val allRatings = ratingRepository.getMaterialRatings(materialId.toLong())
                    totalRatings = allRatings.size
                    averageRating = if (allRatings.isNotEmpty()) {
                        allRatings.mapNotNull { it.rating }.average()
                    } else {
                        0.0
                    }
                } catch (e: Exception) {
                    Log.e("MaterialDetailsScreen", "Erro ao carregar ratings: ${e.message}")
                }
            }

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
            .background(Color.White),
    ) {
        // Header
        TopAppBar(
            title = {
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .padding(end = logoSize() - 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.material_details_title),
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        fontSize = (txtSize().value + 4).sp,
                        maxLines = 1
                    )
                }
            },
            navigationIcon = {
                IconButton(onClick = {
                    scope.launch {
                        replyingTo = null
                        //navController.popBackStack("MainScreen", false)
                        navController.popBackStack()
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
                            authorName = authorName,
                            averageRating = averageRating,
                            totalRatings = totalRatings,
                            currentUserRating = currentUserRating,
                            onRateClick = { showRatingDialog = true },
                            canRate = currentUserId != null
                        )
                    }

                    // Action Buttons
                    item {
                        val activityContext = LocalContext.current
                        ActionButtonsSection(
                            material = material,
                            context = activityContext,
                            isFavourite = isFavourite,
                            onFavouriteClick = {
                                scope.launch {
                                    if (currentUserId != null) {
                                        if (isFavourite) {
                                            favouritesRepository.removeFromFavourites(currentUserId, materialId.toLong())
                                        } else {
                                            favouritesRepository.addToFavourites(currentUserId, materialId.toLong())
                                        }
                                        isFavourite = !isFavourite
                                    } else {
                                        Toast.makeText(context, context.getText(R.string.dep1), Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        )
                    }

                    // Tags
                    item {
                        TagsSection(tags = material.tags?.map { it.description } ?: emptyList())
                    }

                    // Comments Header
                    item {
                        CommentsHeader(
                            commentCount = totalCommentsCount,
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
                                            commentText = ""
                                            replyingTo = null
                                        } catch (e: Exception) {
                                            error = e.message
                                        }
                                    }
                                },
                                replyingTo = replyingTo,
                                userNames = userNames,
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
                                    text = stringResource(R.string.log_com),
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
                                if (currentUserId != null) {
                                    replyingTo = it
                                }
                            },
                            onDelete = { commentToDelete ->
                                showDeleteConfirmationDialog = commentToDelete
                            },
                            canInteract = currentUserId != null,
                            userNames = userNames,
                            isAuthor = comment.user_id == currentUserId,
                            currentUserId = currentUserId,
                            depth = 0 // Top-level comments start at depth 0
                        )
                    }
                }
                // Rating Dialog
                if (showRatingDialog && currentUserId != null) {
                    RatingDialog(
                        showDialog = showRatingDialog,
                        currentRating = currentUserRating?.rating?.toInt() ?: 0,
                        onDismiss = { showRatingDialog = false },
                        onRatingSubmit = { rating ->
                            scope.launch {
                                if (currentUserId != null) {
                                    val success = ratingRepository.upsertRating(
                                        Rating(
                                            user_id = currentUserId,
                                            material_id = materialId.toLong(),
                                            rating = rating.toLong()
                                        )
                                    )
                                    if (success) {
                                        // Refresh ratings after submission
                                        currentUserRating = ratingRepository.getRating(currentUserId, materialId.toLong())
                                        val allRatings = ratingRepository.getMaterialRatings(materialId.toLong())
                                        totalRatings = allRatings.size
                                        averageRating = if (allRatings.isNotEmpty()) {
                                            allRatings.mapNotNull { it.rating }.average()
                                        } else {
                                            0.0
                                        }
                                        Toast.makeText(context, "Rating submitted!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Failed to submit rating.", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    Toast.makeText(context, "You must be logged in to rate.", Toast.LENGTH_SHORT).show()
                                }
                                showRatingDialog = false
                            }
                        }
                    )
                }
                // Delete Confirmation Dialog
                showDeleteConfirmationDialog?.let { comment ->
                    AlertDialog(
                        onDismissRequest = { showDeleteConfirmationDialog = null },
                        title = {
                            Text(
                                text = stringResource(R.string.delete_comment),
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF395174)
                            )
                        },
                        text = {
                            Text(
                                text = stringResource(R.string.confirm_delete_comment),
                                fontSize = 14.sp,
                                color = Color.Black
                            )
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    scope.launch {
                                        try {
                                            comment.id?.let { commentId ->
                                                commentsRepository.deleteComment(commentId)
                                                // Refresh comments
                                                comments = commentsRepository.getCommentsForMaterial(materialId)
                                            }
                                        } catch (e: Exception) {
                                            error = e.message
                                        }
                                        showDeleteConfirmationDialog = null
                                    }
                                }
                            ) {
                                Text(
                                    text = stringResource(R.string.delete),
                                    color = Color.Red
                                )
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDeleteConfirmationDialog = null }) {
                                Text(
                                    text = stringResource(R.string.cancel),
                                    color = Color(0xFF395174)
                                )
                            }
                        },
                        containerColor = Color.White
                    )
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
                text = stringResource(R.string.comments) + " | $commentCount",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Text(
                text = stringResource(R.string.sort) + " ⚡",
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
                    text = stringResource(R.string.resp) + (userName ?: stringResource(R.string.desc)),
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                TextButton(
                    onClick = onCancelReply,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.cancel),
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
    onDelete: (Comments) -> Unit,
    canInteract: Boolean = true,
    userNames: Map<String, String>,
    isAuthor: Boolean = false,
    currentUserId: String?,
    depth: Int = 0 // New parameter for indentation depth
) {
    var isExpanded by remember(comment) { mutableStateOf(depth == 0) } // Top-level expanded, replies collapsed

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .then(if (depth > 0) Modifier.padding(start = (12 * depth).dp) else Modifier), // Dynamic padding
        horizontalArrangement = Arrangement.spacedBy(4.dp) // Adjusted spacing
    ) {
        // Vertical line for replies
        if (depth > 0) {
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .fillMaxHeight()
                    .background(Color.LightGray)
                    .align(Alignment.CenterVertically)
            )
        }

        // Avatar
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(Color(0xFF395174), CircleShape)
        )

        Column(modifier = Modifier.weight(1f)) {
            // Author, time, and toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = userName ?: stringResource(R.string.desc),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = formatTimeAgo(comment.created_at, LocalContext.current),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                // Only show Collapse/Expand button if it's a reply OR if it's a top-level comment with responses
                if (depth > 0 || comment.responses.isNotEmpty()) {
                    TextButton(onClick = { isExpanded = !isExpanded }) {
                        Text(
                            text = if (isExpanded) stringResource(R.string.collapse) else stringResource(R.string.expand),
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            // Comment content and actions - only show if expanded
            if (isExpanded) {
                if (!comment.content.isNullOrEmpty()) {
                    Text(
                        text = comment.content,
                        fontSize = 14.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(top = 4.dp),
                        lineHeight = 18.sp
                    )
                }

                if (canInteract) {
                    Row(
                        modifier = Modifier.padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        TextButton(
                            onClick = { onReply(comment) }
                        ) {
                            Text(
                                text = stringResource(R.string.response),
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }

                        if (isAuthor) {
                            TextButton(
                                onClick = { onDelete(comment) }
                            ) {
                                Text(
                                    text = stringResource(R.string.delete),
                                    fontSize = 12.sp,
                                    color = Color.Red
                                )
                            }
                        }
                    }
                }

                // Responses - only show if expanded
                if (comment.responses.isNotEmpty()) {
                    Column(
                        modifier = Modifier
                            .padding(top = 8.dp) // Removed start padding here, now handled by the Row's Modifier
                    ) {
                        comment.responses.forEach { response ->
                            CommentItem(
                                comment = response,
                                userName = userNames[response.user_id],
                                onReply = onReply,
                                onDelete = onDelete,
                                canInteract = canInteract,
                                userNames = userNames,
                                isAuthor = response.user_id == currentUserId,
                                currentUserId = currentUserId,
                                depth = depth + 1 // Increment depth for nested replies
                            )
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun formatTimeAgo(createdAt: String?, context: Context): String {
    if (createdAt == null) return context.getString(R.string.unknown_time)

    try {
        val formatter = DateTimeFormatter.ISO_DATE_TIME
        val commentTime = LocalDateTime.parse(createdAt, formatter)
        val now = LocalDateTime.now(ZoneId.of("Europe/Lisbon"))
        val duration = Duration.between(commentTime, now)

        return when {
            duration.toMinutes() < 5 -> context.getString(R.string.just_now)
            duration.toHours() < 1 -> context.getString(R.string.minutes_ago, duration.toMinutes().toInt())
            duration.toDays() < 1 -> context.getString(R.string.hours_ago, duration.toHours().toInt())
            duration.toDays() == 1L -> context.getString(R.string.yesterday)
            duration.toDays() < 30 -> context.getString(R.string.days_ago, duration.toDays().toInt())
            duration.toDays() < 365 -> context.getString(R.string.months_ago, (duration.toDays() / 30).toInt())
            else -> context.getString(R.string.years_ago, (duration.toDays() / 365).toInt())
        }
    } catch (e: Exception) {
        return context.getString(R.string.invalid_date) // Return a generic invalid date string if parsing fails
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
    authorName: String?,
    averageRating: Double = 0.0,
    totalRatings: Int = 0,
    currentUserRating: Rating? = null,
    onRateClick: () -> Unit = {},
    canRate: Boolean = true
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
            text = stringResource(R.string.Title_label),
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
                    text = stringResource(R.string.author),
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
                        text = authorName ?: stringResource(R.string.desc2),
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                }
            }

            Column {
                Text(
                    text = stringResource(R.string.language),
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
                text = stringResource(R.string.description),
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

        // Rating Section
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (totalRatings == 0) {
                Text(
                    text = stringResource(R.string.no_ratings_yet),
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
                )
            } else {
                // Star and Rating
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "⭐",
                        fontSize = 24.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = String.format("%.1f", averageRating),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                Text(
                    text = stringResource(R.string.average_rating),
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            // Rate Now Button
            if (canRate) {
                TextButton(
                    onClick = onRateClick,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        text = if (currentUserRating != null) stringResource(R.string.update_rating) else stringResource(R.string.rate_now),
                        fontSize = 14.sp,
                        color = Color(0xFF395174),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Show current user rating if exists
            if (currentUserRating != null && totalRatings > 0) {
                Text(
                    text = stringResource(R.string.your_rating) + " ${currentUserRating.rating} ⭐",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Created At
        if (!material.created_at.isNullOrEmpty()) {
            Text(
                text = stringResource(R.string.created3),
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
fun RatingDialog(
    showDialog: Boolean,
    currentRating: Int = 0,
    onDismiss: () -> Unit,
    onRatingSubmit: (Int) -> Unit
)   {
    var selectedRating by remember(showDialog) { mutableStateOf(currentRating) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = stringResource(R.string.rate_this),
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF395174)
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.how_rates),
                        fontSize = 14.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Star Rating
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        for (i in 1..5) {
                            IconButton(
                                onClick = { selectedRating = i }
                            ) {
                                Text(
                                    text = if (i <= selectedRating) "⭐" else "☆",
                                    fontSize = 32.sp,
                                    color = if (i <= selectedRating) Color(0xFFFFD700) else Color.Black
                                )
                            }
                        }
                    }

                    if (selectedRating > 0) {
                        Text(
                            text = when (selectedRating) {
                                1 -> stringResource(R.string.poor)
                                2 -> stringResource(R.string.bad)
                                3 -> stringResource(R.string.average)
                                4 -> stringResource(R.string.good)
                                5 -> stringResource(R.string.excellent)
                                else -> ""
                            },
                            fontSize = 14.sp,
                            color = Color(0xFF395174),
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (selectedRating > 0) {
                            onRatingSubmit(selectedRating)
                        }
                    },
                    enabled = selectedRating > 0
                ) {
                    Text(
                        text = stringResource(R.string.submit),
                        color = if (selectedRating > 0) Color(0xFF395174) else Color.Gray
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(
                        text = stringResource(R.string.cancel),
                        color = Color(0xFFFF0000)
                    )
                }
            },
            containerColor = Color.White
        )
    }
}

@Composable
fun ActionButtonsSection(
    material: Material,
    context: Context,
    isFavourite: Boolean = false,
    onFavouriteClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Add to Favourites Button
        OutlinedButton(
            onClick = onFavouriteClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = if (isFavourite) Color.Red else Color(0xFF395174)
            )
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = if (isFavourite) Color.Red else Color(0xFF395174)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isFavourite) stringResource(R.string.remove_fav) else stringResource(R.string.add_fav),
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
                        text = stringResource(R.string.download),
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
                        text = stringResource(R.string.preview),
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
            text = stringResource(R.string.tag),
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
                text = stringResource(R.string.no_tags),
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
    replyingTo: Comments? = null,
    userNames: Map<String, String>
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
                        text = if (replyingTo != null) {
                            val replyToName = userNames[replyingTo.user_id] ?: stringResource(R.string.desc)
                            stringResource(R.string.resp2) + "${replyToName}..."
                        } else stringResource(R.string.leave_comment),
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
                textStyle = TextStyle(color = Color.Black),
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
                            text = stringResource(R.string.clean),
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
                        text = if (replyingTo != null) stringResource(R.string.response) else stringResource(R.string.comment),
                        color = Color.White
                    )
                }
            }
        }
    }
}