package com.example.colearnhub.modelLayer

import kotlinx.serialization.Serializable

@Serializable
data class RoleData(
    val id: Long,
    val role: String
)