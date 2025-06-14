package com.example.colearnhub.modelLayer

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import com.example.colearnhub.modelLayer.TagData

@Serializable
data class StudySession(
    val id: Long,
    val name: String,
    val description: String,
    @SerialName("creator_id")
    val creatorId: String,
    @SerialName("group_id")
    val groupId: Long?,
    val visibility: Boolean,
    @SerialName("created_at")
    val createdAt: String,
    val date: String,
    @SerialName("start_time")
    val startTime: String,
    val duration: Long,
    @SerialName("Tags")
    val embeddedTag: TagData?
)

@Serializable
data class CreateStudySessionRequest(
    val name: String,
    val description: String,
    @SerialName("group_id")
    val group_id: Long?,
    val visibility: Boolean,
    val date: String,
    @SerialName("start_time")
    val startTime: String,
    val duration: Long,
    val tag: Long?
)

@Serializable
data class StudySessionInsert(
    val name: String,
    val description: String,
    @SerialName("creator_id")
    val creator_id: String,
    @SerialName("group_id")
    val group_id: Long?,
    val visibility: Boolean,
    val date: String,
    @SerialName("start_time")
    val startTime: String,
    val duration: Long,
    val tag: Long?
) 