package com.example.colearnhub.ui.screen.main

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

data class Comment(
    val id: String,
    val authorName: String,
    val content: String,
    val timeAgo: String,
    val likes: Int,
    val isAuthor: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaterialDetailsScreen(
    navController: NavController,
    materialId: String = "1" // Default para teste
) {
    // Dados estáticos para demonstração
    val material = remember {
        MaterialDetail(
            title = "Notes - Linear Algebra",
            author = "michelangelo",
            language = "English",
            rating = 5.0f,
            description = "Study notes for the Linear Algebra exam next week. These notes are provided by João's resolution of esquema. 📚 ✨",
            tags = listOf("Notes", "ISEP")
        )
    }

    val comments = remember {
        listOf(
            Comment("1", "michelangelo", "", "4 hours ago", 0, true),
            Comment("2", "michelangelo", "", "4 hours ago", 0, true),
            Comment("3", "June", "Very helpful content ! Thank you for sharing", "1 hour ago", 7),
            Comment("4", "michelangelo", "Thank you!", "1 hour ago", 0, true)
        )
    }

    var commentText by remember { mutableStateOf("") }

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
                IconButton(onClick = { navController.popBackStack() }) {
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

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Material Info
            item {
                MaterialInfoSection(material = material)
            }

            // Action Buttons
            item {
                ActionButtonsSection()
            }

            // Tags
            item {
                TagsSection(tags = material.tags)
            }

            // Comments Header
            item {
                CommentsHeader(commentCount = comments.size)
            }

            // Comment Input
            item {
                CommentInputSection(
                    commentText = commentText,
                    onCommentChange = { commentText = it },
                    onSendComment = {
                        // Handle send comment
                        commentText = ""
                    }
                )
            }

            // Comments List
            items(comments) { comment ->
                CommentItem(comment = comment)
            }
        }
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

@Composable
fun MaterialInfoSection(material: MaterialDetail) {
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
                        text = material.author,
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
                        text = "🇬🇧", // English flag
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = material.language,
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Description
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

        Spacer(modifier = Modifier.height(16.dp))

        // Rating
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(5) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFFFA500),
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = material.rating.toString(),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        Text(
            text = "Average Rating",
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 2.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Rate Now",
            fontSize = 14.sp,
            color = Color(0xFF395174),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun ActionButtonsSection() {
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
        Button(
            onClick = { },
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

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
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
    }
}

@Composable
fun CommentsHeader(commentCount: Int) {
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

@Composable
fun CommentItem(comment: Comment) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(
                    if (comment.isAuthor) Color(0xFF395174) else Color(0xFF50C878),
                    CircleShape
                )
        )

        Column(modifier = Modifier.weight(1f)) {
            // Author and time
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = comment.authorName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = comment.timeAgo,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            // Comment content (if not empty)
            if (comment.content.isNotEmpty()) {
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
                Text(
                    text = "Reply",
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                if (comment.likes > 0) {
                    Text(
                        text = "👍 ${comment.likes}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                } else {
                    Text(
                        text = "👍",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Text(
                    text = "👎",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}