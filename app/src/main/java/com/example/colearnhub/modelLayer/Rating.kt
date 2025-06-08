package com.example.colearnhub.modelLayer

import kotlinx.serialization.Serializable

@Serializable
data class Rating(
    val user_id: String,
    val material_id: Long,
    val rating: Long? = null,
    val created_at: String? = null
) 