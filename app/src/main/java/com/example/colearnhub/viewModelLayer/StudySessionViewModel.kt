package com.example.colearnhub.viewModelLayer

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.colearnhub.modelLayer.CreateStudySessionRequest
import com.example.colearnhub.modelLayer.GroupResponse
import com.example.colearnhub.modelLayer.StudySession
import com.example.colearnhub.repositoryLayer.AuthRepository
import com.example.colearnhub.repositoryLayer.GroupRepository
import com.example.colearnhub.repositoryLayer.StudySessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StudySessionViewModel : ViewModel() {
    private val studySessionRepository = StudySessionRepository()
    private val groupRepository = GroupRepository()
    private val authRepository = AuthRepository()

    private val _isCreating = MutableStateFlow(false)
    val isCreating: StateFlow<Boolean> = _isCreating.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _userGroups = MutableStateFlow<List<GroupResponse>>(emptyList())
    val userGroups: StateFlow<List<GroupResponse>> = _userGroups.asStateFlow()

    private val _createdSession = MutableStateFlow<StudySession?>(null)
    val createdSession: StateFlow<StudySession?> = _createdSession.asStateFlow()

    private val _futureStudySessions = MutableStateFlow<List<StudySession>>(emptyList())
    val futureStudySessions: StateFlow<List<StudySession>> = _futureStudySessions.asStateFlow()

    private val _joinedStudySessions = MutableStateFlow<List<StudySession>>(emptyList())
    val joinedStudySessions: StateFlow<List<StudySession>> = _joinedStudySessions.asStateFlow()

    private val _createdStudySessions = MutableStateFlow<List<StudySession>>(emptyList())
    val createdStudySessions: StateFlow<List<StudySession>> = _createdStudySessions.asStateFlow()

    private val _selectedStudySession = MutableStateFlow<StudySession?>(null)
    val selectedStudySession: StateFlow<StudySession?> = _selectedStudySession.asStateFlow()

    private val _isUserParticipating = MutableStateFlow(false)
    val isUserParticipating: StateFlow<Boolean> = _isUserParticipating.asStateFlow()

    private val _isLoadingDetails = MutableStateFlow(false)
    val isLoadingDetails: StateFlow<Boolean> = _isLoadingDetails.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadUserGroups()
    }

    private fun loadUserGroups() {
        viewModelScope.launch {
            try {
                val currentUser = authRepository.getCurrentUser()
                if (currentUser != null) {
                    _userGroups.value = groupRepository.getUserAcceptedGroups(currentUser.id)
                }
            } catch (e: Exception) {
                Log.e("StudySessionViewModel", "Error loading user groups: ${e.message}")
                _error.value = "Failed to load user groups"
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadFutureStudySessions() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val sessions = studySessionRepository.getFutureStudySessions()
                _futureStudySessions.value = sessions
            } catch (e: Exception) {
                Log.e("StudySessionViewModel", "Error loading future sessions: ${e.message}")
                _error.value = "Failed to load future sessions"
                _futureStudySessions.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadJoinedStudySessions() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val currentUser = authRepository.getCurrentUser()
                if (currentUser != null) {
                    val sessions = studySessionRepository.getJoinedStudySessions(currentUser.id)
                    _joinedStudySessions.value = sessions
                }
            } catch (e: Exception) {
                Log.e("StudySessionViewModel", "Error loading joined sessions: ${e.message}")
                _error.value = "Failed to load joined sessions"
                _joinedStudySessions.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadCreatedStudySessions() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val currentUser = authRepository.getCurrentUser()
                if (currentUser != null) {
                    val sessions = studySessionRepository.getCreatedStudySessions(currentUser.id)
                    _createdStudySessions.value = sessions
                }
            } catch (e: Exception) {
                Log.e("StudySessionViewModel", "Error loading created sessions: ${e.message}")
                _error.value = "Failed to load created sessions"
                _createdStudySessions.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createStudySession(
        name: String,
        description: String,
        groupId: Long?,
        visibility: Boolean,
        date: String,
        startTime: String,
        duration: Long,
        tagId: Long?,
        sessionLink: String?
    ) {
        viewModelScope.launch {
            _isCreating.value = true
            _error.value = null
            try {
                val currentUser = authRepository.getCurrentUser()
                if (currentUser != null) {
                    val request = CreateStudySessionRequest(
                        name = name,
                        description = description,
                        group_id = groupId,
                        visibility = visibility,
                        date = date,
                        startTime = startTime,
                        duration = duration,
                        tag = tagId,
                        session_link = sessionLink
                    )
                    val session = studySessionRepository.createStudySession(request, currentUser.id)
                    if (session != null) {
                        _createdSession.value = session
                        // Refresh the sessions after creating a new one
                        loadFutureStudySessions()
                        loadCreatedStudySessions()
                    } else {
                        _error.value = "Failed to create study session"
                    }
                } else {
                    _error.value = "User not authenticated"
                }
            } catch (e: Exception) {
                Log.e("StudySessionViewModel", "Error creating study session: ${e.message}")
                _error.value = e.message ?: "Failed to create study session"
            } finally {
                _isCreating.value = false
            }
        }
    }

    fun loadStudySessionDetails(sessionId: String) {
        viewModelScope.launch {
            _isLoadingDetails.value = true
            _error.value = null
            try {
                val currentUser = authRepository.getCurrentUser()
                val session = studySessionRepository.getStudySessionDetails(sessionId, currentUser?.id)
                _selectedStudySession.value = session
                if (currentUser != null && session != null) {
                    _isUserParticipating.value = studySessionRepository.isUserJoined(sessionId, currentUser.id)
                } else {
                    _isUserParticipating.value = false
                }
            } catch (e: Exception) {
                Log.e("StudySessionViewModel", "Error loading session details: ${e.message}")
                _error.value = "Failed to load session details"
                _selectedStudySession.value = null
                _isUserParticipating.value = false
            } finally {
                _isLoadingDetails.value = false
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun joinSession(sessionId: String) {
        viewModelScope.launch {
            _isLoadingDetails.value = true // Show loading while joining
            _error.value = null
            try {
                val currentUser = authRepository.getCurrentUser()
                if (currentUser != null) {
                    // Convert sessionId to Long for the repository call, if repository still expects Long
                    val success = studySessionRepository.joinStudySession(sessionId.toLong(), currentUser.id)
                    if (success) {
                        loadStudySessionDetails(sessionId) // Refresh details
                        // Also refresh joined sessions list on main screen
                        loadJoinedStudySessions()
                    } else {
                        _error.value = "Failed to join session"
                    }
                } else {
                    _error.value = "User not authenticated"
                }
            } catch (e: Exception) {
                Log.e("StudySessionViewModel", "Error joining session: ${e.message}")
                _error.value = e.message ?: "Failed to join session"
            } finally {
                _isLoadingDetails.value = false
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun leaveSession(sessionId: String) {
        viewModelScope.launch {
            _isLoadingDetails.value = true // Show loading while leaving
            _error.value = null
            try {
                val currentUser = authRepository.getCurrentUser()
                if (currentUser != null) {
                    // Convert sessionId to Long for the repository call, if repository still expects Long
                    val success = studySessionRepository.leaveStudySession(sessionId.toLong(), currentUser.id)
                    if (success) {
                        loadStudySessionDetails(sessionId) // Refresh details
                        // Also refresh joined sessions list on main screen
                        loadJoinedStudySessions()
                    } else {
                        _error.value = "Failed to leave session"
                    }
                } else {
                    _error.value = "User not authenticated"
                }
            } catch (e: Exception) {
                Log.e("StudySessionViewModel", "Error leaving session: ${e.message}")
                _error.value = e.message ?: "Failed to leave session"
            } finally {
                _isLoadingDetails.value = false
            }
        }
    }
} 