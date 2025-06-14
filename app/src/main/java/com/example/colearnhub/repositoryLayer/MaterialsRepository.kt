package com.example.colearnhub.repositoryLayer

import android.util.Log
import com.example.colearnhub.modelLayer.CreateMaterialRequest
import com.example.colearnhub.modelLayer.Material
import com.example.colearnhub.modelLayer.MaterialTag
import com.example.colearnhub.modelLayer.TagData
import com.example.colearnhub.modelLayer.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinInstant
import java.time.LocalDateTime
import java.time.ZoneOffset

class MaterialsRepository {

    private val ratingRepository = RatingRepository()

    private suspend fun addAverageRatingToMaterial(material: Material): Material {
        val averageRating = material.id?.let { ratingRepository.getAverageRating(it) }
        return material.copy(average_rating = averageRating)
    }

    /**
     * Cria um novo material
     */
    suspend fun createMaterial(
        title: String,
        description: String? = null,
        file_url: String? = null,
        visibility: Boolean = true,
        language: Long? = null,
        author_id: String? = null,
        tagIds: List<Long>? = null
    ): Material? = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.d("MaterialsRepository", "Creating material with title: $title")
            
            // First create the material
            val material = SupabaseClient.client
                .from("Materials")
                .insert(CreateMaterialRequest(
                    title = title,
                    description = description,
                    file_url = file_url,
                    visibility = visibility,
                    language = language,
                    author_id = author_id
                )) {
                    select()
                }
                .decodeSingle<Material>()

            // If we have tags, create the Material_Tag relationships
            if (!tagIds.isNullOrEmpty()) {
                Log.d("MaterialsRepository", "Creating tag relationships for material ${material.id}")
                
                val materialTags = tagIds.map { tagId ->
                    MaterialTag(
                        tag_id = tagId,
                        material_id = material.id
                    )
                }

                SupabaseClient.client
                    .from("Material_Tag")
                    .insert(materialTags)

                Log.d("MaterialsRepository", "Created ${materialTags.size} tag relationships")
            }

            // Fetch the complete material with tags and then add average rating
            material.id?.let { id ->
                val materialWithTags = getMaterialByIdWithTags(id.toString())
                materialWithTags?.let { addAverageRatingToMaterial(it) }
            } ?: material

        } catch (e: Exception) {
            Log.e("MaterialsRepository", "Error creating material: ${e.message}")
            null
        }
    }

    /**
     * Busca um material por ID (versão simples)
     */
    suspend fun getMaterialById(materialId: Long): Material? = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.d("MaterialRepository", "Buscando material ID: $materialId")

            val result = SupabaseClient.client
                .from("Materials")
                .select {
                    filter {
                        eq("id", materialId)
                    }
                }
                .decodeSingleOrNull<Material>()

            if (result != null) {
                Log.d("MaterialRepository", "Material encontrado:")
                Log.d("MaterialRepository", "  - Title: ${result.title}")
                Log.d("MaterialRepository", "  - Visibility: ${result.visibility}")
                Log.d("MaterialRepository", "  - Author: ${result.author_id}")
                Log.d("MaterialRepository", "  - Language: ${result.language}")
                addAverageRatingToMaterial(result)
            } else {
                Log.w("MaterialRepository", "Material $materialId não encontrado")
                null
            }

        } catch (e: Exception) {
            Log.e("MaterialRepository", "Erro ao buscar material: ${e.message}")
            null
        }
    }

    /**
     * Busca um material por ID com suas tags
     */
    suspend fun getMaterialByIdWithTags(materialId: String): Material? = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.d("MaterialsRepository", "Fetching material with ID: $materialId")

            val material = SupabaseClient.client
                .from("Materials")
                .select {
                    filter {
                        eq("id", materialId.toLong())
                    }
                }
                .decodeSingle<Material>()

            // Fetch tags for this material
            val tags = SupabaseClient.client
                .from("Material_Tag")
                .select {
                    filter {
                        eq("material_id", materialId.toLong())
                    }
                }
                .decodeList<MaterialTag>()

            // Fetch tag details
            val tagDetails = tags.mapNotNull { materialTag ->
                SupabaseClient.client
                    .from("Tags")
                    .select {
                        filter {
                            eq("id", materialTag.tag_id)
                        }
                    }
                    .decodeSingleOrNull<com.example.colearnhub.modelLayer.TagData>()
            }

            val materialWithTags = material.copy(tags = tagDetails)
            addAverageRatingToMaterial(materialWithTags)
        } catch (e: Exception) {
            Log.e("MaterialsRepository", "Error fetching material: ${e.message}")
            null
        }
    }

    /**
     * Busca materiais públicos (visibility = true) com filtros
     */
    suspend fun getPublicMaterials(
        searchQuery: String? = null,
        startDate: LocalDateTime? = null,
        endDate: LocalDateTime? = null,
        tagIds: List<Long>? = null
    ): List<Material> = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.d("MaterialRepository", "Buscando materiais públicos com filtros. Query: $searchQuery, StartDate: $startDate, EndDate: $endDate, Tags: $tagIds")

            val materials = SupabaseClient.client
                .from("Materials")
                .select {
                    filter {
                        eq("visibility", true)

                        if (!searchQuery.isNullOrBlank()) {
                            or {
                                ilike("title", "%$searchQuery%")
                                ilike("description", "%$searchQuery%")
                            }
                        }

                        startDate?.let {
                            gte("created_at", it.toInstant(ZoneOffset.UTC).toString())
                        }
                        endDate?.let {
                            lte("created_at", it.toInstant(ZoneOffset.UTC).toString())
                        }
                    }
                }
                .decodeList<Material>()

            Log.d("MaterialRepository", "Encontrados ${materials.size} materiais públicos (antes de filtrar por tags)")

            val materialsWithTags = materials.map { material ->
                val tags = getTagsByMaterialId(material.id)
                addAverageRatingToMaterial(material.copy(tags = tags))
            }

            // Client-side filtering for tags if provided
            if (!tagIds.isNullOrEmpty()) {
                materialsWithTags.filter { material ->
                    material.tags?.any { tag -> tagIds.contains(tag.id) } == true
                }
            } else {
                materialsWithTags
            }

        } catch (e: Exception) {
            Log.e("MaterialRepository", "Erro ao buscar materiais públicos: ${e.message}")
            emptyList()
        }
    }

    /**
     * Pesquisa materiais por título
     * DEPRECATED: Use getPublicMaterials with searchQuery
     */
    @Deprecated("Use getPublicMaterials with searchQuery parameter instead")
    suspend fun searchMaterialsByTitle(query: String): List<Material> = emptyList()

    /**
     * Atualiza um material
     */
    suspend fun updateMaterial(
        materialId: Long,
        title: String? = null,
        description: String? = null,
        visibility: Boolean? = null,
        language: Long? = null
    ): Material? = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.d("MaterialRepository", "Atualizando material ID: $materialId")

            val updateData = mutableMapOf<String, Any?>()
            title?.let { updateData["title"] = it }
            description?.let { updateData["description"] = it }
            visibility?.let { updateData["visibility"] = it }
            language?.let { updateData["language"] = it }

            if (updateData.isEmpty()) {
                Log.w("MaterialRepository", "Nenhum campo para atualizar")
                return@withContext getMaterialByIdWithTags(materialId.toString())
            }

            val result = SupabaseClient.client
                .from("Materials")
                .update(updateData) {
                    filter {
                        eq("id", materialId)
                    }
                    select()
                }
                .decodeSingle<Material>()

            Log.d("MaterialRepository", "Material atualizado: ${result.id}")

            // Retornar o material com tags e average rating
            result.id?.let { id -> getMaterialByIdWithTags(id.toString()) } ?: result

        } catch (e: Exception) {
            Log.e("MaterialRepository", "Erro ao atualizar: ${e.message}")
            null
        }
    }

    /**
     * Obtém materiais por autor com filtros
     */
    suspend fun getMaterialsByAuthor(
        authorId: String,
        searchQuery: String? = null,
        startDate: LocalDateTime? = null,
        endDate: LocalDateTime? = null,
        tagIds: List<Long>? = null
    ): List<Material> = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.d("MaterialsRepository", "getMaterialsByAuthor: authorId = $authorId, Query: $searchQuery, StartDate: $startDate, EndDate: $endDate, Tags: $tagIds")
            val materials = SupabaseClient.client
                .from("Materials")
                .select {
                    filter {
                        eq("author_id", authorId)

                        if (!searchQuery.isNullOrBlank()) {
                            or {
                                ilike("title", "%$searchQuery%")
                                ilike("description", "%$searchQuery%")
                            }
                        }

                        startDate?.let {
                            gte("created_at", it.toInstant(ZoneOffset.UTC).toString())
                        }
                        endDate?.let {
                            lte("created_at", it.toInstant(ZoneOffset.UTC).toString())
                        }
                    }
                }
                .decodeList<Material>()

            Log.d("MaterialsRepository", "getMaterialsByAuthor: response size = ${materials.size} (antes de filtrar por tags)")

            // Carregar tags e average rating para cada material
            val materialsWithTags = materials.map { material ->
                val tags = getTagsByMaterialId(material.id)
                addAverageRatingToMaterial(material.copy(tags = tags))
            }

            // Client-side filtering for tags if provided
            if (!tagIds.isNullOrEmpty()) {
                materialsWithTags.filter { material ->
                    material.tags?.any { tag -> tagIds.contains(tag.id) } == true
                }
            } else {
                materialsWithTags
            }

        } catch (e: Exception) {
            Log.e("MaterialsRepository", "Erro ao obter materiais por autor: ${e.message}")
            emptyList()
        }
    }

    /**
     * Busca materiais por tag
     * DEPRECATED: Use getPublicMaterials or getMaterialsByAuthor with tagIds
     */
    @Deprecated("Use getPublicMaterials or getMaterialsByAuthor with tagIds parameter instead")
    suspend fun getMaterialsByTag(tagId: Long): List<Material> = emptyList()

    /**
     * Elimina um material
     */
    suspend fun deleteMaterial(materialId: Long): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.d("MaterialRepository", "Eliminando material ID: $materialId")

            // Primeiro, eliminar as relações com tags na tabela Material_Tag
            SupabaseClient.client
                .from("Material_Tag")
                .delete {
                    filter {
                        eq("material_id", materialId)
                    }
                }
            Log.d("MaterialRepository", "Relações de tags eliminadas")

            // Depois, eliminar o material
            SupabaseClient.client
                .from("Materials")
                .delete {
                    filter {
                        eq("id", materialId)
                    }
                }
            Log.d("MaterialRepository", "Material eliminado com sucesso")

            true
        } catch (e: Exception) {
            Log.e("MaterialRepository", "Erro ao eliminar material: ${e.message}")
            false
        }
    }

    /**
     * Busca materiais por idioma
     * DEPRECATED: Filtering by language will be handled in MaterialViewModel
     */
    @Deprecated("Filtering by language will be handled in MaterialViewModel")
    suspend fun getMaterialsByLanguage(languageId: Long): List<Material> = emptyList()

    /**
     * Busca as tags de um material específico
     */
    private suspend fun getTagsByMaterialId(materialId: Long?): List<TagData> {
        if (materialId == null) return emptyList()
        return try {
            // Buscar os registros da tabela Material_Tag
            val materialTagResults = SupabaseClient.client
                .from("Material_Tag")
                .select() {
                    filter {
                        eq("material_id", materialId)
                    }
                }
                .decodeList<MaterialTag>()

            val tagIds = materialTagResults.map { it.tag_id }

            if (tagIds.isEmpty()) {
                return emptyList()
            }

            // Buscar as tags propriamente ditas
            SupabaseClient.client
                .from("Tags")
                .select {
                    filter {
                        isIn("id", tagIds)
                    }
                }
                .decodeList<TagData>()
        } catch (e: Exception) {
            Log.e("MaterialRepository", "Erro ao buscar tags do material $materialId: ${e.message}")
            emptyList()
        }
    }

    /**
     * Adiciona uma tag a um material
     */
    suspend fun addTagToMaterial(materialId: Long, tagId: Long): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            SupabaseClient.client
                .from("Material_Tag")
                .insert(mapOf(
                    "material_id" to materialId,
                    "tag_id" to tagId
                ))

            Log.d("MaterialRepository", "Tag $tagId adicionada ao material $materialId")
            true
        } catch (e: Exception) {
            Log.e("MaterialRepository", "Erro ao adicionar tag: ${e.message}")
            false
        }
    }

    /**
     * Remove uma tag de um material
     */
    suspend fun removeTagFromMaterial(materialId: Long, tagId: Long): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            SupabaseClient.client
                .from("Material_Tag")
                .delete {
                    filter {
                        eq("material_id", materialId)
                        eq("tag_id", tagId)
                    }
                }

            Log.d("MaterialRepository", "Tag $tagId removida do material $materialId")
            true
        } catch (e: Exception) {
            Log.e("MaterialRepository", "Erro ao remover tag: ${e.message}")
            false
        }
    }
}