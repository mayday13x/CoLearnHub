package com.example.colearnhub.modelLayer


import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class Country(
    @Contextual
    val id: Int? = null,
    val country: String? = null
)
