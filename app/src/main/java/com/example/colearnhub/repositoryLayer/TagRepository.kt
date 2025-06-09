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
}
