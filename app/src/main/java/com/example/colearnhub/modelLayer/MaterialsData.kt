package com.example.colearnhub.modelLayer

import kotlinx.serialization.Serializable


@Serializable
data class Material(
    val id: Long? = null,
    val title: String,
    val description: String? = null,
    val file_url: String? = null,
    val visibility: Boolean = true,
    val language: Long? = null,
    val author_id: Long? = null,
    val created_at: String? = null,
    val tag_id: Long? = null
)

//CreateMaterialRequest com defaults corretos
@Serializable
data class CreateMaterialRequest(
    val title: String,
    val description: String? = null,
    val file_url: String? = null,
    val visibility: Boolean = true,
    val language: Long? = null,
    val author_id: Long? = null,
    val tag_id: Long? = null
    //created_at não vai aqui porque é auto-gerado pela BD
)