package com.example.colearnhub.modelLayer

import kotlinx.serialization.Serializable

@Serializable
data class FavouritesData(
    val user_id: String,
    val material_id: Long
) 