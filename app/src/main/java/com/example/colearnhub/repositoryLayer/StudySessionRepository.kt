package com.example.colearnhub.repositoryLayer

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.colearnhub.modelLayer.CreateStudySessionRequest
import com.example.colearnhub.modelLayer.StudySession
import com.example.colearnhub.modelLayer.StudySessionInsert
import com.example.colearnhub.modelLayer.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class StudySessionRepository {
    /**
     * Creates a new study session
     */
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun createStudySession(request: CreateStudySessionRequest, creatorId: String): StudySession? = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.d("StudySessionRepository", "Creating study session: ${request.description}")

            // Converter a data de DD/MM/YYYY para YYYY-MM-DD
            val inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val date = LocalDate.parse(request.date, inputFormatter).format(outputFormatter)
            val insertData = StudySessionInsert(
                name = request.name,
                description = request.description.toString(),
                creator_id = creatorId,
                group_id = request.group_id,
                visibility = request.visibility,
                date = date,
                startTime = request.startTime,
                duration = request.duration,
                tag = request.tag
            )

            val session = SupabaseClient.client
                .from("Study_sessions")
                .insert(insertData) {
                    select()
                }
                .decodeSingle<StudySession>()

            session
        } catch (e: Exception) {
            Log.e("StudySessionRepository", "Error creating study session: ${e.message}")
            Log.e("StudySessionRepository", "Stack trace: ${e.stackTraceToString()}")
            null
        }
    }

    /**
     * Gets the current user's ID
     */
    private suspend fun getCurrentUserId(): String? = withContext(Dispatchers.IO) {
        return@withContext try {
            SupabaseClient.client.auth.currentUserOrNull()?.id
        } catch (e: Exception) {
            Log.e("StudySessionRepository", "Error getting current user ID: ${e.message}")
            null
        }
    }
} 