package com.example.colearnhub.viewModelLayer

import android.os.Build
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

    private val _userGroups = MutableStateFlow<List<GroupResponse>>(emptyList())
    val userGroups: StateFlow<List<GroupResponse>> = _userGroups.asStateFlow()

    private val _createdSession = MutableStateFlow<StudySession?>(null)
    val createdSession: StateFlow<StudySession?> = _createdSession.asStateFlow()

    init {
        loadUserGroups()
    }

    private fun loadUserGroups() {
        viewModelScope.launch {
            val currentUser = authRepository.getCurrentUser()
            if (currentUser != null) {
                _userGroups.value = groupRepository.getUserAcceptedGroups(currentUser.id)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createStudySession(
        name : String,
        description: String,
        groupId: Long?,
        visibility: Boolean,
        date: String,
        startTime: String,
        duration: Long,
        tagId: Long?
    ) {
        viewModelScope.launch {
            _isCreating.value = true
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
                        tag = tagId
                    )
                    _createdSession.value = studySessionRepository.createStudySession(request, currentUser.id)
                }
            } finally {
                _isCreating.value = false
            }
        }
    }
} 