package com.example.colearnhub.modelLayer

import kotlinx.serialization.Serializable

@Serializable
data class TagData(
    val id: Long,
    val description: String,
    val color: String? = null
)