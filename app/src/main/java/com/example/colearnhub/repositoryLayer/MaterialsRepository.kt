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

class MaterialsRepository {

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
            Log.d("MaterialRepository", "=== A criar material ===")
            Log.d("MaterialRepository", "Title: '$title'")
            Log.d("MaterialRepository", "Author ID: $author_id")
            Log.d("MaterialRepository", "Visibility: $visibility")
            Log.d("MaterialRepository", "Language ID: $language")
            Log.d("MaterialRepository", "Tag IDs: $tagIds")

            // Criar o material primeiro
            val materialData = CreateMaterialRequest(
                title = title,
                description = description,
                file_url = file_url,
                visibility = visibility,
                language = language,
                author_id = author_id,
                tag_ids = tagIds
            )

            val result = SupabaseClient.client
                .from("Materials")
                .insert(materialData) {
                    select()
                }
                .decodeSingle<Material>()

            // Se temos tags, criar as relações na tabela Material_Tag
            if (tagIds != null && tagIds.isNotEmpty()) {
                tagIds.forEach { tagId ->
                    try {
                        SupabaseClient.client
                            .from("Material_Tag")
                            .insert(mapOf(
                                "material_id" to result.id,
                                "tag_id" to tagId
                            ))
                        Log.d("MaterialRepository", "Tag $tagId associada ao material ${result.id}")
                    } catch (e: Exception) {
                        Log.e("MaterialRepository", "Erro ao associar tag $tagId: ${e.message}")
                    }
                }
            }

            Log.d("MaterialRepository", "Material criado com sucesso: ID ${result.id}")

            // Retornar o material com as tags carregadas
            getMaterialByIdWithTags(result.id)
        } catch (e: Exception) {
            Log.e("MaterialRepository", "Erro ao criar material: ${e.message}")
            e.printStackTrace()
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
            } else {
                Log.w("MaterialRepository", "Material $materialId não encontrado")
            }

            result
        } catch (e: Exception) {
            Log.e("MaterialRepository", "Erro ao buscar material: ${e.message}")
            null
        }
    }

    /**
     * Busca um material por ID com suas tags
     */
    suspend fun getMaterialByIdWithTags(materialId: Long): Material? = withContext(Dispatchers.IO) {
        return@withContext try {
            val material = getMaterialById(materialId) ?: return@withContext null
            val tags = getTagsByMaterialId(materialId)

            material.copy(tags = tags)
        } catch (e: Exception) {
            Log.e("MaterialRepository", "Erro ao buscar material com tags: ${e.message}")
            null
        }
    }

    /**
     * Busca materiais públicos (visibility = true)
     */
    suspend fun getPublicMaterials(): List<Material> = withContext(Dispatchers.IO) {
        return@withContext try {
            val materials = SupabaseClient.client
                .from("Materials")
                .select {
                    filter {
                        eq("visibility", true)
                    }
                }
                .decodeList<Material>()

            Log.d("MaterialRepository", "Encontrados ${materials.size} materiais públicos")

            // Carregar tags para cada material
            materials.map { material ->
                val tags = getTagsByMaterialId(material.id)
                material.copy(tags = tags)
            }
        } catch (e: Exception) {
            Log.e("MaterialRepository", "Erro ao buscar materiais públicos: ${e.message}")
            emptyList()
        }
    }

    /**
     * Pesquisa materiais por título
     */
    suspend fun searchMaterialsByTitle(query: String): List<Material> = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.d("MaterialsRepository", "searchMaterialsByTitle: query = $query")
            val materials = SupabaseClient.client
                .from("Materials")
                .select {
                    filter {
                        ilike("title", "%$query%")
                    }
                }
                .decodeList<Material>()

            Log.d("MaterialsRepository", "searchMaterialsByTitle: response size = ${materials.size}")

            // Carregar tags para cada material
            materials.map { material ->
                val tags = getTagsByMaterialId(material.id)
                material.copy(tags = tags)
            }
        } catch (e: Exception) {
            Log.e("MaterialsRepository", "Erro ao pesquisar materiais: ${e.message}")
            emptyList()
        }
    }

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
                return@withContext getMaterialByIdWithTags(materialId)
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

            // Retornar o material com tags
            getMaterialByIdWithTags(result.id)
        } catch (e: Exception) {
            Log.e("MaterialRepository", "Erro ao atualizar: ${e.message}")
            null
        }
    }

    /**
     * Obtém materiais por autor
     */
    suspend fun getMaterialsByAuthor(authorId: String): List<Material> = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.d("MaterialsRepository", "getMaterialsByAuthor: authorId = $authorId")
            val materials = SupabaseClient.client
                .from("Materials")
                .select {
                    filter {
                        eq("author_id", authorId)
                    }
                }
                .decodeList<Material>()

            Log.d("MaterialsRepository", "getMaterialsByAuthor: response size = ${materials.size}")

            // Carregar tags para cada material
            materials.map { material ->
                val tags = getTagsByMaterialId(material.id)
                material.copy(tags = tags)
            }
        } catch (e: Exception) {
            Log.e("MaterialsRepository", "Erro ao obter materiais por autor: ${e.message}")
            emptyList()
        }
    }

    /**
     * Busca materiais por tag
     */
    suspend fun getMaterialsByTag(tagId: Long): List<Material> = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.d("MaterialRepository", "Buscando materiais com tag ID: $tagId")

            // Primeiro buscar os IDs dos materiais na tabela Material_Tag
            val materialTagResults = SupabaseClient.client
                .from("Material_Tag")
                .select() {
                    filter {
                        eq("tag_id", tagId)
                    }
                }
                .decodeList<MaterialTag>()

            val materialIds = materialTagResults.map { it.material_id }

            if (materialIds.isEmpty()) {
                Log.d("MaterialRepository", "Nenhum material encontrado com a tag $tagId")
                return@withContext emptyList()
            }

            // Depois buscar os materiais propriamente ditos
            val materials = SupabaseClient.client
                .from("Materials")
                .select {
                    filter {
                        isIn("id", materialIds)
                        eq("visibility", true) // Apenas materiais públicos
                    }
                }
                .decodeList<Material>()

            Log.d("MaterialRepository", "Encontrados ${materials.size} materiais com a tag $tagId")

            // Carregar tags para cada material
            materials.map { material ->
                val tags = getTagsByMaterialId(material.id)
                material.copy(tags = tags)
            }
        } catch (e: Exception) {
            Log.e("MaterialRepository", "Erro ao buscar por tag: ${e.message}")
            emptyList()
        }
    }

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
     */
    suspend fun getMaterialsByLanguage(languageId: Long): List<Material> = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.d("MaterialRepository", "Buscando materiais no idioma ID: $languageId")

            val materials = SupabaseClient.client
                .from("Materials")
                .select {
                    filter {
                        eq("language", languageId)
                        eq("visibility", true)
                    }
                }
                .decodeList<Material>()

            Log.d("MaterialRepository", "Encontrados ${materials.size} materiais no idioma $languageId")

            // Carregar tags para cada material
            materials.map { material ->
                val tags = getTagsByMaterialId(material.id)
                material.copy(tags = tags)
            }
        } catch (e: Exception) {
            Log.e("MaterialRepository", "Erro ao buscar por idioma: ${e.message}")
            emptyList()
        }
    }

    /**
     * Busca as tags de um material específico
     */
    private suspend fun getTagsByMaterialId(materialId: Long): List<TagData> {
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