package com.example.colearnhub.modelLayer

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class Comments(
    @Contextual
    val id: Int? = null,
    val user_id: String? = null,
    val material_id: String? = null,
    val content: String? = null,
    val created_at: String? = null,
    val response: Int? = null,
    @kotlinx.serialization.Transient
    val responses: List<Comments> = emptyList()
)
