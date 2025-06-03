// ============== REPOSITORY CORRIGIDO ==============
package com.example.colearnhub.repositoryLayer

import android.util.Log
import com.example.colearnhub.modelLayer.SupabaseClient
import com.example.colearnhub.modelLayer.Material
import com.example.colearnhub.modelLayer.CreateMaterialRequest
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

class MaterialRepository {

    /**
     * Cria um novo material com defaults explícitos
     */
    suspend fun createMaterial(
        title: String,
        description: String? = null,
        fileUrl: String? = null,
        visibility: Boolean = true,
        languageId: Long? = null,
        authorId: Long? = null,
        tagId: Long? = null
    ): Material? = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.d("MaterialRepository", "=== CRIANDO MATERIAL ===")
            Log.d("MaterialRepository", "Title: '$title'")
            Log.d("MaterialRepository", "Visibility: $visibility")
            Log.d("MaterialRepository", "Language ID: $languageId")

            val materialRequest = CreateMaterialRequest(
                title = title,
                description = description,
                file_url = fileUrl,
                visibility = visibility,
                language = languageId,
                author_id = authorId,
                tag_id = tagId,
            )

            Log.d("MaterialRepository", "Request criado: $materialRequest")

            val result = SupabaseClient.client
                .from("Materials")
                .insert(materialRequest) {
                    select()
                }
                .decodeSingle<Material>()

            Log.d("MaterialRepository", "MATERIAL CRIADO:")
            Log.d("MaterialRepository", "  - ID: ${result.id}")
            Log.d("MaterialRepository", "  - Title: '${result.title}'")
            Log.d("MaterialRepository", "  - Visibility: ${result.visibility}") // Deve ser true para ser visivel
            Log.d("MaterialRepository", "  - Language: ${result.language}")

            result
        } catch (e: Exception) {
            Log.e("MaterialRepository", "ERRO ao criar material: ${e.message}")
            Log.e("MaterialRepository", "Detalhes do erro:", e)
            null
        }
    }

    /**
     * Get com filtros corretos
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
                Log.d("MaterialRepository", "  - Visibility: ${result.visibility}")
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
     * Busca materiais públicos (visibility = true)
     */
    suspend fun getPublicMaterials(): List<Material> = withContext(Dispatchers.IO) {
        return@withContext try {
            val result = SupabaseClient.client
                .from("Materials")
                .select {
                    filter {
                        eq("visibility", true)
                    }
                }
                .decodeList<Material>()

            Log.d("MaterialRepository", "Encontrados ${result.size} materiais públicos")
            result
        } catch (e: Exception) {
            Log.e("MaterialRepository", "Erro ao buscar materiais públicos: ${e.message}")
            emptyList()
        }
    }

    /**
     * Pesquisa materiais por título
     */
    suspend fun searchMaterialsByTitle(searchQuery: String): List<Material> = withContext(Dispatchers.IO) {
        return@withContext try {
            SupabaseClient.client
                .from("Materials")
                .select {
                    filter {
                        ilike("title", "%$searchQuery%")
                        eq("visibility", true)
                    }
                }
                .decodeList<Material>()
        } catch (e: Exception) {
            Log.e("MaterialRepository", "Erro na pesquisa: ${e.message}")
            emptyList()
        }
    }

    /**
     * Update com tipos corretos
     */
    suspend fun updateMaterial(
        materialId: Long,
        title: String? = null,
        description: String? = null,
        visibility: Boolean? = null
    ): Material? = withContext(Dispatchers.IO) {
        return@withContext try {
            @Serializable
            data class MaterialUpdate(
                val title: String? = null,
                val description: String? = null,
                val visibility: Boolean? = null
            )

            val updateObject = MaterialUpdate(
                title = title,
                description = description,
                visibility = visibility
            )

            val result = SupabaseClient.client
                .from("Materials")
                .update(updateObject) {
                    filter {
                        eq("id", materialId)
                    }
                    select()
                }
                .decodeSingle<Material>()

            Log.d("MaterialRepository", "Material atualizado: ${result.id}")
            result
        } catch (e: Exception) {
            Log.e("MaterialRepository", "Erro ao atualizar: ${e.message}")
            null
        }
    }

    /**
     * Obtém materiais por autor
     */
    suspend fun getMaterialsByAuthor(authorId: Long): List<Material> = withContext(Dispatchers.IO) {
        return@withContext try {
            SupabaseClient.client
                .from("Materials")
                .select {
                    filter {
                        eq("author_id", authorId)
                    }
                }
                .decodeList<Material>()
        } catch (e: Exception) {
            Log.e("MaterialRepository", "Erro ao buscar por autor: ${e.message}")
            emptyList()
        }
    }

    /**
     * Elimina um material
     */
    suspend fun deleteMaterial(materialId: Long): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            SupabaseClient.client
                .from("Materials")
                .delete {
                    filter {
                        eq("id", materialId)
                    }
                }
            true
        } catch (e: Exception) {
            Log.e("MaterialRepository", "Erro ao eliminar: ${e.message}")
            false
        }
    }
}