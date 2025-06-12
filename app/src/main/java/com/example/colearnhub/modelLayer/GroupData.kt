package com.example.colearnhub.modelLayer

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import java.time.LocalDateTime
import java.util.UUID

@Serializable
data class Groups(
    @Contextual
    val id: Long? = null,
    val name: String? = null,
    val description: String? = null,
    @SerialName("owner_id")
    @Contextual
    val owner_id: String? = null,
    @SerialName("group_picture")
    val group_picture: String? = null,
    @SerialName("created_at")
    @Contextual
    val create_at: String? = null
)

@Serializable
data class Group_Members(
    @SerialName("user_id")
    @Contextual
    val user_id: String,
    @SerialName("group_id")
    @Contextual
    val group_id: Long,
    @SerialName("joined_at")
    @Contextual
    val joined_at: String? = null,
    val accept: Boolean? = null
)

@Serializable
data class CreateGroupRequest(
    val name: String,
    val description: String? = null,
    val invitedUserIds: List<String> = emptyList()
)

@Serializable
data class GroupResponse(
    val group: Groups,
    val members: List<Group_Members> = emptyList()
)

@Serializable
data class Users(
    @Contextual
    val id: String,
    val username: String? = null,
    val email: String? = null,
    @SerialName("profile_picture")
    val profilePicture: String? = null
)