package com.example.colearnhub.repositoryLayer

import android.util.Log
import com.example.colearnhub.modelLayer.TagData
import com.example.colearnhub.modelLayer.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TagRepository {

    /**
     * Get tag by ID
     */
    suspend fun getTagById(tagId: Long): TagData? = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.d("TagRepository", "Buscando tag ID: $tagId")

            val result = SupabaseClient.client
                .from("Tags")
                .select {
                    filter {
                        eq("id", tagId)
                    }
                }
                .decodeSingleOrNull<TagData>()

            if (result != null) {
                Log.d("TagRepository", "Tag encontrada: ${result.description}")
            } else {
                Log.w("TagRepository", "Tag $tagId não encontrada")
            }

            result
        } catch (e: Exception) {
            Log.e("TagRepository", "Erro ao buscar tag: ${e.message}")
            null
        }
    }

    /**
     * Get all tags
     */
    suspend fun getAllTags(): List<TagData> = withContext(Dispatchers.IO) {
        return@withContext try {
            val result = SupabaseClient.client
                .from("Tags")
                .select()
                .decodeList<TagData>()

            Log.d("TagRepository", "Encontradas ${result.size} tags")
            result
        } catch (e: Exception) {
            Log.e("TagRepository", "Erro ao buscar tags: ${e.message}")
            emptyList()
        }
    }

    /**
     * Converte lista de nomes de tags para lista de IDs
     */
    suspend fun getTagIdsByNames(tagNames: List<String>): List<Long> = withContext(Dispatchers.IO) {
        return@withContext try {
            if (tagNames.isEmpty()) {
                Log.d("TagRepository", "Lista de nomes de tags vazia")
                return@withContext emptyList()
            }

            Log.d("TagRepository", "Convertendo nomes de tags para IDs: $tagNames")

            val result = SupabaseClient.client
                .from("Tags")
                .select {
                    filter {
                        isIn("description", tagNames)
                    }
                }
                .decodeList<TagData>()

            val tagIds = result.map { it.id }
            Log.d("TagRepository", "IDs encontrados: $tagIds")

            // Verificar se todas as tags foram encontradas
            val foundNames = result.map { it.description }
            val missingNames = tagNames - foundNames.toSet()
            if (missingNames.isNotEmpty()) {
                Log.w("TagRepository", "Tags não encontradas: $missingNames")
            }

            tagIds
        } catch (e: Exception) {
            Log.e("TagRepository", "Erro ao converter nomes de tags para IDs: ${e.message}")
            emptyList()
        }
    }

    /**
     * Busca tag por nome exato
     */
    suspend fun getTagByName(tagName: String): TagData? = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.d("TagRepository", "Buscando tag por nome: $tagName")

            val result = SupabaseClient.client
                .from("Tags")
                .select {
                    filter {
                        eq("description", tagName)
                    }
                }
                .decodeSingleOrNull<TagData>()

            if (result != null) {
                Log.d("TagRepository", "Tag encontrada: ID ${result.id}")
            } else {
                Log.w("TagRepository", "Tag '$tagName' não encontrada")
            }

            result
        } catch (e: Exception) {
            Log.e("TagRepository", "Erro ao buscar tag por nome: ${e.message}")
            null
        }
    }

    /**
     * Cria uma nova tag se não existir
     */
    suspend fun createTagIfNotExists(tagName: String): TagData? = withContext(Dispatchers.IO) {
        return@withContext try {
            // Primeiro verificar se já existe
            val existingTag = getTagByName(tagName)
            if (existingTag != null) {
                Log.d("TagRepository", "Tag '$tagName' já existe com ID ${existingTag.id}")
                return@withContext existingTag
            }

            // Criar nova tag
            Log.d("TagRepository", "Criando nova tag: $tagName")
            val newTag = SupabaseClient.client
                .from("Tags")
                .insert(mapOf("description" to tagName)) {
                    select()
                }
                .decodeSingle<TagData>()

            Log.d("TagRepository", "Nova tag criada: ID ${newTag.id}")
            newTag
        } catch (e: Exception) {
            Log.e("TagRepository", "Erro ao criar tag: ${e.message}")
            null
        }
    }
}