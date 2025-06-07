package com.example.colearnhub.modelLayer

import kotlinx.serialization.Serializable

@Serializable
data class LanguageData(
    val id: Long,
    val language: String,
)