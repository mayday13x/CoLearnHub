package com.example.colearnhub.modelLayer

import kotlinx.serialization.Serializable

@Serializable
data class Material(
    val id: Long,
    val title: String,
    val description: String? = null,
    val file_url: String? = null,
    val visibility: Boolean? = null,
    val language: Long? = null,
    val author_id: String? = null,
    val created_at: String? = null,
    // Adding tags list to handle many-to-many relationship
    val tags: List<TagData>? = null
)

@Serializable
data class CreateMaterialRequest(
    val title: String,
    val description: String? = null,
    val file_url: String? = null,
    val visibility: Boolean = true,
    val language: Long? = null,
    val author_id: String? = null,
    val tag_ids: List<Long>? = null // Changed from tag_id to tag_ids for multiple tags
)

@Serializable
data class MaterialTag(
    val tag_id: Long,
    val material_id: Long
)