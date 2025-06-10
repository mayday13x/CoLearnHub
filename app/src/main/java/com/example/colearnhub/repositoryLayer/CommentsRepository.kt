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
import java.time.format.DateTimeFormatter
import java.util.Objects.isNull

class CommentsRepository {
    @RequiresApi(Build.VERSION_CODES.O)
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

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
            
            val comment = Comments(
                user_id = userId,
                material_id = materialId,
                content = content,
                created_at = LocalDateTime.now().format(dateFormatter),
                response = responseTo
            )

            val result = SupabaseClient.client
                .from("Comments")
                .insert(comment) {
                    select()
                }
                .decodeSingle<Comments>()

            Log.d("CommentsRepository", "Comment created successfully: ID ${result.id}")
            result
        } catch (e: Exception) {
            Log.e("CommentsRepository", "Error creating comment: ${e.message}")
            null
        }
    }

    /**
     * Gets all comments for a material
     */
    suspend fun getCommentsForMaterial(materialId: String): List<Comments> = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.d("CommentsRepository", "Fetching comments for material: $materialId")

            val comments = SupabaseClient.client
                .from("Comments")
                .select {
                    filter {
                        eq("material_id", materialId)
                        isNull("response") // Only get top-level comments
                    }
                }
                .decodeList<Comments>()

            // For each top-level comment, fetch its responses
            comments.map { comment ->
                val responses = getCommentResponses(comment.id!!)
                comment.copy(responses = responses)
            }
        } catch (e: Exception) {
            Log.e("CommentsRepository", "Error fetching comments: ${e.message}")
            emptyList()
        }
    }

    /**
     * Gets all responses for a specific comment
     */
    private suspend fun getCommentResponses(commentId: Int): List<Comments> = withContext(Dispatchers.IO) {
        return@withContext try {
            SupabaseClient.client
                .from("Comments")
                .select {
                    filter {
                        eq("response", commentId)
                    }
                }
                .decodeList<Comments>()
        } catch (e: Exception) {
            Log.e("CommentsRepository", "Error fetching comment responses: ${e.message}")
            emptyList()
        }
    }

    /**
     * Deletes a comment
     */
    suspend fun deleteComment(commentId: Int): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.d("CommentsRepository", "Deleting comment: $commentId")

            // First delete all responses to this comment
            SupabaseClient.client
                .from("Comments")
                .delete {
                    filter {
                        eq("response", commentId)
                    }
                }

            // Then delete the comment itself
            SupabaseClient.client
                .from("Comments")
                .delete {
                    filter {
                        eq("id", commentId)
                    }
                }

            Log.d("CommentsRepository", "Comment deleted successfully")
            true
        } catch (e: Exception) {
            Log.e("CommentsRepository", "Error deleting comment: ${e.message}")
            false
        }
    }
} 