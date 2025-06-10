package com.example.colearnhub.ui.screen.main

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.colearnhub.R
import com.example.colearnhub.modelLayer.Comments
import com.example.colearnhub.modelLayer.Material
import com.example.colearnhub.repositoryLayer.CommentsRepository
import com.example.colearnhub.repositoryLayer.MaterialsRepository
import com.example.colearnhub.repositoryLayer.UserRepository
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaterialDetailsScreen(
    navController: NavController,
    materialId: String = "1" // Default para teste
) {
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

    // Load material, comments and author data
    LaunchedEffect(materialId) {
        try {
            isLoading = true
            material = materialsRepository.getMaterialByIdWithTags(materialId.toLong().toString())
            
            // Fetch author name
            material?.author_id?.let { authorId ->
                userRepository.getUserById(authorId)?.let { user ->
                    authorName = user.name
                }
            }
            
            comments = commentsRepository.getCommentsForMaterial(materialId)
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
                    text = "Material Details",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = { 
                    if (replyingTo != null) {
                        replyingTo = null
                    } else {
                        navController.navigateUp()
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
                        ActionButtonsSection(
                            fileUrl = material.file_url,
                            onDownload = {
                                // TODO: Implement download functionality
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
                            commentCount = comments.size,
                            replyingTo = replyingTo,
                            onCancelReply = { replyingTo = null }
                        )
                    }

                    // Comment Input
                    item {
                        CommentInputSection(
                            commentText = commentText,
                            onCommentChange = { commentText = it },
                            onSendComment = {
                                scope.launch {
                                    try {
                                        commentsRepository.createComment(
                                            userId = "current_user_id", // TODO: Get from auth
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
                            }
                        )
                    }

                    // Comments List
                    items(comments) { comment ->
                        CommentItem(
                            comment = comment,
                            onReply = { replyingTo = it },
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
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CommentsHeader(
    commentCount: Int,
    replyingTo: Comments?,
    onCancelReply: () -> Unit
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
                    text = "Replying to: ${it.user_id}",
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
    onReply: (Comments) -> Unit,
    onDelete: (Int) -> Unit
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
                        text = comment.user_id ?: "Unknown",
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

                // Actions
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

                // Responses
                if (comment.responses.isNotEmpty()) {
                    Column(
                        modifier = Modifier
                            .padding(start = 16.dp, top = 8.dp)
                    ) {
                        comment.responses.forEach { response ->
                            CommentItem(
                                comment = response,
                                onReply = onReply,
                                onDelete = onDelete
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
    
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val commentTime = LocalDateTime.parse(createdAt, formatter)
    val now = LocalDateTime.now()
    val duration = Duration.between(commentTime, now)
    
    return when {
        duration.toMinutes() < 1 -> "Just now"
        duration.toHours() < 1 -> "${duration.toMinutes()} minutes ago"
        duration.toDays() < 1 -> "${duration.toHours()} hours ago"
        duration.toDays() < 7 -> "${duration.toDays()} days ago"
        else -> createdAt
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
    fileUrl: String?,
    onDownload: () -> Unit
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

        // Download Button
        if (!fileUrl.isNullOrEmpty()) {
            Button(
                onClick = onDownload,
                modifier = Modifier
                    .fillMaxWidth()
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
    onSendComment: () -> Unit
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
                        text = "Leave a comment",
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
                TextButton(onClick = { onCommentChange("") }) {
                    Text(
                        text = "Close",
                        color = Color.Gray
                    )
                }

                Button(
                    onClick = onSendComment,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF395174)
                    ),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "Comment",
                        color = Color.White
                    )
                }
            }
        }
    }
}