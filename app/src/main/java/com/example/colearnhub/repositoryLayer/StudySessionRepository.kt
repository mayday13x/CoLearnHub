package com.example.colearnhub.repositoryLayer

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.colearnhub.modelLayer.CreateStudySessionRequest
import com.example.colearnhub.modelLayer.SessionParticipant
import com.example.colearnhub.modelLayer.StudySession
import com.example.colearnhub.modelLayer.StudySessionInsert
import com.example.colearnhub.modelLayer.SupabaseClient
import com.example.colearnhub.modelLayer.SessionParticipantInsert
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
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
                tag = request.tag,
                session_link = request.session_link
            )

            val session = SupabaseClient.client
                .from("Study_sessions")
                .insert(insertData) {
                    select(columns = Columns.raw("*, Tags(*)"))
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
     * Gets all future study sessions (from today onwards)
     */
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getFutureStudySessions(): List<StudySession> = withContext(Dispatchers.IO) {
        return@withContext try {
            val today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

            SupabaseClient.client
                .from("Study_sessions")
                .select(
                    columns = Columns.raw("*, Tags(*)")
                ) {
                    filter {
                        gte("date", today)
                    }
                    order("date", Order.ASCENDING)
                    order("start_time", Order.ASCENDING)
                }
                .decodeList<StudySession>()
        } catch (e: Exception) {
            Log.e("StudySessionRepository", "Error fetching future study sessions: ${e.message}")
            Log.e("StudySessionRepository", "Stack trace: ${e.stackTraceToString()}")
            emptyList()
        }
    }

    /**
     * Gets study sessions that the user has joined (from today onwards)
     */
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getJoinedStudySessions(userId: String): List<StudySession> = withContext(Dispatchers.IO) {
        return@withContext try {
            val today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

            // This assumes you have a junction table called "Session_participants"
            // or similar to track which users have joined which sessions
            // You might need to adjust this query based on your database structure
            SupabaseClient.client
                .from("Study_sessions")
                .select(
                    columns = Columns.raw("*, Session_participants!inner(*), Tags(*)")
                ) {
                    filter {
                        eq("Session_participants.user_id", userId)
                        gte("date", today)
                    }
                    order("date", Order.ASCENDING)
                    order("start_time", Order.ASCENDING)
                }
                .decodeList<StudySession>()
        } catch (e: Exception) {
            Log.e("StudySessionRepository", "Error fetching joined study sessions: ${e.message}")
            Log.e("StudySessionRepository", "Stack trace: ${e.stackTraceToString()}")
            emptyList()
        }
    }

    /**
     * Gets study sessions created by the user (from today onwards)
     */
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getCreatedStudySessions(userId: String): List<StudySession> = withContext(Dispatchers.IO) {
        return@withContext try {
            val today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

            SupabaseClient.client
                .from("Study_sessions")
                .select(
                    columns = Columns.raw("*, Tags(*)")
                ) {
                    filter {
                        eq("creator_id", userId)
                        gte("date", today)
                    }
                    order("date", Order.ASCENDING)
                    order("start_time", Order.ASCENDING)
                }
                .decodeList<StudySession>()
        } catch (e: Exception) {
            Log.e("StudySessionRepository", "Error fetching created study sessions: ${e.message}")
            Log.e("StudySessionRepository", "Stack trace: ${e.stackTraceToString()}")
            emptyList()
        }
    }

    /**
     * Gets the details of a single study session by ID, including participant count
     */
    suspend fun getStudySessionDetails(sessionId: String, currentUserId: String?): StudySession? = withContext(Dispatchers.IO) {
        return@withContext try {
            val session = SupabaseClient.client
                .from("Study_sessions")
                .select(
                    columns = Columns.raw("*, Tags(*), Session_participants(user_id, session_id), creator_id")
                ) {
                    filter {
                        eq("id", sessionId.toLong())
                    }
                }
                .decodeSingleOrNull<StudySession>()

            session?.copy(numParticipants = session.sessionParticipants?.size ?: 0)
        } catch (e: Exception) {
            Log.e("StudySessionRepository", "Error fetching study session details: ${e.message}")
            Log.e("StudySessionRepository", "Stack trace: ${e.stackTraceToString()}")
            null
        }
    }

    /**
     * Checks if a user has joined a specific study session
     */
    suspend fun isUserJoined(sessionId: String, userId: String): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            val participants = SupabaseClient.client
                .from("Session_participants")
                .select {
                    filter {
                        eq("session_id", sessionId.toLong())
                        eq("user_id", userId)
                    }
                }
                .decodeList<SessionParticipant>()
            participants.isNotEmpty()
        } catch (e: Exception) {
            Log.e("StudySessionRepository", "Error checking if user joined session: ${e.message}")
            false
        }
    }

    /**
     * Allows a user to join a study session
     */
    suspend fun joinStudySession(sessionId: Long, userId: String): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            val participantInsert = SessionParticipantInsert(userId = userId, sessionId = sessionId)
            SupabaseClient.client
                .from("Session_participants")
                .insert(participantInsert)
            true
        } catch (e: Exception) {
            Log.e("StudySessionRepository", "Error joining session: ${e.message}")
            Log.e("StudySessionRepository", "Stack trace: ${e.stackTraceToString()}")
            false
        }
    }

    /**
     * Allows a user to leave a study session
     */
    suspend fun leaveStudySession(sessionId: Long, userId: String): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            SupabaseClient.client
                .from("Session_participants")
                .delete {
                    filter {
                        eq("session_id", sessionId)
                        eq("user_id", userId)
                    }
                }
            true
        } catch (e: Exception) {
            Log.e("StudySessionRepository", "Error leaving session: ${e.message}")
            Log.e("StudySessionRepository", "Stack trace: ${e.stackTraceToString()}")
            false
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

    /**
     * Deletes a study session and its participants (cascading delete)
     */
    suspend fun deleteStudySession(sessionId: Long): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            // Delete participants first if your database doesn't automatically cascade
            SupabaseClient.client
                .from("Session_participants")
                .delete {
                    filter {
                        eq("session_id", sessionId)
                    }
                }
            
            // Then delete the study session
            SupabaseClient.client
                .from("Study_sessions")
                .delete {
                    filter {
                        eq("id", sessionId)
                    }
                }
            true
        } catch (e: Exception) {
            Log.e("StudySessionRepository", "Error deleting session: ${e.message}")
            Log.e("StudySessionRepository", "Stack trace: ${e.stackTraceToString()}")
            false
        }
    }
} 