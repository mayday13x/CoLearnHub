package com.example.colearnhub.modelLayer

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Serializable
data class StudySession(
    @Contextual
    val id: Long? = null,
    val description: String? = null,
    @SerialName("creator_id")
    @Contextual
    val creatorId: String,
    @SerialName("group_id")
    @Contextual
    val groupId: Long? = null,
    val visibility: Boolean = true,
    @SerialName("created_at")
    @Contextual
    val createdAt: String? = null,
    val date: String,
    @SerialName("start_time")
    @Contextual
    val startTime: String,
    val duration: Long,
    @SerialName("tag_id")
    @Contextual
    val tagId: Long? = null
)

@Serializable
data class CreateStudySessionRequest(
    val description: String? = null,
    val groupId: Long? = null,
    val visibility: Boolean = true,
    val date: String,
    val startTime: String,
    val duration: Long,
    val tagId: Long? = null
) 