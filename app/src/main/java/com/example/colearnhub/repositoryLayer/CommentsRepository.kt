package com.example.colearnhub.repositoryLayer

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.colearnhub.modelLayer.Comments
import com.example.colearnhub.modelLayer.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlinx.serialization.Serializable

@Serializable
data class CreateCommentRequest(
    val user_id: String,
    val material_id: Int,
    val content: String,
    val created_at: String,
    val response: Int? = null
)

class CommentsRepository {
    @RequiresApi(Build.VERSION_CODES.O)
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
        .withZone(ZoneId.of("Europe/Lisbon"))

    /**
     * Creates a new comment
     */
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun createComment(
        userId: String,
        materialId: String,
        content: String,
        responseTo: Int? = null
    ): Comments? = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.d("CommentsRepository", "Creating comment for material: $materialId")

            // If this is a reply, get the original comment to determine if it's a reply to a reply
            val originalComment = if (responseTo != null) {
                getCommentById(responseTo)
            } else null

            val commentRequest = CreateCommentRequest(
                user_id = userId,
                material_id = materialId.toInt(),
                content = content,
                created_at = LocalDateTime.now(ZoneId.of("Europe/Lisbon")).format(dateFormatter),
                response = responseTo
            )

            val result = SupabaseClient.client
                .from("Comments")
                .insert(commentRequest) {
                    select()
                }
                .decodeSingle<Comments>()

            Log.d("CommentsRepository", "Comment created successfully: ID ${result.id}")
            result
        } catch (e: Exception) {
            Log.e("CommentsRepository", "Error creating comment: ${e.message}", e)
            null
        }
    }

    /**
     * Gets all comments for a material with their responses
     */
    suspend fun getCommentsForMaterial(materialId: String): List<Comments> = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.d("CommentsRepository", "Fetching comments for material: $materialId")

            val allComments = SupabaseClient.client
                .from("Comments")
                .select {
                    filter {
                        eq("material_id", materialId.toLong())
                    }
                }
                .decodeList<Comments>()

            // Create a map to store comments by their ID, to easily find parents
            // Initialize each comment with a mutable list for responses
            val commentMap = allComments.associateBy { it.id!! }
                .mapValues { (_, comment) -> comment.copy(responses = mutableListOf()) }
                .toMutableMap()

            val topLevelComments = mutableListOf<Comments>()

            // Sort comments by creation date to ensure parents are processed before their children
            val sortedComments = allComments.sortedBy { it.created_at }

            // Build the hierarchy
            for (comment in sortedComments) {
                val currentComment = commentMap[comment.id!!]!!
                if (comment.response == null) {
                    // This is a top-level comment
                    topLevelComments.add(currentComment)
                } else {
                    // This is a reply, find its parent
                    val parent = commentMap[comment.response]
                    if (parent != null) {
                        // Add the current comment to the parent's responses list
                        (parent.responses as MutableList).add(currentComment)
                    } else {
                        // If parent not found (e.g., parent was deleted), treat as top-level
                        Log.w("CommentsRepository", "Parent comment ${comment.response} not found for reply ${comment.id}. Treating as top-level.")
                        topLevelComments.add(currentComment)
                    }
                }
            }

            // Recursively sort comments and their responses by creation time (newest first)
            fun sortCommentsRecursively(comments: List<Comments>): List<Comments> {
                return comments.sortedByDescending { it.created_at }.map { 
                    it.copy(responses = sortCommentsRecursively(it.responses))
                }
            }

            val finalComments = sortCommentsRecursively(topLevelComments)

            Log.d("CommentsRepository", "Found ${finalComments.size} top-level comments with nested replies")
            finalComments
        } catch (e: Exception) {
            Log.e("CommentsRepository", "Error fetching comments: ${e.message}", e)
            emptyList()
        }
    }

    /**
     * Gets all responses for a specific comment (alternative method if needed)
     */
    private suspend fun getCommentResponses(commentId: Int): List<Comments> = withContext(Dispatchers.IO) {
        return@withContext try {
            val responses = SupabaseClient.client
                .from("Comments")
                .select {
                    filter {
                        eq("response", commentId)
                    }
                }
                .decodeList<Comments>()
                .sortedBy { it.created_at } // Sort responses chronologically

            Log.d("CommentsRepository", "Found ${responses.size} responses for comment $commentId")
            responses
        } catch (e: Exception) {
            Log.e("CommentsRepository", "Error fetching comment responses: ${e.message}", e)
            emptyList()
        }
    }

    /**
     * Gets a single comment by ID
     */
    suspend fun getCommentById(commentId: Int): Comments? = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.d("CommentsRepository", "Fetching comment: $commentId")

            SupabaseClient.client
                .from("Comments")
                .select {
                    filter {
                        eq("id", commentId)
                    }
                }
                .decodeSingle<Comments>()
        } catch (e: Exception) {
            Log.e("CommentsRepository", "Error fetching comment: ${e.message}", e)
            null
        }
    }

    /**
     * Updates a comment's content
     */
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun updateComment(
        commentId: Int,
        newContent: String
    ): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.d("CommentsRepository", "Updating comment: $commentId")

            SupabaseClient.client
                .from("Comments")
                .update({
                    set("content", newContent)
                }) {
                    filter {
                        eq("id", commentId)
                    }
                }

            Log.d("CommentsRepository", "Comment updated successfully")
            true
        } catch (e: Exception) {
            Log.e("CommentsRepository", "Error updating comment: ${e.message}", e)
            false
        }
    }

    /**
     * Deletes a comment and all its responses
     */
    suspend fun deleteComment(commentId: Int): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.d("CommentsRepository", "Deleting comment: $commentId")

            // First, delete all responses to this comment (cascading delete)
            // The database should handle this automatically with ON DELETE CASCADE
            // But we'll do it manually to be safe
            val responses = getCommentResponses(commentId)

            // Delete all responses first
            responses.forEach { response ->
                response.id?.let { responseId ->
                    try {
                        SupabaseClient.client
                            .from("Comments")
                            .delete {
                                filter {
                                    eq("id", responseId)
                                }
                            }
                    } catch (e: Exception) {
                        Log.w("CommentsRepository", "Error deleting response $responseId: ${e.message}")
                    }
                }
            }

            // Then delete the main comment
            SupabaseClient.client
                .from("Comments")
                .delete {
                    filter {
                        eq("id", commentId)
                    }
                }

            Log.d("CommentsRepository", "Comment and ${responses.size} responses deleted successfully")
            true
        } catch (e: Exception) {
            Log.e("CommentsRepository", "Error deleting comment: ${e.message}", e)
            false
        }
    }

    /**
     * Gets comments count for a material
     */
    suspend fun getCommentsCount(materialId: String): Int = withContext(Dispatchers.IO) {
        return@withContext try {
            val comments = SupabaseClient.client
                .from("Comments")
                .select {
                    filter {
                        eq("material_id", materialId.toLong())
                    }
                }
                .decodeList<Comments>()

            comments.size
        } catch (e: Exception) {
            Log.e("CommentsRepository", "Error fetching comments count: ${e.message}", e)
            0
        }
    }

    /**
     * Gets comments by user
     */
    suspend fun getCommentsByUser(userId: String): List<Comments> = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.d("CommentsRepository", "Fetching comments by user: $userId")

            SupabaseClient.client
                .from("Comments")
                .select {
                    filter {
                        eq("user_id", userId)
                    }
                }
                .decodeList<Comments>()
                .sortedByDescending { it.created_at }
        } catch (e: Exception) {
            Log.e("CommentsRepository", "Error fetching user comments: ${e.message}", e)
            emptyList()
        }
    }
}