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

    private val _isUserInGroup = MutableStateFlow(false)
    val isUserInGroup: StateFlow<Boolean> = _isUserInGroup.asStateFlow()

    private val _isUserOwner = MutableStateFlow(false)
    val isUserOwner: StateFlow<Boolean> = _isUserOwner.asStateFlow()

    private val _displayParticipantsCount = MutableStateFlow(0)
    val displayParticipantsCount: StateFlow<Int> = _displayParticipantsCount.asStateFlow()

    private val _isRemoving = MutableStateFlow(false)
    val isRemoving: StateFlow<Boolean> = _isRemoving.asStateFlow()

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
                    
                    // Debug logs for owner check
                    Log.d("StudySessionViewModel", "Current user ID: ${currentUser.id}")
                    Log.d("StudySessionViewModel", "Session creator ID: ${session.creatorId}")
                    _isUserOwner.value = session.creatorId == currentUser.id
                    Log.d("StudySessionViewModel", "Is user owner: ${_isUserOwner.value}")
                    
                    // Check if user is in the same group as the session
                    if (session.groupId != null) {
                        val userGroups = groupRepository.getUserAcceptedGroups(currentUser.id)
                        _isUserInGroup.value = userGroups.any { it.group.id == session.groupId }

                        // Calculate combined participant count
                        val uniqueParticipants = mutableSetOf<String>()
                        uniqueParticipants.add(session.creatorId) // Add owner
                        session.sessionParticipants?.forEach { participant ->
                            uniqueParticipants.add(participant.userId)
                        }

                        val groupDetails = groupRepository.getGroupDetails(session.groupId) // Fetch group details
                        groupDetails?.members?.filter { it.accept == true }?.forEach { member ->
                            uniqueParticipants.add(member.user_id)
                        }
                        _displayParticipantsCount.value = uniqueParticipants.size

                    } else {
                        _isUserInGroup.value = false
                        // Calculate participant count without group members
                        val uniqueParticipants = mutableSetOf<String>()
                        uniqueParticipants.add(session.creatorId) // Add owner
                        session.sessionParticipants?.forEach { participant ->
                            uniqueParticipants.add(participant.userId)
                        }
                        _displayParticipantsCount.value = uniqueParticipants.size
                    }
                } else {
                    _isUserParticipating.value = false
                    _isUserOwner.value = false
                    _isUserInGroup.value = false
                    _displayParticipantsCount.value = 0
                }
            } catch (e: Exception) {
                Log.e("StudySessionViewModel", "Error loading session details: ${e.message}")
                _error.value = "Failed to load session details"
                _selectedStudySession.value = null
                _isUserParticipating.value = false
                _isUserOwner.value = false
                _isUserInGroup.value = false
                _displayParticipantsCount.value = 0
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
                    Log.d("StudySessionViewModel", "Attempting to leave session. Session ID: $sessionId, User ID: ${currentUser.id}")
                    // Convert sessionId to Long for the repository call, if repository still expects Long
                    val success = studySessionRepository.leaveStudySession(sessionId.toLong(), currentUser.id)
                    if (success) {
                        Log.d("StudySessionViewModel", "Successfully left session. Refreshing details.")
                        loadStudySessionDetails(sessionId) // Refresh details
                        // Also refresh joined sessions list on main screen
                        loadJoinedStudySessions()
                    } else {
                        Log.e("StudySessionViewModel", "Failed to leave session from repository.")
                        _error.value = "Failed to leave session"
                    }
                } else {
                    Log.e("StudySessionViewModel", "User not authenticated to leave session.")
                    _error.value = "User not authenticated"
                }
            } catch (e: Exception) {
                Log.e("StudySessionViewModel", "Error leaving session: ${e.message}")
                _error.value = e.message ?: "Failed to leave session"
            } finally {
                _isLoadingDetails.value = false
                Log.d("StudySessionViewModel", "Finished leaving session operation. isLoadingDetails: ${_isLoadingDetails.value}")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun removeStudySession(sessionId: String) {
        viewModelScope.launch {
            _isRemoving.value = true
            _error.value = null
            try {
                val success = studySessionRepository.deleteStudySession(sessionId.toLong()) // Convert to Long for repo
                if (success) {
                    Log.d("StudySessionViewModel", "Successfully removed session: $sessionId")
                    // Optionally, navigate back or refresh a list after successful removal
                } else {
                    _error.value = "Failed to remove session"
                }
            } catch (e: Exception) {
                Log.e("StudySessionViewModel", "Error removing session: ${e.message}")
                _error.value = e.message ?: "Failed to remove session"
            } finally {
                _isRemoving.value = false
            }
        }
    }
} 